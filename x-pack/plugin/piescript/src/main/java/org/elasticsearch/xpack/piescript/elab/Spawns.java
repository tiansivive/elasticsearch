/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreSpawn;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;

/**
 * Spawn expression elaboration: converts a {@code SpawnExpr} CST node into a
 * typed {@link org.elasticsearch.xpack.piescript.core.CoreSpawn}. The body
 * expression is elaborated and its type is wrapped in {@code Chan}.
 */
final class Spawns {

    private Spawns() {}

    static CoreExpr spawn(Elaborator elab, PiescriptAntlrParser.SpawnExprContext s, ElaborationContext ctx) {
        var src = Elaborator.source(s);
        var body = elab.elaborate(s.expr(), ctx);
        var chanType = new MonoType.AppType(Elaborator.CHANNEL, body.type());
        return new CoreSpawn(src.source(), body, chanType);
    }
}
