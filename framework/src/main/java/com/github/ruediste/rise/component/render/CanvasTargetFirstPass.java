package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.CanvasTargetToConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducerHtmlCanvasTarget;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CanvasTargetFirstPass extends HtmlProducerHtmlCanvasTarget implements RiseCanvasTarget {

    @Override
    public void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        this.checkAttributesUncommited();
        this.addProducer(new HtmlProducer() {

            @Override
            public void produce(HtmlConsumer consumer) {
                CanvasTargetForAttributePlaceholder target = new CanvasTargetForAttributePlaceholder() {

                    @Override
                    protected void write(String str) {
                        consumer.accept(str);
                    }
                };
                html.setTarget(target);
                placeholder.run();
            }
        }, false);
    }

    @Override
    public void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        this.addProducer(new HtmlProducer() {

            @Override
            public void produce(HtmlConsumer consumer) {
                CanvasTargetToConsumer target = new CanvasTargetToConsumer(consumer);
                html.setTarget(new CanvasTargetForPlaceholder(target));
                placeholder.run();
                target.commitAttributes();
                target.checkAllTagsClosed();
                target.flush();
            }
        }, true);
    }
}
