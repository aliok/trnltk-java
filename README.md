""" Copyright 2012-2013 Ali Ok (aliokATapacheDOTorg)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. """

Turkish Natural Language Toolkit
================================
This project provides a toolkit for computer linguistic work for Turkish.

Currently a morphologic parser and a tokenizer is provided. Biggest challenge is providing an ambiguity resolver.

Project first implemented in Python, [TRNLTK Python](https://github.com/aliok/trnltk), then Java.
Python project is obsolete.

[![Build Status](https://drone.io/github.com/aliok/trnltk-java/status.png)](https://drone.io/github.com/aliok/trnltk-java/latest)

**See [documentation, tutorial and cookbook](docs/README.md)**

News:
-----
  * TRNLTK 1.0.2 is released : [Release notes](docs/102.md)


Motivation
========================
Why another parsing tool and why FSM?

I've inspected other other approaches and I saw that tracking the problems are very hard with them.
For example, one approach is creating a suffix graph by defining what suffix can come after other suffix.
But with that approach it is impossible to have an overview of the graph, since there would be thousands of nodes and edges.

**See [documentation](docs/README.md) for more information.**


Phonetic rules and implementation is similar to from open-source java library Zemberek3.

How it is tested?
=================
There are thousands of parsing unit tests. Plus, I use the treebank from METU-Sabanci, but is closed-source.
Unfortunately, its license doesn't allow anyone to publish any portion of the treebank,
thus I only test the parser against it in my local environment.


Plan
=================
  1. Get rid of unused stuff as much as possible. Such as
    * suffix based parsing (deprecated by form based parsing)
  1. Fix the build!
  1. Prepare for reducing ambiguity in suffix graph. Fill reducedAmbiguity.ContextlessMorphologicParserBasicSuffixGraphTest
  1. Reduce ambiguity in suffix graph. E.g. discard parse results like
    * `"sokakları", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom", "sokak(sokak)+Noun+A3sg+P3pl(lAr!I[ları])+Nom"`
    * ...
  1. Write a disambiguator with hunch-based parameters and metrics
  1. Use machine learning techniques to determine metrics and parameters
  1. Apply ideas:
    * Ambiguity classification (to apply similar disambiguation techniques in case of a similar disambiguity)
    * Critical surface tagging (solve "easy-win"s)
    * Implement a parse tree for POS tagging
    * Integrate disambiguation and POS tagging
    * Proper noun identification
    *


