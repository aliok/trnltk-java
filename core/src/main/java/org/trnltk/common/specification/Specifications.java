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

package org.trnltk.common.specification;

@SuppressWarnings("unchecked")
public abstract class Specifications {
    public static <T> Specification<T> or(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>)FalseSpecification.INSTANCE;
        for (Specification specification : specifications) {
            returnValue = returnValue.or(specification);
        }
        return returnValue;
    }

    public static <T> Specification<T> and(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>)TrueSpecification.INSTANCE;
        for (Specification<T> specification : specifications) {
            returnValue = returnValue.and(specification);
        }
        return returnValue;
    }
}