## Problem ##
Turkish is an agglutinative language. Thus it is problematic to do spell checking, since it is impossible to
store all words and then check input against them. That is still a good option since storing 10 million words
will cover almost all text.

However for example on internet, unique words are much more than that. In that case, one must parse words on the fly.

## Solution ##

See [here](/core/src/doc/org/trnltk/cookbook/spellcheck/SpellChecker.java)

## Improvements ##
One big improvement would be storing a large number of words in memory and doing the parsing whenever an input out of that
list is received.

This list must be chosen wisely. If the size is too big, then system would need too much memory.
If the number is too small, then system would be slow since it has to do a lot of parsing.

## The right way ##
Current solution finds unknown words, but it does not suggest correct words.

A real world application would combine this approach with a keyboard centric approach.
That means, if there is an unknown word system should suggest corrections based on the relevance in terms of keyboard
layout. If you investigate spelling errors, they are caused by reasons like:
* Mistyped letter
* Missing letter
* Extra letter

System should be able to detect these and suggest corrections in the order of distance to given input.

For this solution, check spelling module of Zemberek3 at https://github.com/ahmetaa/zemberek-nlp

