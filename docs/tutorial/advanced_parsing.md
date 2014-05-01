## Introduction ##

**You can find the source code of the full example [here](/core/src/doc/org/trnltk/doc/advancedparsing/AdvancedParsing.java)**

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

Another example is excluding copula suffix graph. That way, parser is not aware about the case in Turkish where
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
* All three suffix graphes -> all 3 parse results


See *[Suffix Graphs Explained](suffix_graphs_explained.md)* for details.

## Root Finders ##

Roots are to find starting node in a suffix graph. In TRNLTK, roots are found using `RootFinder`s.
Some bundled `RootFinder`s are:
* `DictionaryRootFinder` finds roots defined in a dictionary. For example `at(mak)` and `ata(mak)` for surface `atamadım`
* `CardinalDigitsRootFinder` extracts cardinal number roots from a surface. For example `32` for surface `32'yi`
* `OrdinalDigitsRootFinder` extracts ordinal number roots from a surface. For example `32.` for surface `32.'yi`
* ...

Any number of `RootFinder`s can be injected to a parser. That way you can limit or improve the matching of roots.

See *[Root Finders Explained](root_finders_explained.md)* for details.


## Creating a customized parser ##

In this section, 2 parsers will be created:
* A relatively simple one which parses numbers and words without copula.
* A complex one which in addition parses punctuations, words with wrong circumflexes, proper nouns and words with copula.
This one will use a cache: a 2-level online one.

### Parser #1 ###
This parser will parse numbers, thus it needs numeral root finders and numeral suffix graph.
It will also parse simple words, but it won't be able to parse when they have copula.
It won't be able to parse proper nouns.

**A parser like this one could be used e.g. within a action-semantic search engine**, since it recognizes basic keywords
and by limited graph and roots it is quite fast.

Building such parser is quite simple using `ContextlessMorphologicParserBuilder`:
```java
ContextlessMorphologicParserBuilder.newBuilderWithoutCircumflexConversion()
    .includeBundledBasicSuffixGraph()
    .includeBundledNumeralSuffixGraph()
    .addAllBundledNoBruteForceRootFinders(false)        // don't add proper noun root finders
    .build(false);
```
There are methods in the builder to include other suffix graphs or other root finders. You can also pass your own cache
or own root finders, or even your own suffix graph using methods in the builder. See javadoc of the class.

### Parser #2 ###
This is an example of building the parser manually. Since a parser consists of many parts (suffix graphs, root finders,
dictionary, phonetic analyzer, phonetic engine, etc.) it is a boring task.

```java
// load bundled dictionaries of numbers and words
HashMultimap<String, ? extends Root> dictionaryRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();

// build common parts
final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

// build extractor which is used while converting a suffix graph to a suffix form graph
final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);

// build suffix graphs
final SuffixGraph suffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
suffixGraph.initialize();

// build predefined paths with suffix graphs and dictionary
final PredefinedPathProvider predefinedPaths = new PredefinedPathProviderImpl(suffixGraph, dictionaryRootMap, suffixApplier);
predefinedPaths.initialize();

// build root finders and add them into the chain
final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(dictionaryRootMap);
final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
final PuncRootFinder puncRootFinder = new PuncRootFinder();

final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());

rootFinderChain
        .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
        .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
        .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
        .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
        .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
        .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
        .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

// extract suffix form graph from suffix graph
final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(suffixGraph);

// finally, build parser
final ContextlessMorphologicParser parser = new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);

// build cache
final MorphologicParserCache l1Cache = new LRUMorphologicParserCache(NUMBER_OF_THREADS, L1_CACHE_INITIAL_SIZE, L1_CACHE_MAX_SIZE);
final MorphologicParserCache twoLevelCache = new TwoLevelMorphologicParserCache(L2_CACHE_MAX_SIZE, l1Cache);

// build a caching parser which delegates parsing to the one created above, if surface is not found in the cache
return new CachingMorphologicParser(twoLevelCache, parser, true);
```

With this approach, you can inject your extensions in common parts as well as suffix graphs or root finders.

One important thing to note here is the cache. It is a 2 level online cache.

In order to make use of locality of the inputs, a 2 level cache could be used. That means, 2nd level will be a small
cache which will store the values of one batch in a thread. 1st level cache will be the big and parser wide cache
which is used by 2nd level if no cached value is found on 2nd level. If no cached value is found on 1st level cache,
then the surface will be actually parsed. Please note that, in order to optimize the performance and memory usage,
you must think of the values passed to caches : concurrency level, initial capacity of 1st level cache, max size of 1st
level cache, max size of 2nd level cache. Implementations used in this example are thread safe.

Please note that caches are not fully used since in this example focus is on comparison of building the parsers.

Please see the javadoc of the respective cache classes.

### Comparison ##
```
Surface elma:
Results from Parser1:
	elma(elma)+Noun+A3sg+Pnon+Nom                                    --> regular noun
Results from Parser2:
	elma(elma)+Noun+A3sg+Pnon+Nom                                    --> regular noun
	elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg                --> regular noun used as a verb (implicit copula)

Surface Ahmet:
Results from Parser1:                                                --> no results, since proper noun root finders are not added
	No results found
Results from Parser2:
	Ahmet(Ahmet)+Noun+Prop+A3sg+Pnon+Nom                             --> proper noun

Surface elmadır:
Results from Parser1:                                                --> no results, since copula graph is not included
	No results found
Results from Parser2:
	elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dır])  --> noun with explicit copula

Surface Türkiye'ye:
Results from Parser1:                                                --> no results, since proper noun root finders are not added
	No results found
Results from Parser2:
	Türkiye(Türkiye)+Noun+Prop+Apos+A3sg+Pnon+Dat(+yA[ye])           --> proper noun
	Türkiye(Türkiye)+Noun+Prop+Apos+A3sg+Pnon+Dat(+yA[ye])+Verb+Zero+Pres+A3sg    --> proper noun used as a verb (implicit copula)

Surface kâtip:
Results from Parser1:
	kâtip(kâtip)+Noun+A3sg+Pnon+Nom                                  --> regular noun with circumflex. surface is also given with circumflex
Results from Parser2:
	kâtip(kâtip)+Noun+A3sg+Pnon+Nom                                  --> same as above...
	kâtip(kâtip)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg              --> regular noun used as a verb (implicit copula)

Surface katip:
Results from Parser1:                                                --> no circumflex form of surface is not recognized
	No results found
Results from Parser2:
	katip(kâtip)+Noun+A3sg+Pnon+Nom                                  --> no circumflex form of surface is recognized
	katip(kâtip)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg              --> regular noun used as a verb (implicit copula)
```


## Summary ##
TRNLTK offers a simple builder-pattern way of building a morphologic parser and a manual way of doing it.

While builder approach will be sufficient in most cases, it won't allow you to use some custom things such as: custom
dictionary, custom phonetics, custom root validator, etc.

Manual approach is boring, but allows you to inject extended versions of all things. Design of TRNLTK parser allows that.

You can find the source code of the full example [here](/core/src/doc/org/trnltk/doc/advancedparsing/AdvancedParsing.java)