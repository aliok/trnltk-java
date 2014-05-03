Kararlar
============================

- SIFATLAR
    - Isme ek gelmedigi surece donusmuyor.


- dayi, baba, amca vs --> Sadece isim! 'Cok baba adamsin' gibi durumlari daha sonra cikaracaz. 'Demir kapi' da buna benziyor. Bunlari
daha sonra ayirdetmek icin yalin haliyle baska bir isimden once olan isimlere bakabiliriz.


- sokakları --> Acc durumu zaten var. Possession durumu icin, 'P3sp' diye ozel birsey kullan. Bu su demek:
    - Onun guzel sokaklari (onun 5 tane sokagi var)           : A3sg+P3pl(lAr!I)+Nom
    - Onlarin guzel sokaklari (herkesin tek sokagi var)       : A3pl(lAr)+P3sg(+sI)+Nom
    - Onlarin guzel sokaklari (herkesin 5 sokagi var)         : A3pl(lAr)+P3pl(!I)+Nom"
    - **Sadece su olacak : A3pl(lAr)+P3sp(!I)+Nom**

- kirmizi --> Sifatlari yalin halde isim olarak degerlendirme. Ama, 'kirmizinin' olursa, o zaman isme cevir ve ondan sonra ekle.
Bu da daha sonradan cikarabilecegimiz bir bilgi!


- yapar / yaparlar --> Aor'un sadece A3sg ve A3pl hali icin bir de sifat sonuc ver. Bkz : yaparim
    - uyanir gibi oldum
    - dayanilmaz aci
    - kosar adimlar / uretebilir hale
    - bilmez degilim
    - tartisilir konu
    - kullanilabilir hal
    - kullanilabilirleri getir

- yapar --> cifte Aor durumunu engelle   **TAMAM**
    - bekler : bekle+Aor(+Ir)
    - bekler : bekle+Aor(+Ar)
    - ** bekle+Aor(+Ar) ** oldu

- yaparim --> Sadece fiil halini kullan! Aor'dan sonra ek gelince aslinda baska bisey kastedilmis oluyor hep. Bkz : yapar
    - yaparim : Aor+A1sg
    - neden tartisilirim? : Aor+A1sg


- yapmakta --> Fiil ve isim hali. 'yapmakta diretiyor' durumundan dolayi. isim halinin ek almamasi lazim!
    - o bu isi su anda yapmakta.
    - o bu isi yapmakta diretiyor.

- yapmaktayim --> Sadece fiil hali donder! Gercekten isim olan durumlar da olabilir. Ama bunlar kaliplasmis seyler ve sozlukte olmalari lazim.
Bkz : yapmakta


- yapacak vs. asagi yukari her zaman Verb, Adj+Fut, Adj+FutPart ve Noun+FutPart durumlari mumkun oluyor. En guzeli bunlari elemeyip training set'e cumle eklemek olacak.
    - Verb         : ben yapacagim.
    - Adj+Fut      : yapacak kisi / benim yapacagimi getir (Adj+Fut+Noun+P1sg+Acc)
    - Adj+FutPart  : yapacagim is
    - Noun+FutPart : yapacagimi soyledim

    - **hep 3. tekil sahis durumlari sunlar:**
    - adam onu yapacak                              : fiil
    - yapacak is                                    : sifat, Part degil
    - yapacagi is / burada kalacagi cagir           : sifat, Part degil
    - yapacagi zaman                                : sifat Part
    - kimin yapacagina karar verdim                 : sifat Part
    - onu yapacagina bunu yapsin                    : sifat Part
    - onu yapacagini soyledi                        : sifat Part

    - ben onu yapacagim                             : fiil
    - yapacagim is                                  : sifat, Part degil
    - onu yapacagima bunu yaparim                   : sifat Part
    - yapacagim zaman / yapacagimi soyledim         : sifat Part

    - sen onu yapacaksin                            : fiil
    - yapacagin is / yapacagini cagir               : sifat, Part degil
    - bunu yapacagin zaman / yapacagini soyledin    : sifat Part


- yapacagiz --> Sadece fiil donder. Sifat durumu aslinda hep fiil.


- yaptik --> Bkz: yapacak . yapacak'a benziyor. ama 'yapti' degil 'yaptik' yalin sifat veya Part oluyor.
    - yaptigim is
    - yaptigi is
    - yaptigi ver : sacma durum. Dolayisiyla PastPart+Adj+..+Acc mumkun degil
    - yaptigimi soyledim
    - yaptik : Hem PastPart hem de Past olabilen tek durum aslinda. Digerleri farkli. Mesela : 'yaptim' vs 'yaptigim'
    - bildik durum


