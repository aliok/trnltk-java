/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.experiments;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.util.Constants;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Try with -Xmx7000m -Xms5000m
 *
 * @deprecated Collator is too slow!
 * On a pretty powerful machine, sorting 35M words took:
 * <ul>
 * <li>0:00:03.429 without the collator</li>
 * <li>0:02:29.257 with the collator with the strength PRIMARY</li>
 * </ul>
 */
public class TurkishCollatorPerformanceTest {

    @Test
    @Ignore
    public void testCollatorPerformance() {
        final Collator collator = Collator.getInstance(Constants.TURKISH_LOCALE);
        collator.setStrength(Collator.PRIMARY);

        List<String> biggerList = getList();

        System.out.println("Entry count : " + biggerList.size());

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Collections.sort(biggerList);
        stopWatch.stop();
        System.out.println("w/o collator, it took " + stopWatch.toString());

        biggerList = getList();
        stopWatch.reset();
        stopWatch.start();
        Collections.sort(biggerList, collator);
        stopWatch.stop();
        System.out.println("w/ collator, it took " + stopWatch.toString());

    }

    private List<String> getList() {
        String text = "açıklamada 2735\n" +
                "gün 2678\n" +
                "şekilde 2405\n" +
                "üzere 2325\n" +
                "tüm 2285\n" +
                "10 2252\n" +
                "yılında 2223\n" +
                "günü 2151\n" +
                "\". 2140\n" +
                "5 2107\n" +
                "4 2018\n" +
                "... 1940\n" +
                "Türkiye'de 1933\n" +
                "yönelik 1925\n" +
                "üzerinde 1910\n" +
                "özel 1906\n" +
                "yüksek 1812\n" +
                "üç 1746\n" +
                "çıktı 1732\n" +
                "şöyle 1727\n" +
                "şu 1693\n" +
                "çıkan 1653\n" +
                "; 1608\n" +
                "güvenlik 1602\n" +
                "6 1570\n" +
                "sırasında 1569\n" +
                "söz 1554\n" +
                "İsrail 1539\n" +
                "açıkladı 1528\n" +
                "wün 1505\n" +
                "qün 1505\n" +
                "xün 1505\n" +
                "zün 1505\n" +
                "ğün 1505\n" +
                "Dışişleri 1489\n";

        final ArrayList<String> wordList = new ArrayList<String>();
        final Iterable<String> lines = Splitter.on("\n").trimResults().omitEmptyStrings().split(text);
        for (String line : lines) {
            final List<String> words = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
            Validate.isTrue(words.size() == 2, line);
            wordList.add(words.get(0));
        }

        List<String> biggerList = new ArrayList<String>();
        final int N = 1 * 1000 * 1000;
        for (int i = 0; i < N; i++) {
            biggerList.addAll(wordList);
            if (i % (N / 10) == 0)
                System.out.println("Adding " + i + ". list to the bigger list");
        }
        return biggerList;
    }

}
