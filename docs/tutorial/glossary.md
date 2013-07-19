Some definitions
----------------
<!---
Example Term
gideceğini Surface
gideceğini+Noun Surface+SurfacePos
gidecek Body
gidecek+Noun Body+BodyPos
gideceğ BodySurface
gitmek Lemma
gitmek+Verb Lemma+LemmaPos=Lexeme
git Root
gid RootSurface
(y)AcAk SuffixForm
ecek SuffixFormBody
eceğ SuffixFormSurface

Term Value Value Value
WordSurface kitapçılığı
Stems kitap kitapçı kitapçılık
StemSurfaces kitap kitapçı kitapçılığ
Body kitapçılık
BodySurface kitapçılığ

-->
<!---
Tables below are generated using http://www.sensefulsolutions.com/2010/10/format-text-as-table.html
-->
<table><tbody><tr><th>Example</th><th>Term</th></tr><tr><td>gideceğini</td><td>Surface</td></tr><tr><td>gideceğini+Noun</td><td>Surface+SurfacePos</td></tr><tr><td>gidecek</td><td>Body</td></tr><tr><td>gidecek+Noun</td><td>Body+BodyPos</td></tr><tr><td>gideceğ</td><td>BodySurface</td></tr><tr><td>gitmek</td><td>Lemma</td></tr><tr><td>gitmek+Verb</td><td>Lemma+LemmaPos=Lexeme</td></tr><tr><td>git</td><td>Root</td></tr><tr><td>gid</td><td>RootSurface</td></tr><tr><td>(y)AcAk</td><td>SuffixForm</td></tr><tr><td>ecek</td><td>SuffixFormBody</td></tr><tr><td>eceğ</td><td>SuffixFormSurface</td></tr></tbody></table>

<table><tbody><tr><th>Term</th><th>Value</th><th>Value</th><th>Value</th></tr><tr><td>WordSurface</td><td>kitapçılığı</td><td> </td><td> </td></tr><tr><td>Stems</td><td>kitap</td><td>kitapçı</td><td>kitapçılık</td></tr><tr><td>StemSurfaces</td><td>kitap</td><td>kitapçı</td><td>kitapçılığ</td></tr><tr><td>Body</td><td>kitapçılık</td><td> </td><td> </td></tr><tr><td>BodySurface</td><td>kitapçılığ</td><td> </td><td> </td></tr></tbody></table>


* Surface: Full word including the root and suffixes
* Root : The root of a word. Root atomic part.
* Derivation : Deriving a new word from another word.
* Inflection : Conjugating a word with a person agreement / possession / tense etc.
* Suffix form : Form of a suffix. For example, suffix 'Progressive' has 2 suffix forms; '-iyor' and '-makta'
* Body : Root + derivations. Doesn't include the inflections
* POS (part of speech) : Verb, Noun, Adjective etc.
* Inflectional suffix : A suffix that doesn't change body nor the POS of a surface
* Derivational suffix : A suffix that changes the body and might change the POS of a surface
* Morpheme : Elements of a surface; root and suffixes
* Lemma : The root text that can be found in a dictionary
* Lexeme : Lemma + POS of the lemma
* Morphology : How a surface is constructed and how can it be extracted to morphemes
* Morphotactics : Rules when can a suffix can be applied. For example "Progressive suffix can only be applied to a
Verb, and it can't be applied to a surface which has Progressive suffix already"
* Ortographics : Rules of phonetics. For example rules for voicing (kitap+a --> kitaba),
devoicing (kitap+cı --> kitapçı), vowel drop (omuz+u --> omzu), etc.