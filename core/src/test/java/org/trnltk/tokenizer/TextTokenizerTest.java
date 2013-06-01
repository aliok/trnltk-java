package org.trnltk.tokenizer;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.trnltk.tokenizer.TextTokenizer;
import org.trnltk.util.RegexMatcher;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TextTokenizerTest {
    static TextTokenizer tokenizer;

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException {
        final TokenizationGraph graph = TextTokenizerTrainer.buildDefaultTokenizationGraph(true);
        tokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .strict()
                .build();
    }

    @Test
    public void shouldTokenizeSimpleText() {
        {
            final String text = "Fiyatları uçuşa geçti.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Fiyatları", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "Fiyatları uçuşa geçti. ";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Fiyatları", "uçuşa", "geçti", ".")));
        }
        {
            final String text = " Fiyatları uçuşa geçti.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Fiyatları", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "Fiyatları uçuşa geçti .";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Fiyatları", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "\r\t\nFiyatları uçuşa geçti .   ";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Fiyatları", "uçuşa", "geçti", ".")));
        }
    }

    @Test
    public void shouldTokenizeParenthesisQuotesText() {
        {
            final String text = "(Fiyatları) \"arttı.\"";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("(", "Fiyatları", ")", "\"", "arttı", ".", "\"")));
        }
        {
            final String text = "[5.]";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Joiner.on(" ").join(tokens), equalTo("[ 5. ]"));
        }
    }

    @Test
    public void shouldTokenizeTextWithExceptionalCase_1() {
        {
            final String text = "\r\tBen 3. adam.\t\r\n\t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Ben", "3.", "adam", ".")));
        }
        {
            final String text = "\r\tBen 3987. adam.\t\r\n\t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Ben", "3987.", "adam", ".")));
        }
        {
            final String text = "\r\n 5.'de uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5.\'de", "uçuşa", "geçti", ".")));
        }
    }

    @Test
    public void shouldTokenizeTextWithExceptionalCase_2() {
        {
            final String text = "Dağlara taşlara yürüyordum... Derken birşeyler oldu!...";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Dağlara", "taşlara", "yürüyordum", "...", "Derken", "birşeyler", "oldu", "!...")));
        }
        {
            final String text = "\r\tBen \"...\"  dedi adam.\t\r\n\t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Ben", "\"...\"", "dedi", "adam", ".")));
        }
        {
            final String text = "6., 7. ve 8. adamlar gelsin.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("6.", ",", "7.", "ve", "8.", "adamlar", "gelsin", ".")));
        }
        {
            final String text = "\r\n 5.'de uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5.\'de", "uçuşa", "geçti", ".")));
        }
    }

    @Test
    public void shouldTokenizeTextWithExceptionalCase_3() {
        {
            final String text = "\r\tABD'de elma fiyatları  uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "\r\n 5'te uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5\'te", "uçuşa", "geçti", ".")));
        }
    }

    @Test
    public void shouldTokenizeTextWithExceptionalCase_4() {
        {
            final String text = "Bu işleme 'amelasyon' denir.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Bu", "işleme", "'amelasyon'", "denir", ".")));
        }
        {
            final String text = "Bu işlemin adı 'amelasyon'du.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Bu", "işlemin", "adı", "'amelasyon'du", ".")));
        }
        {
            final String text = "Bu işlemin adı \"amelasyon\"du.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("Bu", "işlemin", "adı", "\"amelasyon\"du", ".")));
        }
    }

    @Test
    public void shouldTokenizeTextWithExceptionalCase_5() {
        {
            final String text = "5:20 gibi gelecek.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5:20", "gibi", "gelecek", ".")));
        }
        {
            final String text = "5,60 TL verdim.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5,60", "TL", "verdim", ".")));
        }
        {
            final String text = "5.600 TL verdim.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5.600", "TL", "verdim", ".")));
        }
        {
            final String text = "5.600,1234 TL verdim.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5.600,1234", "TL", "verdim", ".")));
        }
        {
            final String text = "100'le gidiyor.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("100\'le", "gidiyor", ".")));
        }
        {
            final String text = "5:20'de geliyorum.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5:20\'de", "geliyorum", ".")));
        }
        {
            final String text = "5,74'te bir ihtimal.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("5,74\'te", "bir", "ihtimal", ".")));
        }
    }

    @Test
    public void shouldTokenizeTextWithMixOfExceptionalCases() {
        {
            final String text = "\r\tABD'de elma fiyatları  uçuşa geçti ..   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "uçuşa", "geçti", "..")));
        }
        {
            final String text = "\r\tABD'de elma fiyatları  uçuşa geçti !..   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "uçuşa", "geçti", "!..")));
        }
        {
            final String text = "\r\tABD'de elma fiyatları 3.  uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "3.", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "\r\tABD'de elma fiyatları 5'te uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "5\'te", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "\r\tABD'de elma fiyatları 5.'de uçuşa geçti .   \t";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("ABD\'de", "elma", "fiyatları", "5.\'de", "uçuşa", "geçti", ".")));
        }
        {
            final String text = "6, 7 ve 8 numara gelsin.";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("6", ",", "7", "ve", "8", "numara", "gelsin", ".")));
        }
        {
            final String text = "678.123 TL'yi cebe atmıştı ?.. ";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("678.123", "TL'yi", "cebe", "atmıştı", "?..")));
        }
    }

    @Test
    public void shouldSeparatePuncCharsInFrontOfWords() {
        {
            final String text = "(TBMM Tutanak)";
            final Iterable<String> tokens = tokenizer.tokenize(text);
            assertThat(Lists.newArrayList(tokens), equalTo(Arrays.asList("(", "TBMM", "Tutanak", ")")));
        }
    }

    @Test
    public void tokenizedTextShouldNotHaveWhiteSpace() {
        final String text = "ABD'den gelen veriyle altın fiyatları uçuşa geçti.\n" +
                "ABD'de tarım dışı istihdamın beklentilerin altında artmasının ardından küresel piyasalarda ABD Merkez Bankası'nın yavaşlayan ekonomiye önlem olarak yeni bir parasal gevşemeye gideceğine yönelik beklentilerle altın 6 ayın en yüksek düzeyini gördü.\n" +
                "Altının ons fiyatı ABD'den 15.30'da gelen verinin hemen ardından dakikalar içinde yüzde 1.5 yükselişle 1730 dolara kadar yükseldi.\n" +
                "ABD'den gelen zayıf veriler yatırımcıları güvenli liman olan altına yönlendiriyor.\n" +
                "Altının yıl başından bu yana değer kazancı yüzde 10'u aştı.\n" +
                "Uluslararası piyasalarda altının onsu yıl içerinde en düşük 1527,22 doları en yüksek ise 1790,79 doları gördü.";

        final Iterable<String> strings = Splitter.on("\n").split(text);
        for (String string : strings) {
            final Iterable<String> tokens = tokenizer.tokenize(string);

            assertThat(tokens, not(hasItem("")));
            for (String token : tokens) {
                assertThat(token, not(RegexMatcher.containsMatch("\\s")));
            }
        }
    }

}