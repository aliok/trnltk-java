## Introduction ##

TRNLTK offers a morphologic parser. That is, it fragments given words (surfaces) into morphemes.
A *morpheme* is any part of a surface: root or suffixes

For example, for surface `kediyi` one of the possible parse results is `kedi+Noun+A3sg+Pnon+Acc`. That means:

* Root of the word is `kedi` and it is a noun
* It is a singular noun : `A3sg`
* There is no possession : `Pnon`
* And the case for the noun is accusative : `Acc`

## Multiple Parse Results ##

Turkish has very high morphologic ambiguity. That means without knowing the context (ie. using one surface only; not
using other surfaces in the sentence) it is not possible to tell what is the correct parse result.
For example, for surface `eti` there are more than one parse results:

* `et+Noun+A3sg+Pnon+Acc` : `o eti`
* `et+Noun+A3sg+P3sg+Nom` : `onun eti`

The morphologic parser we are talking here finds all possible parse results for all scenarios. That means, it is
*contextless*.

## Creating a Parser ##
Default parser implementation has many parts and dependencies. To simplify creation of a parser there is a builder
which creates some predefined parsers.

For simple cases, please use `ContextlessMorphologicalParserBuilder`:

```java
// create a morphologic parser with simplest suffix graph and numeral suffix graph, roots from bundled dictionary
MorphologicParser parser = ContextlessMorphologicParserBuilder.createSimple();
```

This code snippet gives you a parser which uses
* a basic suffix graph : simplest suffix graph for Turkish morphotactics
* a dictionary root finder : a root finder which only tries finding roots in the non-numeral dictionary

For most of the cases, this is not sufficient since it does not include
* punctuation morphotactics (ie. punctuation suffix graph)
* numeral morphotactics (ie. numeral suffix graph)
* proper noun morphotactics (ie. proper noun suffix graph)
* copula morphotactics (ie. copula suffix graph)
* numeral dictionary roots
* brute force root finders
* ...

However, it is still very good since it is able to parse 80% of words in Turkish.

Then you can use the parser as shown following:
```java
// parse surface
List<MorphemeContainer> morphemeContainers = parser.parseStr("eti");
```

All possible parse results are returned.

Let's print results:
```java
// print results
for (MorphemeContainer morphemeContainer : morphemeContainers) {
    // printing format is the simplest one : no suffix form applications, no grouping
    System.out.println(MorphemeContainerFormatter.formatMorphemeContainer(morphemeContainer));
}
```
This produces the following output:
```
et+Noun+A3sg+P3sg+Nom
et+Noun+A3sg+Pnon+Acc
```

Since the parser is contextless (it doesn't know the context), it returns all possible parse results for all scenarios.
* et+Noun+A3sg+P3sg+Nom : *onun eti*
* et+Noun+A3sg+Pnon+Acc : *bu eti*

You may find the example [here](/core/src/doc/org/trnltk/doc/simpleparsing/SimpleParsing.java).

## Formatting Options ##

You might want to compare results with other parsers or other corpus. TRNLTK offers different formatting options to
make this comparison or integration easy. For the word *kitaba* and the parse result 'kitap+Noun+A3sg+Pnon+Dat', here
are the illustrations:

* Oflazer format

    MorphemeContainerFormatter.formatMorphemeContainer(result)

    `kitap+Noun+A3sg+Pnon+Dat`

* TRNLTK detailed format

    MorphemeContainerFormatter.formatMorphemeContainerDetailed(result)

    `{"Parts":[{"POS":"Noun","Suffixes":["A3sg","Pnon","Dat"]}],"LemmaRoot":"kitap","RootPos":"Noun","Root":"kitab"}`

* Metu-Sabanci corpus format

    MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result)

    `(1,"kitap+Noun+A3sg+Pnon+Dat")`

* TRNLTK format

    MorphemeContainerFormatter.formatMorphemeContainerWithForms(result)

    `kitab(kitap)+Noun+A3sg+Pnon+Dat(+yA[a])`

You may find the example [here](/core/src/doc/org/trnltk/doc/formattingoptions/FormattingOptions.java).

## Advanced Usage ##

See [advanced_parsing.md](advanced_parsing.md)
