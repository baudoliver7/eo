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
 * Free attribute.
 *
 * The attribute is not yet set, but can be set. It's writable, but
 * only once.
 *
 * @since 0.1
 */
public final class AtFree implements Attr {

    private final Attr origin;

    private Boolean set;

    public AtFree() {
        this(new AtSimple());
    }

    public AtFree(final Phi phi) {
        this(new AtSimple(phi));
    }

    public AtFree(final Attr attr) {
        this(attr, false);
    }

    public AtFree(final Attr attr, final boolean used) {
        this.origin = attr;
        this.set = used;
    }

    @Override
    public String toString() {
        return String.format("%sF", this.origin.toString());
    }

    @Override
    public String φTerm() {
        final String term;
        if (this.set) {
            term = this.origin.φTerm();
        } else {
            term = "Ø";
        }
        return term;
    }

    @Override
    public Attr copy(final Phi self) {
        return new AtFree(this.origin.copy(self), this.set);
    }

    @Override
    public Phi get() {
        final Phi phi = this.origin.get();
        if (phi.equals(Phi.Φ)) {
            throw new Attr.StillAbstractException(
                "The attribute is not initialized, can't read"
            );
        }
        return phi;
    }

    @Override
    public void put(final Phi phi) {
        if (this.set) {
            throw new Attr.ReadOnlyException(
                "This free attribute is already set, can't reset"
            );
        }
        this.origin.put(phi);
        this.set = true;
    }

}
