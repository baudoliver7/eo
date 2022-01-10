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
package org.eolang.parser;

import com.jcabi.manifests.Manifests;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cactoos.list.Mapped;
import org.cactoos.text.Joined;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * The listener for ANTLR4 walker.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
public final class XeListener implements ProgramListener {

    /**
     * The name of it.
     */
    private final String name;

    /**
     * Xembly directives we are building (mutable).
     */
    private final Directives dirs;

    /**
     * When we start.
     */
    private final long start;

    /**
     * Ctor.
     * @param nme Tha name of it
     */
    public XeListener(final String nme) {
        this.name = nme;
        this.dirs = new Directives();
        this.start = System.nanoTime();
    }

    /**
     * To get the XML ready to be used.
     * @return XML
     */
    public XML xml() {
        return new XMLDocument(new Xembler(this.dirs).domQuietly());
    }

    @Override
    public void enterProgram(final ProgramParser.ProgramContext ctx) {
        this.dirs.add("program")
            .attr("name", this.name)
            .attr("version", Manifests.read("EO-Version"))
            .attr(
                "time",
                ZonedDateTime.now(ZoneOffset.UTC).format(
                    DateTimeFormatter.ISO_INSTANT
                )
            )
            .add("listing").set(ctx.getText()).up()
            .add("errors").up()
            .add("sheets").up();
    }

    @Override
    public void exitProgram(final ProgramParser.ProgramContext ctx) {
        this.dirs
            // @checkstyle MagicNumber (1 line)
            .attr("ms", (System.nanoTime() - this.start) / (1000L * 1000L))
            .up();
    }

    @Override
    public void enterLicense(final ProgramParser.LicenseContext ctx) {
        this.dirs.add("license").set(
            new Joined(
                "\n",
                new Mapped<>(
                    cmt -> cmt.getText().substring(1).trim(),
                    ctx.COMMENT()
                )
            )
        ).up();
    }

