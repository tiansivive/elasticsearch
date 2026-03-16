/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xpack.esql.core.tree.Node;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;

/**
 * Base class for the Piescript Core IR — the typed intermediate representation
 * produced by the elaborator from the surface CST. Every node carries a {@link MonoType}
 * (possibly containing unsolved metas resolved via the zonker). Variables use de Bruijn
 * indices; binders carry optional debug names for error messages.
 *
 * <p>Extends {@link Node} from esql-core for tree traversal infrastructure
 * ({@code transformDown}, {@code transformUp}, {@code forEachDown}), which will be
 * used by the optimizer in Phase 2+.
 *
 * <p>Design spec (sealed interface form, from Phase 1 plan D1.2):
 * <pre>{@code
 * CoreExpr
 *   = Var(index: int, debugName: String?, type: MonoType)
 *   | Lit(value: LitVal, type: MonoType)
 *   | Lam(debugName: String?, paramType: MonoType, body: CoreExpr, type: MonoType)
 *   | App(fn: CoreExpr, arg: CoreExpr, type: MonoType)
 *   | Let(debugName: String?, bindType: MonoType, rhs: CoreExpr, body: CoreExpr, type: MonoType)
 *   | Record(fields: List<Field>, type: MonoType)
 *   | Project(expr: CoreExpr, label: String, type: MonoType)
 *   | Update(expr: CoreExpr, fields: List<Field>, type: MonoType)
 *   | PrimOp(op: Op, args: List<CoreExpr>, type: MonoType)
 * }</pre>
 */
public abstract sealed class CoreExpr extends Node<CoreExpr> permits CoreVar, CoreFree, CoreLit, CoreLam, CoreApp, CoreLet, CoreRecord,
    CoreProject, CoreUpdate, CorePrimOp, CoreTypeAbs, CoreTypeApp, CoreQuery, CoreSpawn, CoreWhen {

    protected CoreExpr(Source source, List<CoreExpr> children) {
        super(source, children);
    }

    /** The monomorphic type of this expression, filled by elaboration. */
    public abstract MonoType type();

    @Override
    public String getWriteableName() {
        throw new UnsupportedOperationException("piescript Core IR is not serialized");
    }

    @Override
    public void writeTo(StreamOutput out) {
        throw new UnsupportedOperationException("piescript Core IR is not serialized");
    }
}
