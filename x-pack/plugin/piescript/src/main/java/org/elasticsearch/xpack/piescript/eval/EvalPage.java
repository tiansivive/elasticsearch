/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.piescript.eval;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.BooleanBlock;
import org.elasticsearch.compute.data.BytesRefBlock;
import org.elasticsearch.compute.data.DoubleBlock;
import org.elasticsearch.compute.data.LongBlock;
import org.elasticsearch.compute.data.Page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builtins for materializing columnar {@link Page} data into piescript Values.
 */
final class EvalPage {

    private EvalPage() {}

    /**
     * {@code Page.toList : Page r → List (Record r)}
     * Materializes a columnar Page into a list of record values, one per row.
     */
    static void toList(Value.PageVal pageVal, ActionListener<Value> listener) {
        try {
            Page page = pageVal.page();
            List<String> columnNames = pageVal.columnNames();
            int rowCount = page.getPositionCount();
            int colCount = page.getBlockCount();

            var rows = new ArrayList<Value>(rowCount);
            for (int row = 0; row < rowCount; row++) {
                var fields = new LinkedHashMap<String, Value>();
                for (int col = 0; col < colCount; col++) {
                    Block block = page.getBlock(col);
                    fields.put(columnNames.get(col), extractValue(block, row));
                }
                rows.add(new Value.RecordVal(fields));
            }
            listener.onResponse(new Value.ListVal(rows));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Page.toList failed", e));
        }
    }

    /**
     * {@code Page.count : Page r → Double}
     * Returns the number of rows in the page.
     */
    static void count(Value.PageVal pageVal, ActionListener<Value> listener) {
        listener.onResponse(new Value.DoubleVal(pageVal.page().getPositionCount()));
    }

    /**
     * Extract a single cell value from a Block at the given row position.
     * Block type determines Value type — no runtime type metadata needed.
     */
    private static Value extractValue(Block block, int position) {
        if (block.isNull(position)) {
            return new Value.NullVal();
        }
        int valueIndex = block.getFirstValueIndex(position);
        return switch (block) {
            case BooleanBlock bb -> new Value.BooleanVal(bb.getBoolean(valueIndex));
            case LongBlock lb -> new Value.DoubleVal(lb.getLong(valueIndex));
            case DoubleBlock db -> new Value.DoubleVal(db.getDouble(valueIndex));
            case BytesRefBlock bb -> new Value.KeywordVal(bb.getBytesRef(valueIndex, new BytesRef()).utf8ToString());
            default -> throw new EvaluationException("Page.toList: unsupported block type " + block.getClass().getSimpleName());
        };
    }
}
