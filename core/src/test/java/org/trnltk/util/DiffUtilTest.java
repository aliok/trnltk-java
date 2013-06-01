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

package org.trnltk.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class DiffUtilTest {

    @Test
    public void testDiff() {
        String text1 = "I am the vXXery model  of a carttoon inidual";
        String text2 = "I a m the very Zodel of a caraatoon individu  al";
        final String[] diffLines = DiffUtil.diffLines(text1, text2, true);
        assertThat(diffLines, equalTo(new String[]{
                "I am the vXXery model of a cart toon in   idual",
                "          --    ^             ^^       +++     ",
                "                Z             aa       div     "
        }));
    }

    @Test
    public void testDiff_sc2() {
        String text1 = "'Hancı dedim, bildin mi Maraşlı Şeyhoğlu'nu?' (Faruk Nafiz Çamlıbel)";
        String text2 = "' Hancı dedim, bildin mi Maraşlı Şeyhoğlu'nu ? ' ( Faruk Nafiz Çamlıbel )";

        assertThat(DiffUtil.diffLines(text1, text2, true), nullValue());

        assertThat(DiffUtil.diffLines(text1, text2, false), equalTo(new String[]{
                "' Hancı dedim, bildin mi Maraşlı Şeyhoğlu'nu?  ' ( Faruk Nafiz Çamlıbel )",
                " +                                          ^^^   +                    + ",
                "                                             ?                           "
        }));
    }

    @Test
    public void testDiff_sc3() {
        String text1 = "soy-dil-din-abc";
        String text2 = "soy - dil - din - abc";

        assertThat(DiffUtil.diffLines(text1, text2, true), nullValue());

        assertThat(DiffUtil.diffLines(text1, text2, false), equalTo(new String[]{
                "soy-dil-din-      abc",
                "   ^^^^^^^^^^^^^^^   ",
                "    - dil - din -    "
        }));
    }

    @Test
    public void testDiff_sc4() {
        String text1 = "soy-dil-din-abc";
        String text2 = "soz - dil - din - abc";

        assertThat(DiffUtil.diffLines(text1, text2, true), equalTo(new String[]{
                "soy-dil-din-      abc",
                "  ^^^^^^^^^^^^^^^^   ",
                "  z - dil - din -    "
        }));

        assertThat(DiffUtil.diffLines(text1, text2, false), equalTo(new String[]{
                "soy-dil-din-      abc",
                "  ^^^^^^^^^^^^^^^^   ",
                "  z - dil - din -    "
        }));
    }
}
