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

package EOorg.EOeolang.EOgray;

import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.Attr;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.PhDefault;
import org.eolang.PhWith;
import org.eolang.Phi;
import org.eolang.XmirObject;

/**
 * TRY.
 *
 * @since 0.19
 */
@XmirObject(oname = "try")
public class EOtry extends PhDefault {

    /**
     * Ctor.
     * @param sigma Sigma
     */
    public EOtry(final Phi sigma) {
        super(sigma);
        this.add("main", new AtFree());
        this.add("catch", new AtFree());
        this.add("finally", new AtFree());
        this.add("φ", new AtComposite(this, rho -> {
            final Phi main = rho.attr("main").get().copy();
            main.move(rho);
            main.attr(0).put(new EOtry.Throw(rho));
            Phi ret;
            try {
                ret = new Data.ToPhi(new Dataized(main).take());
            } catch (final EOtry.ThrowException ex) {
                if (!ex.sigma.equals(rho)) {
                    throw ex;
                }
                final Phi ctch = rho.attr("catch").get().copy();
                ctch.move(rho);
                ret = new Data.ToPhi(
                    new Dataized(
                        new PhWith(
                            ctch,
                            0, ex.exception
                        )
                    ).take()
                );
            } finally {
                final Phi fin = rho.attr("finally").get().copy();
                fin.move(rho);
                new Dataized(fin).take();
            }
            return ret;
        }));
    }

    /**
     * The token.
     * @since 0.19
     */
    @XmirObject(oname = "goto.throw")
    private final class Throw extends PhDefault {
        Throw(final Phi sigma) {
            super(sigma);
            this.add("ex", new AtFree());
            this.add("φ", new AtComposite(this, rho -> {
                throw new EOtry.ThrowException(
                    rho.attr("σ").get(),
                    rho.attr("ex").get()
                );
            }));
        }
    }

    /**
     * When exception happens.
     * @since 0.19
     */
    private static class ThrowException extends Attr.FlowException {
        private static final long serialVersionUID = 1735493012609760997L;
        public final Phi sigma;
        public final Phi exception;
        ThrowException(final Phi sgm, final Phi exp) {
            super();
            this.sigma = sgm;
            this.exception = exp;
        }
    }

}
