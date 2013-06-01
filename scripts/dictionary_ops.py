# coding=utf-8
import codecs
import os
import unittest

Trnltk_Resources_Folder = "../core/src/main/resources/"


def word_ends_with_one_of(line, endings):
    word = ""
    if '[' in line:
        word = line.split('[')[0].strip()
    else:
        word = line
    return any([word.endswith(e) for e in endings])


ends_with_giller = lambda line: word_ends_with_one_of(line, [u"giller"])
ends_with_lerLar = lambda line: word_ends_with_one_of(line, [u"ler", u"lar"])
ends_with_enAn = lambda line: word_ends_with_one_of(line, [u"en", u"an"])
ends_with_lanmakLenmek = lambda line: word_ends_with_one_of(line, [u"lenmek", u"lanmak"])
ends_with_siSu = lambda line: word_ends_with_one_of(line, [u"si", u"su", u"sı", u"sü"])
ends_with_likLuk = lambda line: word_ends_with_one_of(line, [u"lik", u"luk", u"lık", u"lük"])
ends_with_caCe = lambda line: word_ends_with_one_of(line, [u"ca", u"ce", u"ça", u"çe"])
ends_with_casinaCesine = lambda line: word_ends_with_one_of(line, [u"casına", u"cesine", u"çasına", u"çesine"])
ends_with_dirmakDirmek = lambda line: word_ends_with_one_of(line,
    [u"dırmak", u"dirmek", u"tırmak", u"tirmek", u"durmak", u"dürmek", u"turmak", u"türmek"])
ends_with_tmakTmek = lambda line: word_ends_with_one_of(line, [u"tmak", u"tmek"])
ends_with_rmakRmek = lambda line: word_ends_with_one_of(line, [u"rmak", u"rmek"])
ends_with_ilmakIlmek = lambda line: word_ends_with_one_of(line, [u"ılmak", u"ilmek", u"ulmak", u"ülmek"])
ends_with_mazMez = lambda line: word_ends_with_one_of(line, [u"maz", u"mez"])
ends_with_misMus = lambda line: word_ends_with_one_of(line, [u"mış", u"miş", u"muş", u"müş"])
ends_with_ciCu = lambda line: word_ends_with_one_of(line, [u"cı", u"ci", u"cu", u"cü", u"çı", u"çi", u"çu", u"çü"])


