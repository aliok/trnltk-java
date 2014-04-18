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

/**
 * Provides helper functions for specifications.
 * <p/>
 * <code>Specifications.or(spec1, spec2, spec3)</code> is more readable than <code>spec1.or(spec2).or(spec3)</code>
 */
@SuppressWarnings("unchecked")
public abstract class Specifications {

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> or(Specification<T> specification1, Specification<T> specification2) {
        return internalOr(specification1, specification2);
    }

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> or(Specification<T> specification1, Specification<T> specification2, Specification<T> specification3) {
        return internalOr(specification1, specification2, specification3);
    }

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> or(Specification<T> specification1, Specification<T> specification2, Specification<T> specification3, Specification<T> specification4) {
        return internalOr(specification1, specification2, specification3, specification4);
    }

    public static <T> Specification<T> or(Specification<T>... specifications) {
        return internalOr(specifications);
    }

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> and(Specification<T> specification1, Specification<T> specification2) {
        return internalAnd(specification1, specification2);
    }

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> and(Specification<T> specification1, Specification<T> specification2, Specification<T> specification3) {
        return internalAnd(specification1, specification2, specification3);
    }

    // this is here to prevent "Unchecked generics array creation for varargs parameter"
    public static <T> Specification<T> and(Specification<T> specification1, Specification<T> specification2, Specification<T> specification3, Specification<T> specification4) {
        return internalAnd(specification1, specification2, specification3, specification4);
    }

    public static <T> Specification<T> and(Specification<T>... specifications) {
        return internalAnd(specifications);
    }

    private static <T> Specification<T> internalOr(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>) FalseSpecification.INSTANCE;
        for (Specification specification : specifications) {
            returnValue = returnValue.or(specification);
        }
        return returnValue;
    }

    private static <T> Specification<T> internalAnd(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>) TrueSpecification.INSTANCE;
        for (Specification<T> specification : specifications) {
            returnValue = returnValue.and(specification);
        }
        return returnValue;
    }
}
