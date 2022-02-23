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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import junit.framework.TestCase;

/**
 * Unit test for {@link Strings}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class StringsTest extends TestCase {
    public void testNullToEmpty() {
        assertEquals("", Strings.nullToEmpty(null));
        assertEquals("", Strings.nullToEmpty(""));
        assertEquals("a", Strings.nullToEmpty("a"));
    }

    public void testEmptyToNull() {
        assertNull(Strings.emptyToNull(null));
        assertNull(Strings.emptyToNull(""));
        assertEquals("a", Strings.emptyToNull("a"));
    }

    public void testIsNullOrEmpty() {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(Strings.isNullOrEmpty(""));
        assertFalse(Strings.isNullOrEmpty("a"));
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testPadStart_null() {
        try {
            Strings.padStart(null, 5, '0');
            fail();
        } catch (NullPointerException expected) {
        }
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testPadEnd_null() {
        try {
            Strings.padEnd(null, 5, '0');
            fail();
        } catch (NullPointerException expected) {
        }
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testRepeat_null() {
        try {
            Strings.repeat(null, 5);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testCommonPrefix() {
        assertEquals("", Strings.commonPrefix("", ""));
        assertEquals("", Strings.commonPrefix("abc", ""));
        assertEquals("a", Strings.commonPrefix("abc", "aaaaa"));
        assertEquals("aa", Strings.commonPrefix("aa", "aaaaa"));

        // Identical valid surrogate pairs.
        assertEquals(
                "abc\uD8AB\uDCAB", Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCABxyz"));
        // Differing valid surrogate pairs.
        assertEquals("abc", Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCACxyz"));
        // One invalid pair.
        assertEquals("abc", Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uD8ABxyz"));
        // Two identical invalid pairs.
        assertEquals(
                "abc\uD8AB\uD8AC", Strings.commonPrefix("abc\uD8AB\uD8ACdef", "abc\uD8AB\uD8ACxyz"));
        // Two differing invalid pairs.
        assertEquals("abc\uD8AB", Strings.commonPrefix("abc\uD8AB\uD8ABdef", "abc\uD8AB\uD8ACxyz"));
        // Two orphan high surrogates.
        assertEquals("\uD8AB", Strings.commonPrefix("\uD8AB", "\uD8AB"));
    }

    public void testCommonSuffix() {
        assertEquals("", Strings.commonSuffix("", ""));
        assertEquals("", Strings.commonSuffix("abc", ""));
        assertEquals("c", Strings.commonSuffix("abc", "ccccc"));
        assertEquals("aa", Strings.commonSuffix("aa", "aaaaa"));

        // Identical valid surrogate pairs.
        assertEquals(
                "\uD8AB\uDCABdef", Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uD8AB\uDCABdef"));
        // Differing valid surrogate pairs.
        assertEquals("def", Strings.commonSuffix("abc\uD8AB\uDCABdef", "abc\uD8AC\uDCABdef"));
        // One invalid pair.
        assertEquals("def", Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uDCAB\uDCABdef"));
        // Two identical invalid pairs.
        assertEquals(
                "\uD8AB\uD8ABdef", Strings.commonSuffix("abc\uD8AB\uD8ABdef", "xyz\uD8AB\uD8ABdef"));
        // Two differing invalid pairs.
        assertEquals("\uDCABdef", Strings.commonSuffix("abc\uDCAB\uDCABdef", "abc\uDCAC\uDCABdef"));
        // Two orphan low surrogates.
        assertEquals("\uDCAB", Strings.commonSuffix("\uDCAB", "\uDCAB"));
    }

    public void testValidSurrogatePairAt() {
        assertTrue(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 0));
        assertTrue(Strings.validSurrogatePairAt("abc\uD8AB\uDCAB", 3));
        assertTrue(Strings.validSurrogatePairAt("abc\uD8AB\uDCABxyz", 3));
        assertFalse(Strings.validSurrogatePairAt("\uD8AB\uD8AB", 0));
        assertFalse(Strings.validSurrogatePairAt("\uDCAB\uDCAB", 0));
        assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -1));
        assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 1));
        assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -2));
        assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 2));
        assertFalse(Strings.validSurrogatePairAt("x\uDCAB", 0));
        assertFalse(Strings.validSurrogatePairAt("\uD8ABx", 0));
    }
}
