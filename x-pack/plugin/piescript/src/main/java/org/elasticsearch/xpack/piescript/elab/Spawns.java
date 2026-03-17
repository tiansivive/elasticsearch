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
 * Spawn expression elaboration. Two forms (D-045):
 * <ul>
 *   <li>{@code spawn expr} — fork body, return {@code Channel bodyType}.</li>
 *   <li>{@code spawn!} — bare channel creation, return {@code Channel alpha} (fresh meta).</li>
 * </ul>
 */
final class Spawns {

    private Spawns() {}

    static CoreExpr spawn(Elaborator elab, PiescriptAntlrParser.SpawnExprContext s, ElaborationContext ctx) {
        var src = Elaborator.source(s);
        var body = elab.elaborate(s.expr(), ctx);
        var chanType = new MonoType.AppType(Elaborator.CHANNEL, body.type());
        return new CoreSpawn(src.source(), body, chanType);
    }

    static CoreExpr spawnBang(Elaborator elab, PiescriptAntlrParser.SpawnBangExprContext s, ElaborationContext ctx) {
        var src = Elaborator.source(s);
        var elementMeta = elab.state.freshType(ctx.bindingLevel());
        var chanType = new MonoType.AppType(Elaborator.CHANNEL, elementMeta);
        return new CoreSpawn(src.source(), null, chanType);
    }
}
