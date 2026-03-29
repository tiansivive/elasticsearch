/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.SubscribableListener;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.exchange.ExchangeService;
import org.elasticsearch.compute.operator.exchange.ExchangeSink;
import org.elasticsearch.compute.operator.exchange.ExchangeSource;
import org.elasticsearch.compute.operator.exchange.ExchangeSourceHandler;

import java.util.List;
import java.util.UUID;

/**
 * Builtins for exchange-based streaming of columnar {@link Page} data.
 * See D-054 for design rationale.
 *
 * <p>Uses ESQL's {@link ExchangeService} for exchange handler management.
 * {@code Exchange.open} creates a serializable descriptor. {@code Exchange.sink}
 * registers a sink handler via {@code ExchangeService.createSinkHandler}.
 * {@code Exchange.connect} creates an {@code ExchangeSourceHandler} and attaches
 * a local {@code RemoteSink} that fetches pages from the sink handler.
 */
final class EvalExchange {

    private EvalExchange() {}

    /**
     * {@code Exchange.open : List Keyword → Double → Exchange r}
     * Creates a serializable exchange descriptor. No infrastructure is created yet —
     * that happens when {@code Exchange.sink} or {@code Exchange.connect} is called.
     */
    static void open(List<Value> columnNameValues, double bufferSize, ActionListener<Value> listener) {
        try {
            var columnNames = columnNameValues.stream().map(v -> switch (v) {
                case Value.KeywordVal k -> k.value();
                default -> throw new AssertionError("type checker bug: expected Keyword in column names, got " + v);
            }).toList();
            var exchangeId = UUID.randomUUID().toString();
            listener.onResponse(new Value.ExchangeVal(exchangeId, columnNames, (int) bufferSize));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Exchange.open failed", e));
        }
    }

    /**
     * {@code Exchange.sink : Exchange r → Sink r}
     * Registers a sink handler with {@link ExchangeService} and returns a sink.
     */
    static void sink(ExchangeService exchangeService, Value.ExchangeVal exchangeVal, ActionListener<Value> listener) {
        try {
            var sinkHandler = exchangeService.getOrCreateSinkHandler(
                exchangeVal.exchangeId(),
                exchangeVal.bufferSize()
            );
            var sinkHandle = sinkHandler.createExchangeSink(() -> {});
            listener.onResponse(new Value.ExchangeSinkVal(sinkHandle, exchangeVal.columnNames()));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Exchange.sink failed", e));
        }
    }

    /**
     * {@code Exchange.connect : Exchange r → Source r}
     * Creates an {@link ExchangeSourceHandler} and attaches a local {@code RemoteSink}
     * that fetches pages from the sink handler registered under this exchange ID.
     */
    static void connect(Evaluator eval, Value.ExchangeVal exchangeVal, ActionListener<Value> listener) {
        try {
            var deps = eval.deps;
            var exchangeService = deps.exchangeService();
            var sourceHandler = new ExchangeSourceHandler(exchangeVal.bufferSize(), deps.executor());
            var remoteSink = exchangeService.newRemoteSink(
                deps.task(),
                exchangeVal.exchangeId(),
                deps.transportService(),
                deps.transportService().getLocalNodeConnection()
            );
            sourceHandler.addRemoteSink(remoteSink, true, () -> {}, 1, ActionListener.noop());
            var sourceHandle = sourceHandler.createExchangeSource();
            listener.onResponse(new Value.ExchangeSourceVal(sourceHandle, exchangeVal.columnNames()));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Exchange.connect failed", e));
        }
    }

    /**
     * {@code Exchange.addPage : Sink r → Page r → Null}
     * Pushes a page into the exchange sink.
     */
    static void addPage(Value.ExchangeSinkVal sinkVal, Value.PageVal pageVal, ActionListener<Value> listener) {
        try {
            // Runtime safety check: verify column names match (order-independent)
            if (pageVal.columnNames().size() != sinkVal.columnNames().size()
                || new java.util.HashSet<>(pageVal.columnNames()).equals(new java.util.HashSet<>(sinkVal.columnNames())) == false) {
                listener.onFailure(new EvaluationException(
                    "Exchange.addPage: column name mismatch — page has " + pageVal.columnNames()
                        + " but exchange expects " + sinkVal.columnNames()
                ));
                return;
            }
            sinkVal.sink().addPage(pageVal.page());
            listener.onResponse(new Value.NullVal());
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Exchange.addPage failed", e));
        }
    }

    /**
     * {@code Exchange.poll : Source r → (Page r → Null) → Channel Null}
     * Consumes all pages from the source, invoking the callback for each page.
     * Returns a channel that completes when the source is exhausted.
     *
     * <p>Uses {@code ExchangeSource.waitForReading()} for backpressure: when no pages
     * are available, waits asynchronously until a page is added or the source finishes.
     */
    static void poll(
        Evaluator eval,
        Value.ExchangeSourceVal sourceVal,
        Value callback,
        ActionListener<Value> listener
    ) {
        var deps = eval.deps;
        var channelId = deps.channelRegistry().nextChannelId();
        var channelListener = new SubscribableListener<Value>();
        deps.channelRegistry().register(channelId, channelListener);

        // Start the async poll loop on the executor
        deps.executor().execute(
            () -> pollLoop(eval, sourceVal.source(), sourceVal.columnNames(), callback, channelListener)
        );

        listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
    }

    /**
     * Async poll loop: polls pages from the source, applies the callback to each,
     * and completes the channel when done.
     */
    private static void pollLoop(
        Evaluator eval,
        ExchangeSource source,
        List<String> columnNames,
        Value callback,
        SubscribableListener<Value> channelListener
    ) {
        try {
            while (true) {
                if (source.isFinished()) {
                    channelListener.onResponse(new Value.NullVal());
                    return;
                }
                Page page = source.pollPage();
                if (page != null) {
                    var pageVal = new Value.PageVal(page, columnNames);
                    // Apply callback synchronously (it returns Null)
                    eval.applyFunction(callback, pageVal, ActionListener.noop());
                } else {
                    // No page available — wait asynchronously for data or completion
                    var blocked = source.waitForReading();
                    blocked.listener().addListener(ActionListener.running(
                        () -> eval.deps.executor().execute(
                            () -> pollLoop(eval, source, columnNames, callback, channelListener)
                        )
                    ));
                    return;
                }
            }
        } catch (Exception e) {
            channelListener.onFailure(new EvaluationException("Exchange.poll failed", e));
        }
    }

    /**
     * {@code Exchange.finish : Sink r → Null}
     * Signals that no more pages will be added to this sink.
     */
    static void finish(Value.ExchangeSinkVal sinkVal, ActionListener<Value> listener) {
        try {
            sinkVal.sink().finish();
            listener.onResponse(new Value.NullVal());
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Exchange.finish failed", e));
        }
    }
}
