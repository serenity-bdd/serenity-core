package net.thucydides.core.reports.renderer;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.asciidoctor.Asciidoctor.Factory.create;

public class AsciidocMarkupRenderer implements MarkupRenderer {

    Asciidoctor asciidoctor;

    public AsciidocMarkupRenderer() {}

    private Asciidoctor getAsciidoctor() {
        if (asciidoctor == null) {
            asciidoctor = create();
        }
        return asciidoctor;
    }

    @Override
    public String render(String text) {
        String textToRender = Optional.ofNullable(text).orElse("");
        return Optional.ofNullable(
                getAsciidoctor().convert(addAsciidocLineBreaks(textToRender), getOptions())
        ).orElse("");
    }


    public String addAsciidocLineBreaks(final String text) {
        return (text != null) ?
                text.replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, " +" + IOUtils.LINE_SEPARATOR_WINDOWS)
                        .replaceAll(IOUtils.LINE_SEPARATOR_UNIX, " +" + IOUtils.LINE_SEPARATOR_UNIX) : "";
    }


    private Options getOptions() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(Attributes.EXPERIMENTAL,true);
        attributes.put(Attributes.DATA_URI,true);
        Map<String, Object> options = new HashMap<>();
        options.put(Options.COMPACT,true);
        options.put(Options.ATTRIBUTES,attributes);
        options.put(Options.DOCTYPE,"inline");
        return new Options(options);
    }
}
