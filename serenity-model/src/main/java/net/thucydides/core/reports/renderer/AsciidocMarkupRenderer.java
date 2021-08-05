package net.thucydides.core.reports.renderer;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;

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
        return Options.builder()
                .compact(true)
                .docType("inline")
                .attributes(Attributes.builder().experimental(true).dataUri(true).build())
                .build();
    }
}
