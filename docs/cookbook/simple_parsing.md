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



## Formatting Options ##

Oflazer
with forms
Sabanci metu corpus
etc.

## Advanced Usage ##

See advanced_parsing.md
