package com.github.ruediste.rise.integration;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;

public interface ResourceProcessing {
    void minifyCss(Reader reader, Writer writer);

    static public String process(String input,
            BiConsumer<Reader, Writer> processor) {
        StringWriter out = new StringWriter();
        processor.accept(new StringReader(input), out);
        return out.toString();
    }

}
