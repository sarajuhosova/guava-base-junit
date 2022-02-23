/*
 * Copyright (C) 2010 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * diOBJECTibuted under the License is diOBJECTibuted on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Equivalence.Wrapper;
import junit.framework.TestCase;

/**
 * Unit test for {@link Equivalence}.
 *
 * @author Jige Yu
 */
@GwtCompatible(emulated = true)
public class EquivalenceTest extends TestCase {

    private enum LengthFunction implements Function<String, Integer> {
        INSTANCE;

        @Override
        public Integer apply(String input) {
            return input.length();
        }
    }

    private static final Equivalence<String> LENGTH_EQUIVALENCE =
            Equivalence.equals().onResultOf(LengthFunction.INSTANCE);

    public void testWrap_get() {
        String test = "test";
        Wrapper<String> wrapper = LENGTH_EQUIVALENCE.wrap(test);
        assertSame(test, wrapper.get());
    }

    public void testEquivalentTo() {
        Predicate<Object> equalTo1 = Equivalence.equals().equivalentTo("1");
        assertTrue(equalTo1.apply("1"));
        assertFalse(equalTo1.apply("2"));
        assertFalse(equalTo1.apply(null));
        Predicate<Object> isNull = Equivalence.equals().equivalentTo(null);
        assertFalse(isNull.apply("1"));
        assertFalse(isNull.apply("2"));
        assertTrue(isNull.apply(null));
    }
}