- yapmisim --> Bkz: 'yapar' ve 'yaparim'
    - yapmis            : Fiil + Sifat
    - yapmisim          : Fiil
    - yapilmis duvar    : Fiil + Sifat
    - yapilmisim        : Fiil



- yalin fiil + degil --> Tum fiil durumlari icin bu mumkun. Bunu contextless bir parser ile halletmek cok zor. Daha sonraki asamalarda bunu Fiil+Tense+Neg seklinde algilayacak biseyler yapabiliriz.
    - yapar degilim
    - bilmez degilim
    - geliyor degilim
    - bilmiyor degilim
    - bilmis degilim
    - bilmemis degilim
    - yapacak degilim
    - yapmayacak degilim
    - vs.


- yapmam --> Fiil ve isim. 'yapmaz' durumunun ek almamasi lazim. 'yapmazim' sacma oluyor. Bu yuzden 'yapmazsin' durumunun sadece Fiil donmesi lazim.
    - yapmam            : Fiil (aslinda isim hali de var : yapma + m)
    - yapmazsin         : Fiil
    - yapmaz            : Fiil + ek almayan Sifat
    - yapmazlar         : Fiil


- yapmamakta --> Bkz : yapmakta
    - o bu isi yapmamakta.            : aslinda fiil
    - o bu isi yapmamakta diretiyor   : aslinda isim


- yapmamaktayim --> Bkz : yapmakta ve yapmaktayim


- elerdim --> Aslinda Aor[+Ar] ve Aor[+Ir] eklerinin ikisi de uyuyor. Eger ikisi de uyuyorsa, bunlari Aor[+r] durumuna dusur. Bu parse'tan sonra yapilacak bir is olsa gerek.



- o --> Pers veya Demons diye siniflama. Sadece Pron.


- onlar --> Bkz : o


- kim / kimi / kimisi / kimileri -->        Buna karar veremedim. Buyuk ihtimalle kim, kimi, kimisi ve kimileri icin predefined path tanimlayacam
    - kim geldi?     :                                                                   : kesin -> kim+Soru
    - kim geldiyse   :                                                                   : kesin -> kim+Soru
    - kimi insan     :                                                                   : kesin -> kimi = bazi
    - kimim          :
    - kimin          : kimin kimsen / kimin arabasi                                      : kimim'deki durum / Genitive durumu.
    - kimimiz        :
    - kiminiz        :
    - kimilerimiz    :
    - kimileriniz    :
    - kimisi         : tek mumkun durum kimi+                                                                   : kesin -> kimi = bazi
    - kimileri       : zamir
    - kimleri        : kimleri aradin?


- yapismak --> Recip durumunu engelle!


- Passive indirgeme
    - yenildi --> +nil ve +inil durumlarini reduce et ikisi de varsa eger!
        - ye + +InIl + di
        - ye + +nIl + di
        - -> ye + +nIl + di olsun sadece
    - ot

-


- yerlesmek --> Become durumunu engelle!
    - yer+Become durumunu engelle!


- yapmamali / kortutmamali --> -li ekinin Neg(ma)+Inf(ma) arkasindan gelmemesi lazim. Pozitif hali mumkun:
    - korkutmali     : korkutma + li mumkun
    - korkutmamali   : korkutmama + li pek degil

- Recip --> Sg mumkun degil!
    - opustuk
    - bakistiniz
    - kavgaya tutustular

    - Become durumu ile karistirmamak lazim : kufurlestik

- yaparcasina --> yap+ar+ca+si+na durumunu engelle!
    - yaparcasina : yap+ar+casina
    - onun yaparcasi+na mumkun degil!

- yapmamiscasina --> yap+ma+mis+ca+si+na durumunu engelle!
Bkz: yaparcasina

- kirmizimsi --> Sadece kirmizi+Adj+Noun+Zero+Adj+JustLike durumuna izin ver. Yani sifattan sifat uremesin, sifattan isim uresin sonra isimden sifat uresin.
    - kirmizimsi : kirmizi+Adj -> Noun+Zero -> Adj+JustLike
    - catalimsi  : catal+Noun -> Adj+JustLike


- çoğu / cogu --> Sifat ve zamir olabiliyor. Ama ek alinca direk zamir oluyor. Aynisi bircogu icin de gecerli.
    - cogu kitap                 : sifat
    - cogu boyle yapiyor         : zamir
    - onlarin cogu boyle yapiyor : zamir      (sifat gibi gorunuyor ama 'onlarin bircogu' zamir oldugu icin bu da oyle olsun)
    - onlarin cogunun            : zamir
    - bizim cogumuz boyle yapiyor: zamir
    - benim cogum / cogumun      : N/A
    - senin cogun / cogunun      : N/A



Yapilacaklar
===================
- dayi, baba vs kelimeler sadece Adj olacak!
