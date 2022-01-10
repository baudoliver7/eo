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
package org.eolang.maven;

import com.jcabi.log.Logger;
import com.yegor256.tojos.Tojo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Pull EO XML files from Objectionary and parse them into XML.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @todo #490:30min Enable Objectionary caching in PullMojo.
 *  Replace usages of RemoteObjectionary with a
 *  combination of Local and Caching and Remote.
 *  Add a parameter to bypass/overwrite cache.
 */
@Mojo(
    name = "pull",
    defaultPhase = LifecyclePhase.PROCESS_SOURCES,
    threadSafe = true
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class PullMojo extends SafeMojo {

    /**
     * The directory where to process to.
     */
    public static final String DIR = "04-pull";

    /**
     * The Git hash to pull objects from, in objectionary.
     * @since 0.21.0
     */
    @SuppressWarnings("PMD.ImmutableField")
    @Parameter(required = true, defaultValue = "master")
    private String hash = "master";

    /**
     * Pull again even if the .eo file is already present?
     * @checkstyle MemberNameCheck (7 lines)
     * @since 0.10.0
     */
    @Parameter(required = true, defaultValue = "false")
    private boolean overWrite;

    /**
     * The objectionary.
     */
    @SuppressWarnings("PMD.ImmutableField")
    private Objectionary objectionary = new RemoteObjectionary();

    @Override
    public void exec() throws IOException {
        final Collection<Tojo> tojos = this.scopedTojos().select(
            row -> !row.exists(AssembleMojo.ATTR_EO)
                && !row.exists(AssembleMojo.ATTR_XMIR)
        );
        if (!"master".equals(this.hash)) {
            this.objectionary = new RemoteObjectionary(this.hash);
        }
        if (!tojos.isEmpty()) {
            for (final Tojo tojo : tojos) {
                tojo.set(
                    AssembleMojo.ATTR_EO,
                    this.pull(tojo.get("id")).toAbsolutePath().toString()
                );
            }
            Logger.info(this, "%d program(s) pulled", tojos.size());
        }
    }

    /**
     * Pull one object.
     *
     * @param name Name of the object, e.g. "org.eolang.io.stdout"
     * @return The path of .eo file
     * @throws IOException If fails
     */
    private Path pull(final String name) throws IOException {
        final Path src = new Place(name).make(
            this.targetDir.toPath().resolve(PullMojo.DIR), "eo"
        );
        if (src.toFile().exists() && !this.overWrite) {
            Logger.debug(
                this, "The object '%s' already pulled to %s (and 'overWrite' is false)",
                name, Save.rel(src)
            );
        } else {
            new Save(
                this.objectionary.get(name),
                src
            ).save();
            Logger.debug(
                this, "The sources of the object '%s' pulled to %s",
                name, Save.rel(src)
            );
        }
        return src;
    }

}
