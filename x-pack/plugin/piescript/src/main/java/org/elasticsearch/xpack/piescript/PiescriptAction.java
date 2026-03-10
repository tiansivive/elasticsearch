/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionType;
import org.elasticsearch.xpack.esql.action.EsqlQueryResponse;

public class PiescriptAction extends ActionType<EsqlQueryResponse> {

    public static final PiescriptAction INSTANCE = new PiescriptAction();
    public static final String NAME = "indices:data/read/piescript";

    private PiescriptAction() {
        super(NAME);
    }
}
