# Piescript Syntax Highlighting in Kibana Dev Tools

This directory contains the Monaco Monarch grammar for Piescript (`piescript-monarch.ts`).
To get syntax highlighting in Kibana Dev Tools, you need to register this language in the
Kibana `@kbn/monaco` package and wire it into the Console.

## Architecture overview

ES|QL highlighting in Kibana follows this pattern:

1. **Language definition** in `@kbn/monaco` (`src/platform/packages/shared/kbn-monaco/src/languages/esql/`)
2. **Monarch tokenizer** registered via `monaco.languages.setMonarchTokensProvider()`
3. **Language config** (brackets, auto-close, comments) via `monaco.languages.setLanguageConfiguration()`
4. **Console integration** — the Console detects triple-quoted `"query": """..."""` fields in
   ES|QL requests and applies the ES|QL tokenizer inside them

Piescript follows the same pattern but with `"program"` as the field name and
`POST /_piescript/eval` / `POST /_piescript/dev` as the endpoints.

## Step-by-step integration

### 1. Create the language directory

```
src/platform/packages/shared/kbn-monaco/src/languages/piescript/
├── index.ts
├── language.ts
└── lib/
    └── constants.ts
```

### 2. `lib/constants.ts`

```ts
export const PIESCRIPT_LANG_ID = 'piescript';
```

### 3. `language.ts`

Copy `piescript-monarch.ts` from this directory and adapt it to the `CustomLangModuleType`
interface (see the ES|QL `language.ts` for the pattern):

```ts
import { monaco } from '../../monaco_imports';
import type { CustomLangModuleType } from '../../types';
import { PIESCRIPT_LANG_ID } from './lib/constants';
import { piescriptMonarchLanguage, piescriptLanguageConfiguration } from './piescript-monarch';

export const PiescriptLang: CustomLangModuleType = {
  ID: PIESCRIPT_LANG_ID,
  async onLanguage() {
    monaco.languages.setMonarchTokensProvider(PIESCRIPT_LANG_ID, piescriptMonarchLanguage);
  },
  languageConfiguration: piescriptLanguageConfiguration,
};
```

### 4. `index.ts`

```ts
export { PIESCRIPT_LANG_ID } from './lib/constants';
export { PiescriptLang } from './language';
```

### 5. Register in `languages/index.ts`

Add to imports and to the `initializeSupportedLanguages` array:

```ts
import { PiescriptLang, PIESCRIPT_LANG_ID } from './piescript';
// ...
export { PIESCRIPT_LANG_ID };
// ...
export { PiescriptLang };
// ... inside initializeSupportedLanguages:
  PiescriptLang,
```

### 6. Console body detection

The Console needs to apply Piescript highlighting inside `"program"` string fields
when the request targets `/_piescript/*`. This is the same mechanism that highlights
ES|QL inside `"query"` fields for `/_query` endpoints.

Look at how PR [#219980](https://github.com/elastic/kibana/pull/219980) added ESQL
autocomplete in Console — it detects triple-quoted `"query": """..."""` strings and
switches the tokenizer. The equivalent for Piescript would detect `"program"` fields
in `/_piescript/eval` or `/_piescript/dev` requests.

For a simpler v1, you could also just use triple-quoted strings for the program field:

```json
POST /_piescript/eval
{
  "program": """
    let topo = topology "cluster"
    in let nodes = topo.nodes
    in head nodes
  """
}
```

This way the Console's existing triple-quote detection can be extended to match
Piescript endpoints and apply the Piescript tokenizer.

## Token → theme mapping

The Monarch grammar emits these token types:

| Token | What it matches |
|-------|----------------|
| `keyword` | `let`, `in`, `fn`, `if`, `then`, `else`, `send`, `when`, `spawn`, ... |
| `keyword.effect` | `spawn!` |
| `constant.language` | `true`, `false`, `null` |
| `type.identifier` | `Integer`, `Keyword`, `Channel`, and all uppercase-initial identifiers |
| `identifier` | lowercase variables and function names |
| `number` / `number.float` | integer and decimal literals |
| `string` / `string.escape` | `"hello\n"` |
| `string.esql` | embedded ES|QL in backtick blocks |
| `operator` / `operator.arrow` / `operator.pipe` | `->`, `|>`, `==`, etc. |
| `comment` | `//` and `/* ... */` |
| `delimiter` | `.`, `,`, `:`, `;` |
| `@brackets` | `(`, `)`, `{`, `}` |

Map these to EUI theme colors in a `buildPiescriptTheme` function, or reuse the
existing Console/ES|QL theme rules as a starting point.
