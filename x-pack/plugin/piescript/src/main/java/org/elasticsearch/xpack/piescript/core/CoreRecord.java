/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.esql.core.tree.NodeInfo;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Record literal: {@code { name: "alice", age: 30 }}. Labels are stored as
 * non-child properties; field values are the Node children (in the same order
 * as labels).
 */
public final class CoreRecord extends CoreExpr {

    private final List<String> labels;
    private final MonoType type;

    /**
     * @param labels field names, parallel to the children list
     * @param values field value expressions, stored as Node children
     */
    public CoreRecord(Source source, List<String> labels, List<CoreExpr> values, MonoType type) {
        super(source, values);
        this.labels = List.copyOf(labels);
        this.type = type;
    }

    /** Convenience factory from a list of {@link CoreField} pairs. */
    public static CoreRecord create(Source source, List<CoreField> fields, MonoType type) {
        List<String> labels = new ArrayList<>(fields.size());
        List<CoreExpr> values = new ArrayList<>(fields.size());
        for (CoreField f : fields) {
            labels.add(f.label());
            values.add(f.value());
        }
        return new CoreRecord(source, labels, values, type);
    }

    public List<String> labels() {
        return labels;
    }

    /** Reconstruct the field list by zipping labels with children. */
    public List<CoreField> fields() {
        List<CoreExpr> values = children();
        List<CoreField> result = new ArrayList<>(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            result.add(new CoreField(labels.get(i), values.get(i)));
        }
        return result;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreRecord> info() {
        return NodeInfo.create(this, CoreRecord::new, labels, children(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreRecord(source(), labels, newChildren, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreRecord other = (CoreRecord) obj;
        return Objects.equals(labels, other.labels) && Objects.equals(type, other.type);
    }
}
