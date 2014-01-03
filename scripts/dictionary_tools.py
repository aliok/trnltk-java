# coding=utf-8
import codecs
import os
import unittest
from pytrie import SortedStringTrie as trie

Trnltk_Resources_Folder = "../core/src/main/resources/"

RawMaterials = [
    u"aba", u"abanoz", u"ağaç", u"ahşap", u"alçı", u"altın", u"alüminyum", u"antilop", u"astragan", u"bağa", u"bakır",
    u"bambu", u"basma", u"batik", u"batik", u"beledi", u"beton", u"bez", u"billur", u"bor", u"boynuz", u"bronz",
    u"bürümcük", u"cam", u"canfes", u"canfes", u"çelik", u"ceviz", u"çini", u"çinko", u"demir", u"deri", u"dişbudak",
    u"elmas", u"elyaf", u"emaye", u"emprime", u"fağfur", u"fildekoz", u"fildişi", u"fisto", u"fötr", u"gabardin",
    u"gezi", u"güderi", u"gümüş", u"gürgen", u"hasır", u"inci", u"ipek", u"isfendan", u"jarse", u"jelâtin", u"jorjet",
    u"jüt", u"jüt", u"kadife", u"kâgir", u"kâğıt", u"kalavra", u"kamış", u"kapitone", u"karamandola", u"karamandola",
    u"kaşmir", u"kauçuk", u"kayın", u"keçe", u"kehribar", u"kenevir", u"kenevir", u"kerpiç", u"keten", u"kıl",
    u"kılâptan", u"kösele", u"kot", u"kristal", u"krokodil", u"krom", u"kuka", u"kumaş", u"kürk", u"kurşun", u"lame",
    u"lasteks", u"lastik", u"lastik", u"lastikotin", u"lûtr", u"maden", u"maroken", u"maun", u"melamin", u"melamin",
    u"mercan", u"merinos", u"mermer", u"meşe", u"meşin", u"metal", u"mika", u"moher", u"moher", u"muare", u"muşamba",
    u"muslin", u"naylon", u"organtin", u"organtin", u"organze", u"pamuk", u"pamuk", u"papazi", u"papazi", u"patiska",
    u"pembezar", u"penye", u"pike", u"pirinç", u"plastik", u"podösüet", u"poplin", u"porselen", u"pötikare", u"rafya",
    u"rugan", u"sac", u"sadakor", u"şali", u"samur", u"saten", u"saz", u"sedef", u"selofan", u"selüloz", u"sentetik",
    u"seramik", u"şetlant", u"şifon", u"şimşir", u"sırça", u"sırma", u"sof", u"somaki", u"streç", u"sunta", u"sura",
    u"sura", u"tafta", u"tafta", u"tahta", u"tahta", u"taş", u"tel", u"teneke", u"tergal", u"terilen", u"terilen",
    u"timsah", u"tire", u"tombak", u"toprak", u"triko", u"tül", u"tunç", u"tüvit", u"vizon", u"yakut", u"yasemin",
    u"yün", u"yüsrü", u"zerrin", u"zeytin", u"zümrüt", u"goblen", u"ibrişim", u"kemik", u"kendir", u"lepiska",
    u"merserize", u"orlon", u"roza", u"tiftik"
]


