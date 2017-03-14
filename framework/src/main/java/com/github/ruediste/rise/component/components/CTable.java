package com.github.ruediste.rise.component.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste1.i18n.lString.LString;

public class CTable<T> extends Component<CTable<T>> {

    public static class Template<T> extends BootstrapComponentTemplateBase<CTable<T>> {

        @Override
        public void doRender(CTable<T> table, BootstrapRiseCanvas<?> html) {
            html.bTable().bTr();
            for (Column<T> column : table.columns) {
                html.bTh();
                column.renderHead(html);
                html._bTh();
            }
            html._bTr();

            for (T item : table.items.get()) {
                html.bTr();
                for (Column<T> column : table.columns) {
                    html.bTd();
                    column.renderCell(item, html);
                    html._bTd();
                }
                html._bTr();
            }
            html._bTable();
        }

    }

    public static abstract class Column<T> {
        abstract public void renderHead(HtmlCanvas<?> html);

        abstract public void renderCell(T item, HtmlCanvas<?> html);
    }

    public Supplier<Iterable<T>> items;
    public List<Column<T>> columns = new ArrayList<>();

    public CTable<T> items(Supplier<Iterable<T>> value) {
        this.items = value;
        return this;
    }

    public <P> CTable<T> column( Function<T, P> extractor) {
        return column(extractor, (html, x) -> html.write(Objects.toString(x)));
    }

    public <P> CTable<T> column( Function<T, P> extractor, Consumer<P> cellRenderer) {
        return column(extractor, (html, x) -> cellRenderer.accept(x));
    }

    public <P> CTable<T> column( Function<T, P> extractor, BiConsumer<HtmlCanvas<?>, P> cellRenderer) {
        CoreUtil core = InjectorsHolder.getRestartableInjector().getInstance(CoreUtil.class);
        LString label = core.labelOfLambda(extractor);
        columns.add(new Column<T>() {

            @Override
            public void renderHead(HtmlCanvas<?> html) {
                ((RiseCanvas<?>) html).write(label);
            }

            @Override
            public void renderCell(T item, HtmlCanvas<?> html) {
                cellRenderer.accept(html, extractor.apply(item));
            }
        });
        return this;
    }

    public CTable<T> column(Runnable head, Consumer<T> cell) {
        columns.add(new Column<T>() {

            @Override
            public void renderHead(HtmlCanvas<?> html) {
                head.run();
            }

            @Override
            public void renderCell(T item, HtmlCanvas<?> html) {
                cell.accept(item);
            }
        });

        return this;
    }
}
