/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.SubscribableListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-node registry mapping channel IDs to their {@link ActionListener} callbacks.
 * Entries are never auto-removed on completion — {@link SubscribableListener} correctly
 * serves late subscribers with the cached result, so the entry must remain visible for
 * {@code when} lookups that race with {@code send}. Cleanup will be addressed in C.3
 * (node-scoped registry with per-evaluation lifecycle). See D-045.
 *
 * <p>Thread-safe: all operations use a {@link ConcurrentHashMap}.
 */
public final class ChannelRegistry {

    private final ConcurrentHashMap<String, ActionListener<Value>> channels = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    /**
     * Generate a unique channel ID for this node.
     */
    public String nextChannelId() {
        return "ch-" + idCounter.incrementAndGet();
    }

    /**
     * Register a channel.
     */
    public void register(String channelId, ActionListener<Value> listener) {
        var prev = channels.putIfAbsent(channelId, listener);
        if (prev != null) {
            throw new IllegalStateException("channel already registered: " + channelId);
        }
    }

    /**
     * Look up the listener for a channel. Returns {@code null} if the channel
     * is not registered or unknown.
     */
    public ActionListener<Value> lookup(String channelId) {
        return channels.get(channelId);
    }

    /**
     * Look up a channel's listener as a {@link SubscribableListener}, for use
     * by {@code when} which needs to add subscribers. Throws if the channel
     * is not found or is not subscribable (e.g. inbox).
     */
    public SubscribableListener<Value> lookupSubscribable(String channelId) {
        var listener = channels.get(channelId);
        if (listener == null) {
            throw new IllegalStateException("channel not found in local registry: " + channelId);
        }
        if (listener instanceof SubscribableListener<Value> subscribable) {
            return subscribable;
        }
        throw new IllegalStateException("channel is not subscribable (e.g. inbox): " + channelId);
    }

    /**
     * Complete a channel with a value. Delivers the value to the registered
     * listener but does NOT remove the entry — the {@link SubscribableListener}
     * remains in the registry so that late {@code when} lookups still find it.
     */
    public void complete(String channelId, Value value) {
        var listener = channels.get(channelId);
        if (listener == null) {
            throw new IllegalStateException("channel not found: " + channelId);
        }
        listener.onResponse(value);
    }
}
