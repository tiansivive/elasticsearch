/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

/**
 * Monaco Monarch tokenizer definition for Piescript.
 *
 * To integrate into Kibana's @kbn/monaco package, copy this file into
 * src/platform/packages/shared/kbn-monaco/src/languages/piescript/
 * and register it following the pattern used by ES|QL. See INTEGRATION.md
 * in this directory for the full checklist.
 *
 * Monarch reference: https://microsoft.github.io/monaco-editor/monarch.html
 */

import type { languages } from 'monaco-editor';

export const PIESCRIPT_LANG_ID = 'piescript';

export const piescriptMonarchLanguage: languages.IMonarchLanguage = {
  defaultToken: '',
  ignoreCase: false,

  keywords: [
    'let', 'in', 'fn', 'if', 'then', 'else',
    'match', 'spawn', 'send', 'when', 'par', 'do', 'query',
  ],

  effectKeywords: ['spawn!'],

  builtinConstants: ['true', 'false', 'null'],

  typeConstructors: [
    'Integer', 'Long', 'Double', 'Float', 'Keyword', 'Boolean', 'Null',
    'Channel', 'List',
  ],

  operators: [
    '|>', '||', '&&', '->', '==', '!=', '<=', '>=',
    '<', '>', '+', '-', '*', '/', '%', '!', '&', '|',
  ],

  symbols: /[=><!~?:&|+\-*\/\^%]+/,

  escapes: /\\(?:["\\/nrt])/,

  tokenizer: {
    root: [
      // Whitespace & comments
      [/[ \t\r\n]+/, 'white'],
      [/\/\/.*$/, 'comment'],
      [/\/\*/, 'comment', '@blockComment'],

      // Effect keywords (before identifier rules)
      [/spawn!/, 'keyword.effect'],

      // Keywords, type constructors, and identifiers
      [/[a-z_][a-zA-Z0-9_]*/, {
        cases: {
          '@keywords': 'keyword',
          '@builtinConstants': 'constant.language',
          '@default': 'identifier',
        },
      }],

      [/[A-Z][a-zA-Z0-9_]*/, {
        cases: {
          '@typeConstructors': 'type.identifier',
          '@default': 'type.identifier',
        },
      }],

      // Numbers
      [/\d+\.\d+([eE][+-]?\d+)?/, 'number.float'],
      [/\.\d+([eE][+-]?\d+)?/, 'number.float'],
      [/\d+[eE][+-]?\d+/, 'number.float'],
      [/\d+/, 'number'],

      // Strings
      [/"/, 'string', '@string'],

      // ESQL embedded blocks: query `...`
      [/`/, 'string.esql', '@esqlBlock'],

      // Multi-char operators (order matters for prefix matching)
      [/\|>/, 'operator.pipe'],
      [/\|\|/, 'operator'],
      [/&&/, 'operator'],
      [/->/, 'operator.arrow'],
      [/==/, 'operator'],
      [/!=/, 'operator'],
      [/<=/, 'operator'],
      [/>=/, 'operator'],

      // Single-char operators & punctuation
      [/[{}()]/, '@brackets'],
      [/[<>]/, 'operator'],
      [/[+\-*\/%!&|]/, 'operator'],
      [/\./, 'delimiter'],
      [/,/, 'delimiter'],
      [/:/, 'delimiter'],
      [/;/, 'delimiter'],
      [/=/, 'operator'],
    ],

    blockComment: [
      [/[^/*]+/, 'comment'],
      [/\*\//, 'comment', '@pop'],
      [/[/*]/, 'comment'],
    ],

    string: [
      [/@escapes/, 'string.escape'],
      [/[^\\"]+/, 'string'],
      [/"/, 'string', '@pop'],
    ],

    esqlBlock: [
      [/[^`]+/, 'string.esql'],
      [/`/, 'string.esql', '@pop'],
    ],
  },
};

/**
 * Language configuration for bracket matching, auto-closing, etc.
 */
export const piescriptLanguageConfiguration: languages.LanguageConfiguration = {
  brackets: [
    ['(', ')'],
    ['{', '}'],
  ],
  autoClosingPairs: [
    { open: '(', close: ')' },
    { open: '{', close: '}' },
    { open: '"', close: '"' },
    { open: '`', close: '`' },
    { open: '/*', close: '*/' },
  ],
  surroundingPairs: [
    { open: '(', close: ')' },
    { open: '{', close: '}' },
    { open: '"', close: '"' },
  ],
  comments: {
    lineComment: '//',
    blockComment: ['/*', '*/'],
  },
};
