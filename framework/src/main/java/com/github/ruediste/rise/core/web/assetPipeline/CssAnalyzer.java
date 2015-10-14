package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor to extract css image URLs and imports
 */
public class CssAnalyzer {

    /**
     * Pattern to detect both imports and urls
     */
    private Pattern urlRewritePattern;

    {
        String imp = "(?<import>@import\\s*)" //
                + "(" //
                + "(url\\s*\\(\\s*(?<ref2>\"(.+?)\"|'(.+?)'|(.+?))\\s*\\))"//
                + "|(?<ref3>(\"(.+?)\")|(\\'(.+?)\\')|(.+?))" //
                + ")\\s*(?<media>[^;]*?)\\s*;";
        urlRewritePattern = Pattern
                .compile("(?is)(src\\\\s*=\\\\s*['\"](?<ref1>(.*?))['\"])"
                        + "|(" + imp + ")"//
                        + "|(url\\s*\\(\\s*(?<ref4>(\\\"(.+?)\\\")|(\\\\'(.+?)\\\\')|(.+?))\\s*\\))");
    }

    public void process(String content, StringBuffer sb,
            CssProcessorHandler handler) {
        Matcher matcher = urlRewritePattern.matcher(content);
        int appendPos = 0;
        while (matcher.find()) {
            String refName = "ref1";
            String ref = matcher.group(refName);
            if (ref == null) {
                refName = "ref4";
                ref = matcher.group(refName);
            }

            if (ref != null) {
                ref = trimQuotes(ref);

                sb.append(content.substring(appendPos, matcher.start(refName)));
                sb.append("\"");
                sb.append(handler.replaceRef(ref));
                sb.append("\"");
                appendPos = matcher.end(refName);
            } else if (matcher.group("import") != null) {
                // this is an import
                refName = "ref2";
                ref = matcher.group(refName);
                if (ref == null) {
                    refName = "ref3";
                    ref = matcher.group(refName);
                }
                ref = trimQuotes(ref);
                String media = matcher.group("media");
                appendPos = replaceImport(sb, content, ref, media, appendPos,
                        matcher.start(), matcher.start(refName), matcher.end(),
                        matcher.end(refName), handler);
            }
        }
        sb.append(content.substring(appendPos, content.length()));
        System.out.println(sb.toString());
    }

    private String trimQuotes(String ref) {
        if (ref.length() >= 2 && (ref.startsWith("'") || ref.startsWith("\"")))
            ref = ref.substring(1, ref.length() - 1);
        return ref;
    }

    public interface CssProcessorHandler {
        /**
         * Return a replacement for ref. The result will get double quoted.
         */
        String replaceRef(String ref);

        boolean shouldInline(String ref, String media);

        /**
         * Return a replacement for ref of an import. The result will get double
         * quoted.
         */
        String replaceImportRef(String ref);

        void performInline(StringBuffer sb, String ref, String media);
    }

    private int replaceImport(StringBuffer sb, String content, String ref,
            String media, int appendPos, int start, int startRef, int end,
            int endRef, CssProcessorHandler handler) {
        if (handler.shouldInline(ref, media)) {
            sb.append(content.substring(appendPos, start));
            handler.performInline(sb, ref, media);
            return end;
        } else {
            sb.append(content.substring(appendPos, startRef));
            sb.append("\"");
            sb.append(handler.replaceImportRef(ref));
            sb.append("\"");
            return endRef;
        }
    }

    public Pattern getUrlRewritePattern() {
        return urlRewritePattern;
    }

    public void setUrlRewritePattern(Pattern urlRewritePattern) {
        this.urlRewritePattern = urlRewritePattern;
    }
}
