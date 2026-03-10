/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;

/**
 * Entry point for parsing Piescript programs. Takes a program string and
 * produces an ANTLR parse tree ({@link PiescriptAntlrParser.ProgramContext}).
 */
public class PiescriptParser {

    public static final int MAX_LENGTH = 1_000_000;

    public PiescriptAntlrParser.ProgramContext parse(String program) {
        return parseInternal(program).tree;
    }

    /**
     * Parse a program and return its LISP-style tree representation.
     * Useful for debugging and for the Phase 1a REST endpoint.
     */
    public String parseToTreeString(String program) {
        ParseResult result = parseInternal(program);
        return result.tree.toStringTree(result.parser);
    }

    private ParseResult parseInternal(String program) {
        if (program.length() > MAX_LENGTH) {
            throw new PiescriptParsingException(
                "Piescript program is too large [" + program.length() + " characters > " + MAX_LENGTH + "]"
            );
        }

        PiescriptLexer lexer = new PiescriptLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERROR_LISTENER);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        PiescriptAntlrParser parser = new PiescriptAntlrParser(tokenStream);

        parser.removeErrorListeners();
        parser.addErrorListener(ERROR_LISTENER);
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        return new ParseResult(parser.program(), parser);
    }

    private record ParseResult(PiescriptAntlrParser.ProgramContext tree, PiescriptAntlrParser parser) {}

    private static final BaseErrorListener ERROR_LISTENER = new BaseErrorListener() {
        @Override
        public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String message,
            RecognitionException e
        ) {
            throw new PiescriptParsingException("line " + line + ":" + charPositionInLine + " " + message);
        }
    };
}