    @Override
    public void exitLicense(final ProgramParser.LicenseContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void enterMetas(final ProgramParser.MetasContext ctx) {
        this.dirs.add("metas");
        for (final TerminalNode node : ctx.META()) {
            final String[] pair = node.getText().split(" ", 2);
            this.dirs.add("meta")
                .attr("line", node.getSymbol().getLine())
                .add("head").set(pair[0].substring(1)).up()
                .add("tail");
            if (pair.length > 1) {
                this.dirs.set(pair[1].trim()).up();
                for (final String part : pair[1].trim().split(" ")) {
                    this.dirs.add("part").set(part).up();
                }
            } else {
                this.dirs.up();
            }
            this.dirs.up();
        }
        this.dirs.up();
    }

    @Override
    public void exitMetas(final ProgramParser.MetasContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void enterObjects(final ProgramParser.ObjectsContext ctx) {
        this.dirs.add("objects");
    }

    @Override
    public void exitObjects(final ProgramParser.ObjectsContext ctx) {
        this.dirs.up();
    }

    @Override
    public void enterObject(final ProgramParser.ObjectContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void exitObject(final ProgramParser.ObjectContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void enterAbstraction(final ProgramParser.AbstractionContext ctx) {
        this.dirs.add("o").attr("line", ctx.getStart().getLine());
        if (ctx.SLASH() != null) {
            if (ctx.QUESTION() == null) {
                this.dirs.attr("atom", ctx.NAME());
            } else {
                this.dirs.attr("atom", "?");
            }
        }
        this.dirs.up();
    }

    @Override
    public void exitAbstraction(final ProgramParser.AbstractionContext ctx) {
        // Nothing here
    }

    @Override
    public void enterAttributes(final ProgramParser.AttributesContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void exitAttributes(final ProgramParser.AttributesContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void enterAttribute(final ProgramParser.AttributeContext ctx) {
        this.enter();
        this.dirs.add("o").attr("line", ctx.getStart().getLine());
    }

    @Override
    public void exitAttribute(final ProgramParser.AttributeContext ctx) {
        this.dirs.up().up();
    }

    @Override
    public void enterLabel(final ProgramParser.LabelContext ctx) {
        if (ctx.AT() != null) {
            this.dirs.attr("name", ctx.AT().getText());
        }
        if (ctx.NAME() != null) {
            this.dirs.attr("name", ctx.NAME().getText());
        }
        if (ctx.DOTS() != null) {
            this.dirs.attr("vararg", "");
        }
    }

    @Override
    public void exitLabel(final ProgramParser.LabelContext ctx) {
        // Nothing here
    }

    @Override
    public void enterTail(final ProgramParser.TailContext ctx) {
        this.enter();
    }

    @Override
    public void exitTail(final ProgramParser.TailContext ctx) {
        this.dirs.up();
    }

    @Override
    public void enterSuffix(final ProgramParser.SuffixContext ctx) {
        this.enter();
        if (ctx.CONST() != null) {
            this.dirs.attr("const", "");
        }
    }

    @Override
    public void exitSuffix(final ProgramParser.SuffixContext ctx) {
        this.dirs.up();
    }

    @Override
    public void enterMethod(final ProgramParser.MethodContext ctx) {
        this.dirs.add("o")
            .attr("method", "")
            .attr("line", ctx.getStart().getLine())
            .attr("base", String.format(".%s", ctx.mtd.getText())).up();
    }

    @Override
    public void exitMethod(final ProgramParser.MethodContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ConfusingTernary"})
    public void enterHead(final ProgramParser.HeadContext ctx) {
        this.dirs.add("o").attr("line", ctx.getStart().getLine());
        if (ctx.COPY() != null) {
            this.dirs.attr("copy", "");
        }
        String base = "";
        if (ctx.NAME() != null) {
            base = ctx.NAME().getText();
            if (ctx.DOT() != null) {
                base = String.format(".%s", base);
            }
        } else if (ctx.AT() != null) {
            base = "@";
        } else if (ctx.XI() != null) {
            base = "$";
        } else if (ctx.STAR() != null) {
            base = "array";
            this.dirs.attr("data", "array");
        } else if (ctx.RHO() != null) {
            base = "^";
        } else if (ctx.SIGMA() != null) {
            base = "&";
        }
        if (!base.isEmpty()) {
            this.dirs.attr("base", base);
        }
    }

    @Override
    public void exitHead(final ProgramParser.HeadContext ctx) {
        if (ctx.DOTS() != null) {
            this.dirs.attr("unvar", "");
        }
        this.dirs.up();
    }

    @Override
    public void enterHas(final ProgramParser.HasContext ctx) {
        this.enter();
        this.dirs.attr("as", ctx.NAME().getText());
    }

    @Override
    public void exitHas(final ProgramParser.HasContext ctx) {
        this.dirs.up();
    }

    @Override
    public void enterApplication(final ProgramParser.ApplicationContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void exitApplication(final ProgramParser.ApplicationContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void enterHtail(final ProgramParser.HtailContext ctx) {
        this.enter();
    }

    @Override
    public void exitHtail(final ProgramParser.HtailContext ctx) {
        this.dirs.up();
    }

    // @checkstyle ExecutableStatementCountCheck (100 lines)
    @Override
    @SuppressWarnings({ "PMD.ConfusingTernary", "PMD.CyclomaticComplexity" })
    public void enterData(final ProgramParser.DataContext ctx) {
        final String type;
        final String data;
        final String text = ctx.getText();
        if (ctx.BYTES() != null) {
            type = "bytes";
            data = text.replaceAll("\\s+", "").replace("-", " ").trim();
        } else if (ctx.BOOL() != null) {
            type = "bool";
            data = Boolean.toString(Boolean.parseBoolean(text));
        } else if (ctx.CHAR() != null) {
            type = "char";
            data = text.substring(1, text.length() - 1);
        } else if (ctx.FLOAT() != null) {
            type = "float";
            data = Double.toString(Double.parseDouble(text));
        } else if (ctx.INT() != null) {
            type = "int";
            data = Long.toString(Long.parseLong(text));
        } else if (ctx.REGEX() != null) {
            type = "regex";
            data = text.substring(1, text.lastIndexOf('/'));
            this.dirs.attr("flags", text.substring(text.lastIndexOf('/') + 1));
        } else if (ctx.HEX() != null) {
            type = "int";
            data = Long.toString(
                // @checkstyle MagicNumberCheck (1 line)
                Long.parseLong(text.substring(2), 16)
            );
        } else if (ctx.STRING() != null) {
            type = "string";
            data = text.substring(1, text.length() - 1);
        } else if (ctx.TEXT() != null) {
            type = "string";
            final int indent = ctx.getStart().getCharPositionInLine();
            data = XeListener.trimMargin(text, indent);
        } else {
            throw new ParsingException(
                String.format(
                    "Unknown data type at line #%d",
                    ctx.getStart().getLine()
                ),
                new IllegalArgumentException(),
                ctx.getStart().getLine()
            );
        }
        this.dirs.attr("data", type);
        this.dirs.attr("base", type);
        this.dirs.set(
            data
                .replace("\n", "\\n")
                .replace("\r", "\\r")
        );
    }

    @Override
    public void exitData(final ProgramParser.DataContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void visitTerminal(final TerminalNode node) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void visitErrorNode(final ErrorNode node) {
        throw new ParsingException(
            node.getText(),
            new IllegalArgumentException(),
            0
        );
    }

    @Override
    public void enterEveryRule(final ParserRuleContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    @Override
    public void exitEveryRule(final ParserRuleContext ctx) {
        // This method is created by ANTLR and can't be removed
    }

    /**
     * Help method.
     */
    private void enter() {
        this.dirs.xpath("o[last()]").strict(1);
    }

    /**
     * Trim margin from text block.
     * @param text Text block.
     * @param indent Indentation level.
     * @return Trimmed text.
     */
    private static String trimMargin(final String text, final int indent) {
        final String rexp = "\n\\s{%d}";
        String res = text
            // @checkstyle MagicNumberCheck (1 line)
            .substring(3, text.length() - 3);
        res = res.replaceAll(String.format(rexp, indent), "\n");
        if (!res.isEmpty() && res.charAt(0) == '\n') {
            res = res.substring(1);
        }
        if (!res.isEmpty() && res.charAt(res.length() - 1) == '\n') {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

}
