/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Yegor Bugayenko
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
package org.eolang.txt;

import org.eolang.Data;
import org.eolang.EOarray;
import org.eolang.EOint;
import org.eolang.EOstring;
import org.eolang.Phi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link EOsprintf}.
 *
 * @since 0.1
 */
public final class EOsprintfTest {

    @Test
    public void printsString() {
        final Phi format = new EOstring();
        format.put("_data", new Data.Value<>("Hello, %d!"));
        final Phi num = new EOint();
        num.put("_data", new Data.Value<>(1L));
        final Phi array = new EOarray();
        array.put("_data", new Data.Value<>(new Phi[] {num}));
        final Phi phi = new EOsprintf();
        phi.put("format", format);
        phi.put("args", array);
        MatcherAssert.assertThat(
            new Data.Take(phi).take(String.class),
            Matchers.equalTo("Hello, 1!")
        );
    }

}
