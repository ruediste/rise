package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;

import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Charsets;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

/**
 * Processor to extract css image URLs and imports
 */
public class CssProcessor {

    private final class DocumentHandlerImplementation
            implements DocumentHandler {
        public DocumentHandlerImplementation(Asset asset, Ctx ctx) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void startSelector(SelectorList selectors) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void startPage(String name, String pseudo_page)
                throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void startMedia(SACMediaList media) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void startFontFace() throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void startDocument(InputSource source) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void property(String name, LexicalUnit value, boolean important)
                throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void namespaceDeclaration(String prefix, String uri)
                throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void importStyle(String uri, SACMediaList media,
                String defaultNamespaceURI) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void ignorableAtRule(String atRule) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void endSelector(SelectorList selectors) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void endPage(String name, String pseudo_page)
                throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void endMedia(SACMediaList media) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void endFontFace() throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void endDocument(InputSource source) throws CSSException {
            // TODO Auto-generated method stub

        }

        @Override
        public void comment(String text) throws CSSException {
            // TODO Auto-generated method stub

        }
    }

    private static class Ctx {
        Set<Pair<Object, String>> loadedAssets = new HashSet<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Set<String> media;
    }

    public Asset combineStyleSheets(List<Asset> assets) {
        return combineStyleSheets(assets, Collections.emptySet());
    }

    public Asset combineStyleSheets(List<Asset> assets, Set<String> media) {
        Ctx ctx = new Ctx();
        ctx.media = media;

        assets.forEach(a -> {
            try {
                process(a, ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    private void process(Asset asset, Ctx ctx) throws IOException {
        SACParserCSS3 parser = new SACParserCSS3();
        parser.setDocumentHandler(new DocumentHandlerImplementation(asset, ctx));
        
        InputSource src = new InputSource();
        src.setByteStream(new ByteArrayInputStream(asset.getData()));
        src.setEncoding(Charsets.UTF_8.name());
         parser.parseStyleSheet(src);
        
        CSSRuleList rules = sheet.getCssRules();
        ruleLoop: for (int i = 0; i < rules.getLength(); i++) {
            CSSRule rule = rules.item(i);
            System.out.println(rule);

            if (rule.getType() == CSSRule.IMPORT_RULE) {
                CSSImportRule importRule = (CSSImportRule) rule;

                // check if the import should be included based on the media
                if (!ctx.media.isEmpty()) {
                    MediaList media = importRule.getMedia();
                    boolean keep = false;
                    for (int p = 0; p < media.getLength(); p++) {
                        if (ctx.media.contains(media.item(p))) {
                            keep = true;
                            break;
                        }
                    }
                    if (!keep)
                        continue ruleLoop;
                }
                String href = importRule.getHref();
                if (!(href.startsWith("http://") || href.startsWith("https://")
                        || href.startsWith("ftp://"))) {
                    System.out.println(href);
                }
            } else if (rule.getType() == CSSRule.STYLE_RULE) {
                CSSStyleRule styleRule=(CSSStyleRule) rule;
                CSSStyleDeclaration style = styleRule.getStyle();
                for (int p=0;p<style.getLength(); p++){
                }
                style.getLength()
            }
        }
    }
}
