/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.common;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EnumLookupMap<T extends Enum & SupportsEnumLookup> {
    private final Map<String, T> theMap;

    public EnumLookupMap(Class<T> enumClazz) {
        final HashBiMap<T, String> enumStringHashBiMap = HashBiMap.create(Maps.toMap(Arrays.asList(enumClazz.getEnumConstants()), new Function<T, String>() {
            @Override
            public String apply(T input) {
                return input.getLookupKey();
            }
        }));
        theMap = ImmutableMap.copyOf(enumStringHashBiMap.inverse());
    }


    public T get(String lookupKey) {
        if (StringUtils.isBlank(lookupKey))
            return null;

        final T t = theMap.get(lookupKey);
        if (t != null)
            return t;
        else
            throw new IllegalArgumentException("No enum constant found for lookup key " + lookupKey);
    }

    public Set<T> getMultiple(Collection<String> lookupKeys) {
        if (CollectionUtils.isEmpty(lookupKeys))
            return ImmutableSet.of();

        final Collection<T> values = Collections2.transform(lookupKeys, new Function<String, T>() {
            @Override
            public T apply(String input) {
                return get(input);
            }
        });

        return Sets.immutableEnumSet(values);
    }
}
