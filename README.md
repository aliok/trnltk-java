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