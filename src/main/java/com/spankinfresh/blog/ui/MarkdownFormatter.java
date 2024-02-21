package com.spankinfresh.blog.ui;

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MarkdownFormatter {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownFormatter() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                AttributesExtension.create(),
                StrikethroughExtension.create()));
        // FOR maximum protection against cross-site
        // scripting attacks, uncomment the line below:
        // options.set(HtmlRenderer.ESCAPE_HTML, true);
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public String markdownToHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}