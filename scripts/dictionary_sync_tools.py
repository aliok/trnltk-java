# coding=utf-8
import codecs
import difflib
import os
import unittest
import sys
from pytrie import SortedStringTrie as trie

Zemberek_Resources_Folder = "../../zemberek/src/main/resources/tr/"
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

class DictionarySyncTools(unittest.TestCase):
    def test_diff(self):
        z_dict_lines = None
        t_dict_lines = None

        with codecs.open(self._get_zemberek_resource("master-dictionary.dict"), encoding="utf-8") as z_dict:
            z_dict_lines = [line for line in z_dict]

        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as t_dict:
            t_dict_lines = [line for line in t_dict]

        t_dict_lines, z_dict_lines = self._skip_comments(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_words_with_tag_S(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_words_with_tag_Ref(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_words_with_tag_Index(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_words_with_tag_RootSuffix(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_compounds(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_duplicators(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_postpositives_particles(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_numerals(t_dict_lines, z_dict_lines)
        t_dict_lines, z_dict_lines = self._skip_verbs(t_dict_lines, z_dict_lines)

        print 'Lines to compare in Z dict', len(z_dict_lines)
        print 'Lines to compare in T dict', len(t_dict_lines)

        diff = difflib.unified_diff(z_dict_lines, t_dict_lines, 't.dict', 'z.dict', n=0)
        diff_lines = list(diff)

        print 'Diff lines', len(diff_lines)

        for d in diff_lines:
            sys.stdout.write(d)

    def test_extract_Z_interjections_which_dont_exist_in_T(self):
        skippers = []

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Interj' in m for m in z_meta]):
                # Z word is Interj
                if any(['Interj' in m for m in t_meta]):
                    # Z word is a noun and an adjective too, skip
                    continue
                else:
                    self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_T_interjections_which_dont_exist_in_Z(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Interj' in m for m in t_meta]):
                # T word is Interj
                if any(['Interj' in m for m in z_meta]):
                    # Z word is a noun and an adjective too, skip
                    continue
                else:
                    self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_T_interjections(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if any(['Interj' in m for m in t_meta]):
                print t_word, t_meta

    def test_print_some_words_with_metas(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        theList = u"""
        abanoz
        abide
        acar
        acayip
        acemi
        acı
        açık
        adi
        ad
        afacan
        afal
        ağaç
        ağda
        ağı
        ağır
        ağız
        ağ
        ahit
        ahmak
        akçıl
        akışkan
        ak
        aksi
        aktif
        alafranga
        alaturka
        alçak
        aleni
        alık
        al
        Alman
        altın
        Amerikalı
        anaç
        anarşist
        anık
        anı
        anıt
        anlamsız
        an
        anormal
        ant
        apse
        aptal
        arabesk
        Arap
        argo
        arık
        arı
        Arnavut
        arsız
        asabi
        asalak
        asi
        askerî
        asri
        aşağı
        avare
        Avrupalı
        aydın
        aykırı
        ayran
        ayrı
        ayrım
        azgın
        azman
        babacan
        badik
        bağdaşık
        bağım
        bağımlı
        bağımsız
        bağıt
        bağ
        bağlı
        bağnaz
        bakır
        balaban
        barbar
        bariz
        basit
        basmakalıp
        başka
        bayağı
        baygın
        bayındır
        bayır
        bayrak
        bayram
        bebek
        becel
        beceriksiz
        bedava
        bedbin
        bedii
        bek
        belirgin
        belir
        belirsiz
        bencil
        benek
        bengi
        berrak
        beter
        beton
        beyaz
        bezgin
        bıçkın
        biçimsiz
        bi
        bilgisayar
        bilinçsiz
        billur
        bir
        bitki
        blok
        bodur
        boğaz
        boğuk
        bok
        bol
        boncuk
        boynuz
        bozkır
        bölgesel
        bön
        bronz
        budala
        buğu
        buhar
        bukalemun
        bulanık
        bu
        bungun
        buruk
        buzağı
        buz
        buzul
        bücür
        bülbül
        bütün
        cadaloz
        cadı
        cam
        canavar
        cansız
        cazibe
        cazip
        cebel
        cehennem
        cemaat
        cendere
        cenk
        cennet
        cephe
        cılız
        cılk
        cıvık
        ciddi
        cilve
        cimri
        cin
        cisim
        coşkun
        cömert
        cüce
        çabuk
        çağcıl
        çağdaş
        çakır
        çakmak
        çamur
        çapkın
        çapraşık
        çapraz
        çarpık
        çatal
        çatık
        çayır
        çekingen
        çelik
        çene
        çete
        çetin
        çetrefil
        çevik
        çıban
        çılgın
        çıplak
        çiçek
        çifte
        çift
        çiğ
        çipil
        çirkef
        çirkin
        çiroz
        çizgi
        çocuk
        çopur
        çorak
        çökkün
        çöl
        çukur
        da
        dalgın
        dalkavuk
        damaksıl
        dargın
        dar
        dava
        dayanıksız
        dazlak
        değersiz
        değirmi
        dejenere
        demir
        demokratik
        demokrat
        denk
        densiz
        derin
        dermansız
        dernek
        dert
        destan
        destek
        dev
        deyim
        dır
        diftong
        diken
        dik
        dil
        dinamik
        dinç
        dindar
        dingin
        dinozor
        diri
        dişi
        divane
        doğal
        doğurgan
        do
        dolgun
        domuz
        donuk
        dost
        doygun
        dramatik
        durağan
        durgun
        duru
        duyarsız
        düşkün
        düşman
        düz
        ebedî
        ebleh
        edepsiz
        edilgen
        efe
        efsane
        eğ
        ehlî
        ek
        el
        enayi
        endüstri
        engin
        ergen
        ergin
        erkek
        esen
        eski
        esmer
        esnek
        eşek
        eşit
        eş
        eter
        etki
        etkin
        etkisiz
        evcil
        evrensel
        facia
        fakir
        farklı
        farksız
        faşist
        fazla
        federal
        fena
        fersiz
        fettan
        filozof
        firavun
        formül
        fosil
        Fransız
        Frenk
        gaga
        garip
        gâvur
        gaz
        gecekondu
        geçersiz
        geçimsiz
        gelenek
        geleneksel
        genç
        genel
        gen
        gerçek
        gergin
        gerilla
        gırtlak
        global
        göçebe
        göçmen
        göl
        grup
        güç
        güdük
        gülünç
        güncel
        gürbüz
        gür
        güzel
        haber
        hafif
        hain
        hak
        hâlsiz
        hamarat
        ham
        hamur
        hantal
        harap
        haşarı
        haşin
        hayır
        haylaz
        hayvan
        helal
        helezon
        helva
        hesap
        hımbıl
        hınzır
        hırçın
        hır
        hıyar
        hin
        hizip
        holding
        horoz
        hoş
        hovarda
        hödük
        Hristiyan
        huysuz
        hümanist
        ılık
        ırmak
        ıssız
        iddia
        ifrit
        ihtisas
        iki
        iktidarsız
        ilah
        ilginç
        ilke
        ilkel
        ilsiz
        imkânsız
        inat
        insancıl
        insan
        iri
        işaret
        itibarsız
        it
        ivedi
        iyi
        kabadayı
        kabak
        kaba
        kabuk
        kadavra
        kadın
        kadınsı
        kadife
        kadro
        kâfir
        kahpe
        kahraman
        kalabalık
        kalender
        kalın
        kalıp
        kalker
        kambur
        kamçı
        kamp
        kamusal
        kandil
        kangren
        kanser
        kansız
        kanun
        kapitalist
        karamsar
        karar
        karbon
        karı
        karmaşık
        karşı
        karşıt
        kartel
        kart
        kas
        katı
        katır
        kati
        katman
        katmer
        kavga
        kavi
        kavil
        kav
        kavram
        kaypak
        keçe
        keçi
        kekeme
        kelime
        kel
        kemik
        kent
        kentli
        keratin
        kerpiç
        kesin
        keskin
        keşik
        kılıbık
        kılıksız
        kıraç
        kırçıl
        kır
        kırmızı
        kırtıpil
        kısır
        kıt
        kıvırcık
        kıvrak
        kıyak
        kızgın
        kızıl
        kibar
        kireç
        kist
        kişi
        kitap
        klasik
        klik
        klişe
        kof
        kok
        kolay
        kolektif
        komik
        kooperatif
        korkunç
        kor
        koyu
        köhne
        kök
        köle
        kömür
        köpek
        kör
        kötü
        kötümser
        kötürüm
        köy
        köz
        kristal
        kronik
        kucak
        kul
        kural
        kurnaz
        kurşuni
        kurşun
        kurt
        kuru
        kurum
        kurumsal
        kutsal
        kutsi
        kutup
        kuzu
        küçük
        külçe
        küme
        küresel
        küskün
        küstah
        küt
        kütük
        laçka
        laik
        laubali
        legal
        liberal
        lif
        loş
        macun
        madara
        madde
        maddi
        madensel
        mafya
        magazin
        mahallî
        mahkeme
        mahmur
        mahzun
        makine
        mantar
        manyak
        maskara
        mat
        mavi
        maymun
        medeni
        mektup
        me
        melez
        merhaba
        merkezî
        merkez
        mermer
        meşru
        militan
        millî
        miskin
        mit
        moda
        modern
        monoton
        mor
        moruk
        muasır
        mum
        muşamba
        mutsuz
        muzır
        muzip
        müessese
        müstehcen
        müşkül
        müzmin
        nankör
        nasır
        nazik
        neftî
        nemrut
        nesnel
        netice
        net
        normal
        nöbet
        nötr
        obur
        odak
        odun
        okul
        olağan
        olanaksız
        olgun
        oluk
        operatör
        orak
        organ
        orman
        ormansız
        ortak
        otomatik
        ozon
        öbek
        öğür
        ölümsüz
        ötümlü
        ötümsüz
        özdeş
        özel
        özerk
        özgün
        özgür
        öz
        paçavra
        pahalı
        palaz
        pancar
        parlak
        parti
        pasif
        pas
        pay
        pazar
        pek
        peltek
        pelte
        pembe
        pençe
        perçin
        pes
        peynir
        pıhtı
        pısırık
        piç
        pinti
        polimer
        pratik
        profesyonel
        proleter
        put
        radikal
        randevu
        rast
        rasyonel
        resim
        rest
        revan
        rezil
        robot
        ruhsuz
        Rum
        sabit
        sabun
        saçma
        sade
        saf
        sağır
        sağlam
        sahi
        sakar
        sakız
        sakin
        salak
        saldırgan
        salgın
        saloz
        samimi
        sanayi
        sapık
        sarhoş
        sarı
        sarp
        sathi
        saydam
        seçkin
        selam
        sembol
        seme
        semiz
        sendika
        senet
        serin
        serseri
        sert
        sessiz
        sevimli
        sevimsiz
        seyrek
        sıcak
        sığ
        sık
        sınıf
        sıska
        silik
        simge
        sinsi
        sirke
        sistem
        sivil
        sivri
        siyah
        slogan
        softa
        soğuk
        solgun
        soluk
        somut
        sonsuz
        sorumsuz
        sosyal
        soysuz
        soyut
        sömürge
        söy
        söz
        standart
        sucuk
        sulu
        suskun
        süblim
        süfli
        sümsük
        sünger
        süngü
        süreğen
        sürtük
        süzgün
        şaban
        şaka
        şapşal
        şart
        şaşı
        şaşkın
        şeffaf
        şehir
        şehirli
        şeker
        şık
        şirket
        şirret
        şist
        tabii
        tabu
        tahta
        tanrı
        taşıl
        taş
        tatlı
        tatsız
        taze
        tazı
        tedirgin
        tekdüze
        tekel
        tek
        telefon
        tembel
        temel
        tenha
        terbiyesiz
        ters
        tetik
        tez
        tıkız
        ticari
        tilki
        tip
        tirit
        tirşe
        titiz
        titrek
        tiz
        toka
        tombul
        topak
        top
        toplum
        toplumsal
        toprak
        tortu
        tortul
        tos
        toz
        trajik
        tuhaf
        tunç
        turşu
        turuncu
        tutku
        tutucu
        tüccar
        tümör
        tümsek
        Türkçe
        Türk
        türkü
        uçuk
        uçurum
        u
        ur
        usta
        uyanık
        uydu
        uygar
        uy
        uysal
        uyuz
        uzak
        uz
        uzman
        üç
        ü
        ürkek
        üstün
        vahşi
        varsıl
        veda
        verimsiz
        viran
        yabancı
        yabanıl
        yabani
        yağı
        yakın
        yak
        yalçın
        yalın
        yalnız
        yapay
        yapı
        yapısal
        yaramaz
        yardım
        yasa
        yasal
        yassı
        yatkın
        yavan
        yavuz
        yaygın
        yayvan
        yedek
        yeğin
        yeni
        yerel
        yer
        yerli
        yetkin
        yıldız
        yiğit
        yobaz
        yoğun
        yoksul
        yoz
        yöresel
        yumruk
        yumuk
        yumuşak
        yuvarlak
        yüzey
        yüz
        yüzsüz
        zakkum
        zebun
        zengin
        zıt
        zirzop
        ziyade
        zor
        züğürt
        züppe
        """

        words_to_search = [line.strip() for line in theList.strip().splitlines(False)]

        t_words = [t_word for t_word, x, y in t_word_metas]

        for t_word, t_meta, z_meta in t_word_metas:
            if any(['Adj' in m for m in t_meta]):
                continue
            if t_word in words_to_search:
                if t_word + u"laşmak" in t_words or t_word + u"leşmek" in t_words:
                    print t_word, t_meta
                else:
                    continue

    def test_extract_T_nouns_which_are_also_T_adjectives_but_not_Z_adjectives(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and any(['NOMETA' in m for m in t_meta]):
                # T word is a noun and an adjective
                if any(['NOMETA' in m for m in z_meta]):
                    if any(['Adj' in m for m in z_meta]):
                        # Z word is a noun and an adjective too, skip
                        continue
                    else:
                        self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_Z_nouns_which_are_also_Z_adjectives_but_not_T_adjectives(self):
        skippers = []

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and any(['NOMETA' in m for m in z_meta]):
                # Z word is a noun and an adjective
                if any(['NOMETA' in m for m in t_meta]):
                    if any(['Adj' in m for m in t_meta]):
                        # T word is a noun and an adjective too, skip
                        continue
                    else:
                        self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_T_adjectives_which_are_also_T_adverbs_but_not_Z_adjectives(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and any(['Adv' in m for m in t_meta]):
                # T word is an adverb and an adjective
                if any(['Adj' in m for m in z_meta]):
                    if any(['Adv' in m for m in z_meta]):
                        # T word is a adverb and an adjective too, skip
                        continue
                    else:
                        self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_Z_adjectives_which_are_also_Z_adverbs_but_not_T_adjectives(self):
        skippers = []

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and any(['Adv' in m for m in z_meta]):
                # Z word is an adverb and an adjective
                if any(['Adj' in m for m in t_meta]):
                    if any(['Adv' in m for m in t_meta]):
                        # T word is an adverb and an adjective too, skip
                        continue
                    else:
                        self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_words_which_are_only_defined_in_Z(self):
        skippers = []

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if not t_meta:
                self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_words_which_are_only_defined_in_T(self):
        skippers = [
            self._skip_comments,
        ]

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if not z_meta:
                self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_Z_numerals(self):
        z_dict_lines, t_dict_lines = self._get_z_and_t_dict_lines([])

        for line in z_dict_lines:
            if 'P:Num' in line:
                print line

    def test_extract_T_numerals(self):
        z_dict_lines, t_dict_lines = self._get_z_and_t_dict_lines([])

        for line in t_dict_lines:
            if 'P:Num' in line:
                print line

    def test_extract_words_which_are_only_adverbs_in_T_but_only_nouns_in_Z(self):
        skippers = []

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adv' in m for m in t_meta]) and not any(['NOMETA' in m for m in t_meta]):
                # Z word is only an adverb
                if z_meta == set(['NOMETA']):
                    self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_words_which_are_only_adverbs_in_Z_but_only_nouns_in_T(self):
        skippers = [
        ]

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adv' in m for m in z_meta]) and not any(['NOMETA' in m for m in z_meta]):
                # Z word is only an adverb
                if t_meta == set(['NOMETA']):
                    self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_words_which_are_only_adjectives_in_T_but_only_nouns_in_Z(self):
        skippers = [
        ]

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and not any(['NOMETA' in m for m in t_meta]):
                # Z word is only an adjective
                if z_meta == set(['NOMETA']):
                    self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_words_which_are_only_adjectives_in_Z_but_only_nouns_in_T(self):
        skippers = [
        ]

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and not any(['NOMETA' in m for m in z_meta]):
                # Z word is only an adjective
                if t_meta == set(['NOMETA']):
                    self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_Z_adjectives_which_are_not_only_adjectives_in_T(self):
        skippers = [
        ]

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and not any(['NOMETA' in m for m in z_meta]):
                # Z word is only an adjective
                if any(['Adj' in m for m in t_meta]):
                    if not any(['NOMETA' in m for m in t_meta]):
                        # T word is a adjective and not a noun too, skip
                        continue
                    else:
                        self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_T_adjectives_which_are_not_only_adjectives_in_Z(self):
        skippers = [
        ]

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and not any(['NOMETA' in m for m in t_meta]):
                # Z word is only an adjective
                if any(['Adj' in m for m in z_meta]):
                    if not any(['NOMETA' in m for m in z_meta]):
                        # T word is a adjective and not a noun too, skip
                        continue
                    else:
                        self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_extract_Z_nouns_which_are_also_Z_and_T_adjectives_but_not_T_nouns(self):
        skippers = [
        ]

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if z_meta == t_meta or not t_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and any(['NOMETA' in m for m in z_meta]):
                # Z word is a noun and an adjective
                if any(['Adj' in m for m in t_meta]):
                    if any(['NOMETA' in m for m in t_meta]):
                        # T word is a noun and an adjective too, skip
                        continue
                    else:
                        self._print_z_and_line_status_for_word(z_word, z_meta, t_meta, print_all=True)

    def test_extract_T_nouns_which_are_also_Z_and_T_adjectives_but_not_Z_nouns(self):
        skippers = [
        ]

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if t_meta == z_meta or not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and any(['NOMETA' in m for m in t_meta]):
                # T word is a noun and an adjective
                if any(['Adj' in m for m in z_meta]):
                    if any(['NOMETA' in m for m in z_meta]):
                        # Z word is a noun and an adjective too, skip
                        continue
                    else:
                        self._print_t_and_line_status_for_word(t_word, t_meta, z_meta, print_all=True)

    def test_should_extract_words_which_must_be_T_noun_and_also_adjective_but_not(self):
        skippers = [
        ]

        z_dict_trie, t_dict_trie = self._get_z_and_t_dict_tries(skippers)

        for word in RawMaterials:
            meta = t_dict_trie.get(word)
            if not meta:
                print u'Word "{}" is not in the dictionary'.format(word)
                continue

            if '[A:InverseHarmony]' in meta:
                meta.remove('[A:InverseHarmony]')
                meta.add('NOMETA')
            if '[A:Voicing]' in meta:
                meta.remove('[A:Voicing]')
                meta.add('NOMETA')
            if '[A:NoVoicing]' in meta:
                meta.remove('[A:NoVoicing]')
                meta.add('NOMETA')

            if any(['Adj' in m for m in meta]) and any(['NOMETA' in m for m in meta]):
                pass
            else:
                print u'Word "{}" is not Noun and Adj, but {}'.format(word, str(meta))

    def test_should_extract_words_which_must_be_Z_noun_and_also_adjective_but_not(self):
        skippers = [
        ]

        z_dict_trie, t_dict_trie = self._get_z_and_t_dict_tries(skippers)

        for word in RawMaterials:
            meta = z_dict_trie.get(word)
            if not meta:
                print u'Word "{}" is not in the dictionary'.format(word)
                continue

            if '[A:InverseHarmony]' in meta:
                meta.remove('[A:InverseHarmony]')
                meta.add('NOMETA')
            if '[A:Voicing]' in meta:
                meta.remove('[A:Voicing]')
                meta.add('NOMETA')
            if '[A:NoVoicing]' in meta:
                meta.remove('[A:NoVoicing]')
                meta.add('NOMETA')

            if any(['Adj' in m for m in meta]) and any(['NOMETA' in m for m in meta]):
                pass
            else:
                print u'Word "{}" is not Noun and Adj, but {}'.format(word, str(meta))

    def test_should_extract_words_which_must_be_Z_and_T_nouns_and_also_adjective_but_not(self):
        skippers = [
        ]

        z_dict_trie, t_dict_trie = self._get_z_and_t_dict_tries(skippers)

        for word in RawMaterials:
            z_meta = z_dict_trie.get(word)
            if not z_meta:
                print u'Word "{}" is not in the dictionary'.format(word)
                continue

            if '[A:InverseHarmony]' in z_meta:
                z_meta.remove('[A:InverseHarmony]')
                z_meta.add('NOMETA')
            if '[A:Voicing]' in z_meta:
                z_meta.remove('[A:Voicing]')
                z_meta.add('NOMETA')
            if '[A:NoVoicing]' in z_meta:
                z_meta.remove('[A:NoVoicing]')
                z_meta.add('NOMETA')

            t_meta = t_dict_trie.get(word)

            if '[A:InverseHarmony]' in t_meta:
                t_meta.remove('[A:InverseHarmony]')
                t_meta.add('NOMETA')
            if '[A:Voicing]' in t_meta:
                t_meta.remove('[A:Voicing]')
                t_meta.add('NOMETA')
            if '[A:NoVoicing]' in t_meta:
                t_meta.remove('[A:NoVoicing]')
                t_meta.add('NOMETA')

            if t_meta == z_meta:
                pass
            else:
                print u'Word "{}" should be Noun and Adj, but different in Z {} and T {}'.format(word, str(z_meta),
                    str(t_meta))

    def test_extract_T_nouns_which_are_also_T_adjectives(self):
        skippers = [
        ]

        t_word_metas = self._extract_t_word_metas(skippers)

        for t_word, t_meta, z_meta in t_word_metas:
            if not z_meta:
                continue

            if any(['Adj' in m for m in t_meta]) and any(['NOMETA' in m for m in t_meta]):
                print u'Word is Noun and Adj in T dictionary :\t', t_word, u'\t\t', t_meta

    def test_extract_Z_nouns_which_are_also_Z_adjectives(self):
        skippers = [
        ]

        z_word_metas = self._extract_z_word_metas(skippers)

        for z_word, z_meta, t_meta in z_word_metas:
            if not z_meta:
                continue

            if any(['Adj' in m for m in z_meta]) and any(['NOMETA' in m for m in z_meta]):
                print u'Word is Noun and Adj in Z dictionary :\t', z_word, u'\t\t', z_meta

    def _extract_t_word_metas(self, skippers):
        z_dict_trie, t_dict_trie = self._get_z_and_t_dict_tries(skippers)

        t_word_metas = self._extract_meta_tuples(t_dict_trie, z_dict_trie)

        return t_word_metas

    def _extract_z_word_metas(self, skippers):
        z_dict_trie, t_dict_trie = self._get_z_and_t_dict_tries(skippers)

        z_word_metas = self._extract_meta_tuples(z_dict_trie, t_dict_trie)

        return z_word_metas

    def _extract_meta_tuples(self, dict_trie_a, dict_trie_b):
        word_meta_tuples = []
        for word_a, meta_a in dict_trie_a.iteritems():
            meta_b = dict_trie_b.get(word_a)
            word_meta_tuples.append((word_a, meta_a, meta_b))
        return word_meta_tuples

    def _get_z_and_t_dict_tries(self, skippers):
        z_dict_lines, t_dict_lines = self._get_z_and_t_dict_lines(skippers)
        z_dict_trie = self._create_trie_from_dictionary_lines(z_dict_lines)
        t_dict_trie = self._create_trie_from_dictionary_lines(t_dict_lines)
        return z_dict_trie, t_dict_trie

    def _get_z_and_t_dict_lines(self, skippers):
        z_dict_lines = None
        t_dict_lines = None
        with codecs.open(self._get_zemberek_resource("master-dictionary.dict"), encoding="utf-8") as z_dict:
            z_dict_lines = [line.strip() for line in z_dict]
        with codecs.open(self._get_trnltk_resource("master-dictionary.dict"), encoding="utf-8") as t_dict:
            t_dict_lines = [line.strip() for line in t_dict]
        for skipper in skippers:
            t_dict_lines, z_dict_lines = skipper(t_dict_lines, z_dict_lines)
        return z_dict_lines, t_dict_lines

    def _print_t_and_line_status_for_word(self, t_word, t_meta, z_meta, print_all=False,
                                          print_different_into_in_z=False,
                                          print_less_info_in_z=False, print_more_info_in_z=False,
                                          print_no_line_in_z=False, print_same_line_in_z=False):
        return self._print_line_status_for_word(t_word, t_meta, z_meta, 'T', 'Z', print_all, print_different_into_in_z,
            print_less_info_in_z, print_more_info_in_z, print_no_line_in_z, print_same_line_in_z)

    def _print_z_and_line_status_for_word(self, z_word, z_meta, t_meta, print_all=False,
                                          print_different_into_in_z=False,
                                          print_less_info_in_z=False, print_more_info_in_z=False,
                                          print_no_line_in_z=False, print_same_line_in_z=False):
        return self._print_line_status_for_word(z_word, z_meta, t_meta, 'Z', 'T', print_all, print_different_into_in_z,
            print_less_info_in_z, print_more_info_in_z, print_no_line_in_z, print_same_line_in_z)

    def _print_line_status_for_word(self, word_a, meta_a, meta_b, name_a, name_b, print_all=False,
                                    print_different_into_in_b=False,
                                    print_less_info_in_b=False, print_more_info_in_b=False,
                                    print_no_line_in_b=False, print_same_line_in_b=False):
        print_different_into_in_b = print_all or print_different_into_in_b
        print_less_info_in_b = print_all or print_less_info_in_b
        print_more_info_in_b = print_all or print_more_info_in_b
        print_no_line_in_b = print_all or print_no_line_in_b
        print_same_line_in_b = print_all or print_same_line_in_b

        meta_a_str = u','.join(meta_a)
        meta_b_str = u','.join(meta_b) if meta_b else None
        if not meta_b:
            if print_no_line_in_b:
                print u"{} doesn't have word : {} '{}'".format(name_b, word_a, meta_a_str)
        elif meta_b == meta_a:
            if print_same_line_in_b:
                print u"Same {} line : {} '{}', matching lines in {} : '{}'".format(name_a, word_a, meta_a_str,
                    name_b,
                    meta_b_str)
        elif meta_a.issubset(meta_b):
            if print_more_info_in_b:
                print u"{} has more info for {} line : {} '{}', matching lines in {} : '{}'".format(name_b, name_a,
                    word_a, meta_a_str, name_b, meta_b_str)
        elif meta_b.issubset(meta_a):
            if print_less_info_in_b:
                print u"{} has less info for {} line : {} '{}', matching lines in {} : '{}'".format(name_b, name_a,
                    word_a, meta_a_str, name_b, meta_b_str)
        else:
            if print_different_into_in_b:
                print u"{} has different info for {} line : {} '{}', matching lines in {} : '{}'".format(name_b,
                    name_a,
                    word_a,
                    meta_a_str, name_b, meta_b_str)

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

    def _get_zemberek_resource(self, str):
        return os.path.join(os.path.dirname(__file__), Zemberek_Resources_Folder + str)

    def _skip_words_with_tag_S(self, t_dict_lines, z_dict_lines):
        no_tag_S = lambda line: not "S:" in line
        z_dict_lines = filter(no_tag_S, z_dict_lines)
        t_dict_lines = filter(no_tag_S, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_words_with_tag_Ref(self, t_dict_lines, z_dict_lines):
        no_ref = lambda line: not "Ref" in line
        z_dict_lines = filter(no_ref, z_dict_lines)
        t_dict_lines = filter(no_ref, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_words_with_tag_Index(self, t_dict_lines, z_dict_lines):
        no_index = lambda line: not "Index" in line
        z_dict_lines = filter(no_index, z_dict_lines)
        t_dict_lines = filter(no_index, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_words_with_tag_RootSuffix(self, t_dict_lines, z_dict_lines):
        no_root_suffix = lambda line: not "RootSuffix" in line
        z_dict_lines = filter(no_root_suffix, z_dict_lines)
        t_dict_lines = filter(no_root_suffix, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_compounds(self, t_dict_lines, z_dict_lines):
        not_compound = lambda line: not "Compound" in line
        z_dict_lines = filter(not_compound, z_dict_lines)
        t_dict_lines = filter(not_compound, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_duplicators(self, t_dict_lines, z_dict_lines):
        not_dup = lambda line: "Dup" not in line
        z_dict_lines = filter(not_dup, z_dict_lines)
        t_dict_lines = filter(not_dup, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_comments(self, t_dict_lines, z_dict_lines):
        not_comment = lambda line: "#" not in line
        z_dict_lines = filter(not_comment, z_dict_lines)
        t_dict_lines = filter(not_comment, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_postpositives_particles(self, t_dict_lines, z_dict_lines):
        z_dict_lines = filter(lambda line: "Postp" not in line, z_dict_lines)
        t_dict_lines = filter(lambda line: "Part" not in line, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_numerals(self, t_dict_lines, z_dict_lines):
        not_num = lambda line: "Num" not in line
        z_dict_lines = filter(not_num, z_dict_lines)
        t_dict_lines = filter(not_num, t_dict_lines)
        return t_dict_lines, z_dict_lines

    def _skip_verbs(self, t_dict_lines, z_dict_lines):
        def not_verb(line):
            if "mek" not in line and "mak" not in line:
                return True
            else:
                if "Noun" in line:
                    return True
                else:
                    return False

        z_dict_lines = filter(not_verb, z_dict_lines)
        t_dict_lines = filter(not_verb, t_dict_lines)
        return t_dict_lines, z_dict_lines