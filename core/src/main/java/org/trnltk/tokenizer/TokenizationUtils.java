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

package org.trnltk.tokenizer;

public class TokenizationUtils {
    public static String normalizeQuotesHyphens(String input) {
        // rdquo, ldquo, laquo, raquo, Prime sybols in unicode.
        return input
                .replaceAll("[\u201C\u201D\u00BB\u00AB\u2033\u0093\u0094]|''", "\"")
                .replaceAll("[\u0091\u0092\u2032´`’‘]", "'")
                .replaceAll("[\u0096\u0097–]", "-");
    }
}