class DictionaryTools(unittest.TestCase):

    def test_extract_interjections(self):
        skippers = []

        word_metas = self._extract_word_metas(skippers)

        for word, meta in word_metas:
            if any(['Interj' in m for m in meta]):
                print word, meta

    def test_print_some_words_with_metas(self):
        skippers = []

        word_metas = self._extract_word_metas(skippers)

        theList = u"""
        abanoz
        abide
        """

        words_to_search = [line.strip() for line in theList.strip().splitlines(False)]

        words = [word for word, x in word_metas]

        for word, meta in word_metas:
            if any(['Adj' in m for m in meta]):
                continue
            if word in words_to_search:
                if word + u"laşmak" in words or word + u"leşmek" in words:
                    print word, meta
                else:
                    continue

    def test_extract_nouns_which_are_also_adjectives(self):
        skippers = [
        ]

        word_metas = self._extract_word_metas(skippers)

        for word, meta in word_metas:
            if any(['Adj' in m for m in meta]) and any(['NOMETA' in m for m in meta]):
                print u'Word is Noun and Adj in T dictionary :\t', word, u'\t\t', meta

    def _extract_word_metas(self, skippers):
        dict_trie = self._get_dict_trie(skippers)

        word_metas = self._extract_meta_tuples(dict_trie)

        return word_metas

    def _extract_meta_tuples(self, dict_trie):
        word_meta_tuples = []
        for word_a, meta_a in dict_trie.iteritems():
            word_meta_tuples.append((word_a, meta_a))
        return word_meta_tuples

    def _get_dict_trie(self, skippers):
        dict_lines = self._gedict_lines(skippers)
        dict_trie = self._create_trie_from_dictionary_lines(dict_lines)
        return dict_trie

    def _gedict_lines(self, skippers):
        dict_lines = None
        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as dict:
            dict_lines = [line.strip() for line in dict]
        for skipper in skippers:
            dict_lines, z_dict_lines = skipper(dict_lines, dict_lines)
        return dict_lines

    def split_line(self, line):
        word, meta = (line[:line.find('[', 1)], line[line.find('[', 1):]) if '[' in line else (line, None)
        word = word.strip()
        meta = meta.strip() if meta else 'NOMETA'
        if meta != 'NOMETA':
            assert line == (word + u' ' + meta), line
        else:
            assert line == word, line
        return word, meta

    def _create_trie_from_dictionary_lines(self, dict_lines):
        dict_trie = trie()

        for line in dict_lines:
            line = line.strip()
            word, meta = self.split_line(line)
            if not dict_trie.has_key(word):
                dict_trie[word] = set()
            dict_trie[word].add(meta)

        return dict_trie

    def _get_local_resource(self, str):
        return os.path.join(os.path.dirname(__file__), str)

    def _get_trnltk_resource(self, str):
        return os.path.join(os.path.dirname(__file__), Trnltk_Resources_Folder + str)

    def _skip_words_with_tag_S(self, dict_lines):
        no_tag_S = lambda line: not "S:" in line
        dict_lines = filter(no_tag_S, dict_lines)
        return dict_lines

    def _skip_words_with_tag_Ref(self, dict_lines):
        no_ref = lambda line: not "Ref" in line
        dict_lines = filter(no_ref, dict_lines)
        return dict_lines

    def _skip_words_with_tag_Index(self, dict_lines):
        no_index = lambda line: not "Index" in line
        dict_lines = filter(no_index, dict_lines)
        return dict_lines

    def _skip_words_with_tag_RootSuffix(self, dict_lines):
        no_root_suffix = lambda line: not "RootSuffix" in line
        dict_lines = filter(no_root_suffix, dict_lines)
        return dict_lines

    def _skip_compounds(self, dict_lines):
        not_compound = lambda line: not "Compound" in line
        dict_lines = filter(not_compound, dict_lines)
        return dict_lines

    def _skip_duplicators(self, dict_lines):
        not_dup = lambda line: "Dup" not in line
        dict_lines = filter(not_dup, dict_lines)
        return dict_lines

    def _skip_comments(self, dict_lines):
        not_comment = lambda line: "#" not in line
        dict_lines = filter(not_comment, dict_lines)
        return dict_lines

    def _skip_postpositives_particles(self, dict_lines):
        dict_lines = filter(lambda line: "Part" not in line, dict_lines)
        return dict_lines

    def _skip_numerals(self, dict_lines):
        not_num = lambda line: "Num" not in line
        dict_lines = filter(not_num, dict_lines)
        return dict_lines

    def _skip_verbs(self, dict_lines):
        def not_verb(line):
            if "mek" not in line and "mak" not in line:
                return True
            else:
                if "Noun" in line:
                    return True
                else:
                    return False

        dict_lines = filter(not_verb, dict_lines)
        return dict_lines