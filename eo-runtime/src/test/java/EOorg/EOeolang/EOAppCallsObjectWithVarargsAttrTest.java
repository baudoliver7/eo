/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package EOorg.EOeolang;

import org.eolang.AtComposite;
import org.eolang.AtOnce;
import org.eolang.AtVararg;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.PhCopy;
import org.eolang.PhDefault;
import org.eolang.PhMethod;
import org.eolang.PhUnvar;
import org.eolang.PhWith;
import org.eolang.Phi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests dataization of an EO app that calls an abject
 * with varargs.
 *
 * @since 0.22
 * @todo #534:30min Vararg: fix bug readonly attribute for external objects.
 *  We resolved bug for local object with vararg as free attribute. We need now to
 *  fix bug for external ones. To resolve it, we should firstly know the free attributes
 *  of these external objects.
 */
final class EOAppCallsObjectWithVarargsAttrTest {

    @Test
    public void dataizesEOappThatCallsVarargsFunc() {
        MatcherAssert.assertThat(
            new Dataized(
                new EOappThatCallsVarargsFunc(Phi.Φ)
            ).take(Boolean.class),
            Matchers.is(true)
        );
    }

    /**
     * Main program sample.
     * {@code
     *  [] > app
     *    (f 1 2 3).eq 2 > @
     * }
     * @since 0.22
     */
    private static class EOappThatCallsVarargsFunc extends PhDefault {
        public EOappThatCallsVarargsFunc(Phi sigma) {
            super(sigma);
            this.add(
                "φ",
                new AtOnce(
                    new AtComposite(
                        this,
                        (rho) -> {
                            final Phi fvar = new PhWith(
                                new PhCopy(new Fvarargs(rho)),
                                0,
                                new PhUnvar(
                                    new Data.ToPhi(
                                        new Phi[] {
                                            new Data.ToPhi(1L),
                                            new Data.ToPhi(2L),
                                            new Data.ToPhi(3L)
                                        }
                                    )
                                )
                            );
                            return new PhWith(
                                new PhCopy(new PhMethod(fvar, "eq")), 0, new Data.ToPhi(2L)
                            );
                        }
                    )
                )
            );
        }
    }

    /**
     * Varargs function sample.
     * {@code
     *  [args...] > f
     *    1 > a
     *    2 > @
     * }
     * @since 0.22
     */
    @SuppressWarnings("unchecked")
    private static class Fvarargs extends PhDefault {
        public Fvarargs(final Phi sigma) {
            super(sigma);
            this.add("args", new AtVararg());
            this.add(
                "a",
                new AtOnce(
                    new AtComposite(this, (rho) -> new Data.ToPhi(1L))
                )
            );
            this.add(
                "φ",
                new AtOnce(
                    new AtComposite(this, (rho) -> new Data.ToPhi(2L))
                )
            );
        }
    }

}