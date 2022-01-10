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

package org.eolang;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A dataized object.
 *
 * <p>The class automates the process of turning EO objects into data. The
 * mechanism is explained in details in our canonical paper. Simply put,
 * it makes an attempt to either type-cast the provided object into data
 * or find "Δ" attribute inside it. If neither of that works, there is a
 * runtime exception.
 *
 * <p>It's recommended to use {@link Param} object, when you are inside
 * a EO object: it will add type checking on top of dataization.
 *
 * @since 0.1
 * @see <a href="https://arxiv.org/abs/2111.13384">Canonical explanation of the Dataization concept</a>
 */
public final class Dataized {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Dataized.class.getName());

    /**
     * Dataization level.
     */
    private static final ThreadLocal<Integer> LEVEL = ThreadLocal.withInitial(() -> 0);

    /**
     * The object to datarize.
     */
    private final Phi phi;

    /**
     * Ctor.
     * @param src The object
     */
    public Dataized(final Phi src) {
        this.phi = src;
    }

    /**
     * Take the object, no matter the type.
     * @return The data
     */
    public Object take() {
        final int before = Dataized.LEVEL.get();
        Dataized.LEVEL.set(before + 1);
        Phi src = this.phi;
        try {
            if (!(src instanceof Data)) {
                src = src.attr("Δ").get();
            }
        } catch (final Attr.IllegalAttrException ex) {
            throw new Attr.IllegalAttrException(
                String.format("Attribute failure at:%n%s", this.phi),
                ex
            );
        }
        if (!(src instanceof Data)) {
            throw new Attr.IllegalAttrException(
                String.format(
                    "The attribute Δ has %s instead of %s at:%n%s",
                    src.getClass().getCanonicalName(),
                    Data.class.getCanonicalName(),
                    this.phi
                )
            );
        }
        final Object data = Data.class.cast(src).take();
        if (Dataized.LOGGER.isLoggable(Level.FINE)) {
            Dataized.LOGGER.log(
                Level.FINE,
                String.format(
                    "%s\uD835\uDD3B( %s ) ➜ %s",
                    String.join("", Collections.nCopies(before, "·")),
                    this.phi.φTerm().replaceAll("[\n\t]", ""),
                    new Data.Value<>(data).φTerm()
                )
            );
        }
        Dataized.LEVEL.set(before);
        return data;
    }

    /**
     * Take the data with a type.
     * @param type The type
     * @param <T> The type
     * @return The data
     */
    public <T> T take(final Class<T> type) {
        return type.cast(this.take());
    }
}
