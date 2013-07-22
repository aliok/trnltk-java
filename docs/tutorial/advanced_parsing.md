## Introduction ##

Every problem has a different method to solve. In TRNLTK, this is thought and morphological parser is made customizable.

For example, implementing a spell checker is different than implementing a search engine. In a spell checker, system
must strictly parse all surfaces, whereas in a search engine strictness is not good.

Or, in order to make an interactive e-book, parsing a local book is different than parsing a book with official language.
In a local book, some non-dictionary words are expected.

TRNLTK allows all of these customizations by allowing injection of different bundled root finders or suffix graphes.
Extending root finders or suffix graphs is also possible. That way you can decide what you need and you can reduce
the number of parse results for surfaces; it is great for reducing ambiguity. If you don't need to parse some kind of words
such as proper nouns, punctuations or numerals, you can just skip adding the root finders and suffix graphes for them. That
way everything is faster.

Another example is excluding copula suffix graph. That way, parser does not know about the case in Turkish where
all words can also be used as verbs.

## Suffix Graphs ##
TRNLTK morphotactic rules are implemented as a FSM graph which consists of multiple graphs.
For example, some of the bundled suffix graphs are:
* BasicSuffixGraph : Provides morphotactics for Turkish basic
* NumeralSuffixGraph : Provides morphotactics for Turkish numerals
...

These graphs are pluggable. That means, suffix graphs of combinations of suffix graphs can be plugged into the parser.

See the following example for surface `Yedi`:
* BasicSuffixGraph -> `ye+Verb+Pos+Past+A3sg` (yemek yedi)
* NumeralSuffixGraph -> `yedi+Num` (yedi masa)
* ProperNounGraph -> `Yedi+Noun+Prop+A3sg+Pnon+Nom` ('Yedi' as proper noun)
* BasicSuffixGraph + NumeralSuffixGraph -> `ye+Verb+Pos+Past+A3sg` (yemek yedi) and `yedi+Num` (yedi masa)
* ...
* All three suffix graph -> all 3 parse results


See *[Suffix Graphs Explained](suffix_graphs_explained.md)* for details.

## Root Finders ##

Roots are to find starting node in a suffix graph. In TRNLTK, roots are found using `RootFinder`s.
Some bundled `RootFinder`s are:
* `DictionaryRootFinder` finds roots defined in a dictionary. For example `at(mak)` and `ata(mak)` for surface `atamadım`
* `CardinalDigitsRootFinder` extracts cardinal number roots from a surface. For example `32` for surface `32'yi`
* `OrdinalDigitsRootFinder` extracts ordinal number roots from a surface. For example `32.` for surface `32.'yi`
...

Any number of `RootFinder`s can be injected to a parser. That way you can limit or improve the matching of roots.

See *[Root Finders Explained](root_finders_explained.md)* for details.


## Creating a customized parser ##

In this section, 2 parsers will be created:
* A relatively simple one which parses numbers and words without copula.
* A complex one which in addition parses punctuations, words with wrong circumflexes, proper nouns and words with copula.
This one will also use a cache.

### Parser #1 ###
This parser will parse numbers, thus it needs numeral root finders and numeral suffix graph.
It will also parse simple words, but it won't be able to parse when they have copula.

**A parser like this one could be used e.g. within a action-semantic search engine**, since it recognizes basic keywords
and by limited graph and roots it is quite fast.

```java
```


## Summary ##
...