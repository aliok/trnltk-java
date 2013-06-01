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

package org.trnltk.model.suffix;

public class SuffixFormApplication {
    private final SuffixForm suffixForm;
    private final String actualSuffixForm;      //e.g "acağ"
    private final String fittingSuffixForm;     //e.g "acak"

    public SuffixFormApplication(SuffixForm suffixForm, String actualSuffixForm, String fittingSuffixForm) {
        this.suffixForm = suffixForm;
        this.actualSuffixForm = actualSuffixForm;
        this.fittingSuffixForm = fittingSuffixForm;
    }

    public SuffixForm getSuffixForm() {
        return suffixForm;
    }

    public String getActualSuffixForm() {
        return actualSuffixForm;
    }

    public String getFittingSuffixForm() {
        return fittingSuffixForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormApplication that = (SuffixFormApplication) o;

        if (!actualSuffixForm.equals(that.actualSuffixForm)) return false;
        if (!fittingSuffixForm.equals(that.fittingSuffixForm)) return false;
        if (!suffixForm.equals(that.suffixForm)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = suffixForm.hashCode();
        result = 31 * result + actualSuffixForm.hashCode();
        result = 31 * result + fittingSuffixForm.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixFormApplication{" +
                "suffixForm=" + suffixForm +
                ", actualSuffixForm='" + actualSuffixForm + '\'' +
                ", fittingSuffixForm='" + fittingSuffixForm + '\'' +
                '}';
    }
}
