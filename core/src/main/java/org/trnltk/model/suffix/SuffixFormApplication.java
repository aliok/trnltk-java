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

/**
 * An application of a {@link SuffixForm} on a surface.
 * <p/>
 * For example, for suffix <i>Future_Adj</i>, a SuffixForm is "+yAcAk".
 * When this SuffixForm is applied to a surface, it gets different variations.
 * <table border="1">
 * <tr>
 * <th>Surface</th>
 * <th>Suffix</th>
 * <th>SuffixForm</th>
 * <th>SuffixFormApplication.actualSuffixForm</th>
 * <th>SuffixFormApplication.fittingSuffixForm</th>
 * </tr>
 * <tr>
 * <td>gelecek</td>
 * <td>Future_Adj</td>
 * <td>+yAcAk</td>
 * <td>ecek</td>
 * <td>ecek</td>
 * </tr>
 * <tr>
 * <td>geleceği</td>
 * <td>Future_Adj</td>
 * <td>+yAcAk</td>
 * <td>eceğ</td>
 * <td>ecek</td>
 * </tr>
 * <tr>
 * <td>atayacakları</td>
 * <td>Future_Adj</td>
 * <td>+yAcAk</td>
 * <td>yacak</td>
 * <td>yacak</td>
 * </tr>
 * <tr>
 * <td>atayacağım</td>
 * <td>Future_Adj</td>
 * <td>+yAcAk</td>
 * <td>yacağ</td>
 * <td>yacak</td>
 * </tr>
 * </table>
 * <p/>
 * A <code>SuffixFormApplication</code> holds the actual and fitting string forms of a {@link SuffixForm}.
 */
public class SuffixFormApplication {
    private final SuffixForm suffixForm;
    private final String actualSuffixForm;      //e.g "acağ"
    private final String fittingSuffixForm;     //e.g "acak"

    public SuffixFormApplication(SuffixForm suffixForm, String actualSuffixForm, String fittingSuffixForm) {
        this.suffixForm = suffixForm;
        this.actualSuffixForm = actualSuffixForm;
        this.fittingSuffixForm = fittingSuffixForm;
    }

    /**
     * @return SuffixForm that is applied in a surface
     */
    public SuffixForm getSuffixForm() {
        return suffixForm;
    }

    /**
     * @return some str
     * @see SuffixFormApplication
     */
    public String getActualSuffixForm() {
        return actualSuffixForm;
    }

    /**
     * @return some str
     * @see SuffixFormApplication
     */
    public String getFittingSuffixForm() {
        return fittingSuffixForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormApplication that = (SuffixFormApplication) o;

        if (!actualSuffixForm.equals(that.actualSuffixForm)) return false;
        else if (!fittingSuffixForm.equals(that.fittingSuffixForm)) return false;
        else if (!suffixForm.equals(that.suffixForm)) return false;

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
