/*
 * Copyright (C) 2007 The Guava Authors
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
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import junit.framework.TestCase;

import java.util.List;

import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_VERSION;
import static com.google.common.base.Throwables.*;
import static java.util.Arrays.asList;

/**
 * Unit test for {@link Throwables}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class ThrowablesTest extends TestCase {
    public void testThrowIfUnchecked_Unchecked() {
        try {
            throwIfUnchecked(new SomeUncheckedException());
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    public void testThrowIfUnchecked_Error() {
        try {
            throwIfUnchecked(new SomeError());
            fail();
        } catch (SomeError expected) {
        }
    }

    @SuppressWarnings("ThrowIfUncheckedKnownChecked")
    public void testThrowIfUnchecked_Checked() {
        throwIfUnchecked(new SomeCheckedException());
    }

    @GwtIncompatible // propagateIfPossible
    public void testPropagateIfPossible_NoneDeclared_NoneThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatDoesntThrowAnything();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect no exception to be thrown
        sample.noneDeclared();
    }

    @GwtIncompatible // propagateIfPossible
    public void testPropagateIfPossible_NoneDeclared_UncheckedThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatThrowsUnchecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the unchecked exception to propagate as-is
        try {
            sample.noneDeclared();
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible
    public void testPropagateIfPossible_NoneDeclared_UndeclaredThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatThrowsUndeclaredChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the undeclared exception to have been chained inside another
        try {
            sample.noneDeclared();
            fail();
        } catch (SomeChainingException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class)
    public void testPropagateIfPossible_OneDeclared_NoneThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatDoesntThrowAnything();
                        } catch (Throwable t) {
                            // yes, this block is never reached, but for purposes of illustration
                            // we're keeping it the same in each test
                            Throwables.propagateIfPossible(t, SomeCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect no exception to be thrown
        sample.oneDeclared();
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class)
    public void testPropagateIfPossible_OneDeclared_UncheckedThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatThrowsUnchecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t, SomeCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the unchecked exception to propagate as-is
        try {
            sample.oneDeclared();
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class)
    public void testPropagateIfPossible_OneDeclared_CheckedThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatThrowsChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t, SomeCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the checked exception to propagate as-is
        try {
            sample.oneDeclared();
            fail();
        } catch (SomeCheckedException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class)
    public void testPropagateIfPossible_OneDeclared_UndeclaredThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatThrowsUndeclaredChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(t, SomeCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the undeclared exception to have been chained inside another
        try {
            sample.oneDeclared();
            fail();
        } catch (SomeChainingException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class, Class)
    public void testPropagateIfPossible_TwoDeclared_NoneThrown()
            throws SomeCheckedException, SomeOtherCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void twoDeclared() throws SomeCheckedException, SomeOtherCheckedException {
                        try {
                            methodThatDoesntThrowAnything();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(
                                    t, SomeCheckedException.class, SomeOtherCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect no exception to be thrown
        sample.twoDeclared();
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class, Class)
    public void testPropagateIfPossible_TwoDeclared_UncheckedThrown()
            throws SomeCheckedException, SomeOtherCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void twoDeclared() throws SomeCheckedException, SomeOtherCheckedException {
                        try {
                            methodThatThrowsUnchecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(
                                    t, SomeCheckedException.class, SomeOtherCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the unchecked exception to propagate as-is
        try {
            sample.twoDeclared();
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class, Class)
    public void testPropagateIfPossible_TwoDeclared_CheckedThrown() throws SomeOtherCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void twoDeclared() throws SomeCheckedException, SomeOtherCheckedException {
                        try {
                            methodThatThrowsChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(
                                    t, SomeCheckedException.class, SomeOtherCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the checked exception to propagate as-is
        try {
            sample.twoDeclared();
            fail();
        } catch (SomeCheckedException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class, Class)
    public void testPropagateIfPossible_TwoDeclared_OtherCheckedThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void twoDeclared() throws SomeCheckedException, SomeOtherCheckedException {
                        try {
                            methodThatThrowsOtherChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfPossible(
                                    t, SomeCheckedException.class, SomeOtherCheckedException.class);
                            throw new SomeChainingException(t);
                        }
                    }
                };

        // Expect the checked exception to propagate as-is
        try {
            sample.twoDeclared();
            fail();
        } catch (SomeOtherCheckedException expected) {
        }
    }

    public void testThrowIfUnchecked_null() throws SomeCheckedException {
        try {
            throwIfUnchecked(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @GwtIncompatible // propagateIfPossible
    public void testPropageIfPossible_null() throws SomeCheckedException {
        Throwables.propagateIfPossible(null);
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class)
    public void testPropageIfPossible_OneDeclared_null() throws SomeCheckedException {
        Throwables.propagateIfPossible(null, SomeCheckedException.class);
    }

    @GwtIncompatible // propagateIfPossible(Throwable, Class, Class)
    public void testPropageIfPossible_TwoDeclared_null() throws SomeCheckedException {
        Throwables.propagateIfPossible(null, SomeCheckedException.class, SomeUncheckedException.class);
    }

    @GwtIncompatible // propagate
    public void testPropagate_NoneDeclared_NoneThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatDoesntThrowAnything();
                        } catch (Throwable t) {
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect no exception to be thrown
        sample.noneDeclared();
    }

    @GwtIncompatible // propagate
    public void testPropagate_NoneDeclared_UncheckedThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatThrowsUnchecked();
                        } catch (Throwable t) {
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect the unchecked exception to propagate as-is
        try {
            sample.noneDeclared();
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    @GwtIncompatible // propagate
    public void testPropagate_NoneDeclared_ErrorThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void noneDeclared() {
                        try {
                            methodThatThrowsError();
                        } catch (Throwable t) {
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect the error to propagate as-is
        try {
            sample.noneDeclared();
            fail();
        } catch (SomeError expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testThrowIfInstanceOf_Unchecked() throws SomeCheckedException {
        throwIfInstanceOf(new SomeUncheckedException(), SomeCheckedException.class);
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testThrowIfInstanceOf_CheckedDifferent() throws SomeCheckedException {
        throwIfInstanceOf(new SomeOtherCheckedException(), SomeCheckedException.class);
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testThrowIfInstanceOf_CheckedSame() {
        try {
            throwIfInstanceOf(new SomeCheckedException(), SomeCheckedException.class);
            fail();
        } catch (SomeCheckedException expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testThrowIfInstanceOf_CheckedSubclass() {
        try {
            throwIfInstanceOf(new SomeCheckedException() {
            }, SomeCheckedException.class);
            fail();
        } catch (SomeCheckedException expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testPropagateIfInstanceOf_NoneThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatDoesntThrowAnything();
                        } catch (Throwable t) {
                            Throwables.propagateIfInstanceOf(t, SomeCheckedException.class);
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect no exception to be thrown
        sample.oneDeclared();
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testPropagateIfInstanceOf_DeclaredThrown() {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatThrowsChecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfInstanceOf(t, SomeCheckedException.class);
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect declared exception to be thrown as-is
        try {
            sample.oneDeclared();
            fail();
        } catch (SomeCheckedException expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testPropagateIfInstanceOf_UncheckedThrown() throws SomeCheckedException {
        Sample sample =
                new Sample() {
                    @Override
                    public void oneDeclared() throws SomeCheckedException {
                        try {
                            methodThatThrowsUnchecked();
                        } catch (Throwable t) {
                            Throwables.propagateIfInstanceOf(t, SomeCheckedException.class);
                            throw Throwables.propagate(t);
                        }
                    }
                };

        // Expect unchecked exception to be thrown as-is
        try {
            sample.oneDeclared();
            fail();
        } catch (SomeUncheckedException expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testThrowIfInstanceOf_null() throws SomeCheckedException {
        try {
            throwIfInstanceOf(null, SomeCheckedException.class);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @GwtIncompatible // throwIfInstanceOf
    public void testPropageIfInstanceOf_null() throws SomeCheckedException {
        Throwables.propagateIfInstanceOf(null, SomeCheckedException.class);
    }

    public void testGetRootCause_NoCause() {
        SomeCheckedException exception = new SomeCheckedException();
        assertSame(exception, Throwables.getRootCause(exception));
    }

    public void testGetRootCause_SingleWrapped() {
        SomeCheckedException cause = new SomeCheckedException();
        SomeChainingException exception = new SomeChainingException(cause);
        assertSame(cause, Throwables.getRootCause(exception));
    }

    public void testGetRootCause_DoubleWrapped() {
        SomeCheckedException cause = new SomeCheckedException();
        SomeChainingException exception = new SomeChainingException(new SomeChainingException(cause));
        assertSame(cause, Throwables.getRootCause(exception));
    }

    private static class SomeError extends Error {
    }

    private static class SomeCheckedException extends Exception {
    }

    private static class SomeOtherCheckedException extends Exception {
    }

    private static class SomeUncheckedException extends RuntimeException {
    }

    private static class SomeUndeclaredCheckedException extends Exception {
    }

    private static class SomeChainingException extends RuntimeException {
        public SomeChainingException(Throwable cause) {
            super(cause);
        }
    }

    static class Sample {
        void noneDeclared() {
        }

        void oneDeclared() throws SomeCheckedException {
        }

        void twoDeclared() throws SomeCheckedException, SomeOtherCheckedException {
        }
    }

    static void methodThatDoesntThrowAnything() {
    }

    static void methodThatThrowsError() {
        throw new SomeError();
    }

    static void methodThatThrowsUnchecked() {
        throw new SomeUncheckedException();
    }

    static void methodThatThrowsChecked() throws SomeCheckedException {
        throw new SomeCheckedException();
    }

    static void methodThatThrowsOtherChecked() throws SomeOtherCheckedException {
        throw new SomeOtherCheckedException();
    }

    static void methodThatThrowsUndeclaredChecked() throws SomeUndeclaredCheckedException {
        throw new SomeUndeclaredCheckedException();
    }

    public void testGetCausalChain() {
        SomeUncheckedException sue = new SomeUncheckedException();
        IllegalArgumentException iae = new IllegalArgumentException(sue);
        RuntimeException re = new RuntimeException(iae);
        IllegalStateException ex = new IllegalStateException(re);

        assertEquals(asList(ex, re, iae, sue), Throwables.getCausalChain(ex));
        assertSame(sue, Iterables.getOnlyElement(Throwables.getCausalChain(sue)));

        List<Throwable> causes = Throwables.getCausalChain(ex);
        try {
            causes.add(new RuntimeException());
            fail("List should be unmodifiable");
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testGetCasualChainNull() {
        try {
            Throwables.getCausalChain(null);
            fail("Should have throw NPE");
        } catch (NullPointerException expected) {
        }
    }

    @AndroidIncompatible // No getJavaLangAccess in Android (at least not in the version we use).
    @GwtIncompatible // lazyStackTraceIsLazy()
    public void testLazyStackTraceWorksInProd() {
        // TODO(b/64442212): Remove this guard once lazyStackTrace() works in Java 9+.
        Integer javaVersion = Ints.tryParse(JAVA_SPECIFICATION_VERSION.value());
        if (javaVersion != null && javaVersion >= 9) {
            return;
        }
        // Obviously this isn't guaranteed in every environment, but it works well enough for now:
        assertTrue(lazyStackTraceIsLazy());
    }
}