class DictionaryOps(unittest.TestCase):
    def test_should_print_lines_ending_with_lAsmAk_in_TDK_dict(self):
        with codecs.open("C:\\Users\\ali\\Desktop\\New folder\\tdk-dic.xml", encoding="utf-8") as tdk_dict:
            for line in tdk_dict:
                line = line.strip()
                #<unit name="anaçlaşmak">
                if not line.startswith('<unit name="'):
                    continue
                else:
                    index_of_second_quote = line.find('"', len('<unit name="'))
                    word = line[len('<unit name="'):index_of_second_quote]
                    if word.endswith(u'laşmak') or word.endswith(u'leşmek'):
                        print word

    def test_should_print_lines_ending_with_Arabic_Ayn_in_TDK_dict(self):
        # lines matching the regex <unit name="\p{L}+[aeiouıüö]" origin="Arap.+¤" are the matches
        with codecs.open("C:\\Users\\ali\\Desktop\\New folder\\tdk-dic.xml", encoding="utf-8") as tdk_dict:
            for line in tdk_dict:
                line = line.strip()
                #<unit name="anaçlaşmak">
                if not line.startswith('<unit name="') or u'origin="Arapça' not in line or u'¤"' not in line:
                    continue
                else:
                    index_of_unit_end_quote = line.find('"', len('<unit name="'))
                    word = line[len('<unit name="'):index_of_unit_end_quote]
                    index_of_origin_end_quote = line.find('"', len(' origin="') + index_of_unit_end_quote + 1)
                    origin = line[len(' origin="') + index_of_unit_end_quote +1 :index_of_origin_end_quote]

                    if not origin.endswith(u'¤'):
                        continue
                    if not word[-1] in u'aeıioöuü':
                        continue

                    print word

    def test_should_print_duplicate_lines_in_trnltk_master_dictionary(self):
        master_dict_lines = set()
        duplicates = []

        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as master_dict:
            for line in master_dict:
                line = line.strip()
                if line in master_dict_lines:
                    duplicates.append(line)
                else:
                    master_dict_lines.add(line)
        for line in duplicates:
            print line

    @unittest.skip("skipped")
    def test_should_remove_lines_from_trnltk_master_dictionary(self):
        lines_to_remove = u"""
        yöneltmek
        yürütmek
        """.strip().splitlines(False)

        lines_to_remove = [l.strip() for l in lines_to_remove]

        def in_lines(line):
            return line in lines_to_remove

        self._process_trnltk_master_dictionary(in_lines)

    @unittest.skip("skipped")
    def test_should_add_attr_ayn_to_some_words(self):
        # following words are extracted using "test_should_print_lines_ending_with_Arabic_Ayn_in_TDK_dict"

        txt_words_ending_with_ayn = u"""
        bayi
        bedayi
        bittabi
        cami
        cami
        cemi
        cima
        darülbedayi
        elveda
        emrivaki
        enva
        feci
        filvaki
        füru
        gayrimeşru
        hissikablelvuku
        huşu
        ıttıla
        ibda
        icma
        içtima
        ihtira
        ikna
        indifa
        inkıta
        intifa
        irca
        irtica
        irtifa
        işba
        izaleişüyu
        kani
        layenkati
        makta
        maktu
        mâni
        masnu
        matbu
        matla
        mayi
        mecmu
        memba
        memnu
        menafi
        merci
        mesmu
        meşbu
        meşru
        meta
        metbu
        mevdu
        mevki
        mevzi
        mevzu
        mezra
        mezru
        mısra
        mudi
        muhteri
        murabba
        murassa
        musanna
        muşamba
        muti
        muttali
        müdafi
        mülemma
        mümteni
        mürteci
        mürtefi
        mütenevvi
        mütetebbi
        mütevazı
        müvezzi
        nafi
        nane
        niza
        raci
        rücu
        sanayi
        seci
        sema
        seri
        şayi
        şeci
        şeni
        şüyu
        tabi
        tabi
        takti
        tasannu
        tasdi
        tasni
        tazarru
        teberru
        tecemmu
        temettü
        tenevvü
        terbi
        terfi
        tesri
        teşci
        teşri
        teşyi
        tetebbu
        tevabi
        tevazu
        tevdi
        tevessü
        tevki
        tevsi
        tevzi
        tulu
        vaki
        vâsi
        veca
        veda
        vuku
        zayi
        zürra
        """

        words_ending_with_ayn = [word.strip() for word in txt_words_ending_with_ayn.strip().splitlines(False)]

        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as dict_file:
            with codecs.open(self._get_local_resource("new-master-dictionary.dict"), mode="w", encoding="utf-8") as new_dict_file:
                for line in dict_file:
                    stripped_line = line.strip()
                    if stripped_line.startswith('#'):
                        continue

                    word, meta = self.split_dict_line(stripped_line)
                    if word in words_ending_with_ayn:
                        if meta==u'NOMETA':
                            new_dict_file.write(word + u" [A:EndsWithAyn]\n")
                        elif meta==u'[P:Adj]':
                            new_dict_file.write(word + u" [P:Adj; A:EndsWithAyn]\n")
                        elif meta==u'[P:Adv]':
                            new_dict_file.write(word + u" [P:Adv; A:EndsWithAyn]\n")
                        elif meta==u'[P:Interj]':
                            new_dict_file.write(word + u" [P:Interj; A:EndsWithAyn]\n")
                        else:
                            print "Line is not written in the result file " + stripped_line

                        print stripped_line

    @unittest.skip("skipped")
    def test_should_remove_words_with_giller_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_giller)

    @unittest.skip("skipped")
    def test_should_remove_words_with_lerLar_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_lerLar)

    @unittest.skip("skipped")
    def test_should_remove_words_with_enAn_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_enAn)

    @unittest.skip("skipped")
    def test_should_remove_words_with_lanmakLenmek_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_lanmakLenmek)

    @unittest.skip("skipped")
    def test_should_remove_words_with_siSu_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_siSu)

    @unittest.skip("skipped")
    def test_should_remove_words_with_likLuk_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_likLuk)

    @unittest.skip("skipped")
    def test_should_remove_words_with_caCe_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_caCe)

    @unittest.skip("skipped")
    def test_should_remove_words_with_casinaCesine_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_casinaCesine)

    @unittest.skip("skipped")
    def test_should_remove_words_with_dirmakDirmek_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_dirmakDirmek)

    @unittest.skip("skipped")
    def test_should_remove_words_with_tmakTmek_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_tmakTmek)

    @unittest.skip("skipped")
    def test_should_remove_words_with_rmakRmek_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_rmakRmek)

    @unittest.skip("skipped")
    def test_should_remove_words_with_ilmakIlmek_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_ilmakIlmek)

    @unittest.skip("skipped")
    def test_should_remove_words_with_mazMez_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_mazMez)

    @unittest.skip("skipped")
    def test_should_remove_words_with_misMus_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_misMus)

    @unittest.skip("skipped")
    def test_should_remove_words_with_ciCu_from_trnltk_master_dictionary(self):
        self._process_trnltk_master_dictionary(ends_with_ciCu)

    def _process_trnltk_master_dictionary(self, function):
        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as dict_file:
            with codecs.open(self._get_local_resource("new-master-dictionary.dict"), mode="w",
                encoding="utf-8") as new_dict_file:
                for line in dict_file:
                    if function(line.strip()):
                        print line.strip()
                    else:
                        new_dict_file.write(line)

    def _get_local_resource(self, str):
        return os.path.join(os.path.dirname(__file__), str)

    def _get_trnltk_resource(self, str):
        return os.path.join(os.path.dirname(__file__), Trnltk_Resources_Folder + str)

    def split_dict_line(self, line):
        word, meta = (line[:line.find('[', 1)], line[line.find('[', 1):]) if '[' in line else (line, None)
        word = word.strip()
        meta = meta.strip() if meta else 'NOMETA'
        if meta != 'NOMETA':
            assert line == (word + u' ' + meta), line
        else:
            assert line == word, line
        return word, meta