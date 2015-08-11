package net.thucydides.core.reports.renderer;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;

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
        return getAsciidoctor().render(addAsciidocLineBreaks(text), getOptions());
    }


    public String addAsciidocLineBreaks(final String text) {
        return (text != null) ?
                text.replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, " +" + IOUtils.LINE_SEPARATOR_WINDOWS)
                        .replaceAll(IOUtils.LINE_SEPARATOR_UNIX, " +" + IOUtils.LINE_SEPARATOR_UNIX) : "";
    }


    private Options getOptions() {
        Options options = new Options();
        options.setCompact(true);
        options.setDocType("inline");

        Attributes attributes = new Attributes();
        attributes.setExperimental(true);
        attributes.setDataUri(true);

        options.setAttributes(attributes);

        return options;

    }
}
