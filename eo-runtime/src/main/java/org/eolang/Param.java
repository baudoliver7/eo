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

/**
 * Param of an object (convenient retrieval mechanism).
 *
 * <p>The job of the utility object is to help our EO objects retrieve
 * attributes from other objects and from their own \rho (owners). On top of
 * retrieval this object also does simple type checking. When an attribute
 * is expected to be of some type, we use {@link #strong(Class)}. This method
 * will throw a runtime exception if types don't match. If just a simple
 * retrieval without type checking is necessary, just use the method
 * {@link #weak()}.
 *
 * @since 0.20
 */
public final class Param {

    /**
     * The object.
     */
    private final Phi rho;

    /**
     * Attr name.
     */
    private final String attr;

    /**
     * Ctor.
     * @param obj The object to fetch \rho from
     */
    public Param(final Phi obj) {
        this(obj, "ρ");
    }

    /**
     * Ctor.
     * @param obj The object to fetch the attribute from
     * @param name Name of the attr
     */
    public Param(final Phi obj, final String name) {
        this.rho = obj;
        this.attr = name;
    }

    /**
     * Fetch and check type.
     * @param type The type
     * @param <T> The type
     * @return The object
     */
    public <T> T strong(final Class<T> type) {
        final Object ret = this.weak();
        if (!type.isInstance(ret)) {
            throw new IllegalArgumentException(
                String.format(
                    "The argument '.%s' is of Java type '%s', not '%s' as expected",
                    this.attr,
                    ret.getClass().getCanonicalName(),
                    type.getCanonicalName()
                )
            );
        }
        return type.cast(ret);
    }

    /**
     * Fetch and DON'T check type.
     * @return The object
     */
    public Object weak() {
        return new Dataized(
            this.rho.attr(this.attr).get()
        ).take();
    }

}
