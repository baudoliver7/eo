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
package EOorg.EOeolang.EOtxt;

import java.util.regex.Pattern;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.PhMethod;
import org.eolang.PhWith;
import org.eolang.Phi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link EOregex$EOmatch}.
 *
 * @since 0.1
 */
public final class EOregexEOmatchTest {

    /**
     * Method name.
     */
    public static final String MATCH = "match";
    /**
     * Attribute name.
     */
    private static final String TXT = "txt";

    @Test
    public void matchesString() {
        final Phi regex = new Data.ToPhi(Pattern.compile("([a-z]+)"));
        MatcherAssert.assertThat(
            new Dataized(
                new PhWith(
                    new PhMethod(regex, EOregexEOmatchTest.MATCH),
                    EOregexEOmatchTest.TXT,
                    new Data.ToPhi("hello")
                )
            ).take(Phi[].class).length,
            Matchers.equalTo(1)
        );
    }

    @Test
    public void doesntMatchString() {
        final Phi regex = new Data.ToPhi(Pattern.compile("([A-Z]{2})"));
        MatcherAssert.assertThat(
            new Dataized(
                new PhWith(
                    new PhMethod(regex, EOregexEOmatchTest.MATCH),
                    EOregexEOmatchTest.TXT,
                    new Data.ToPhi("Hello, World!")
                )
            ).take(Phi[].class).length,
            Matchers.equalTo(0)
        );
    }

}
