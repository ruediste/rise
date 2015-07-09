package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.lString.LString;

@DefaultTemplate(CDataGridTemplate.class)
public class CDataGrid<T> extends ComponentBase<CDataGrid<T>> {

    public enum HAlign {
        LEFT("left"), RIGHT("right"), CENTER("center"), JUSTIFY("justify"), JUSTIFY_ALL(
                "justify-all");

        private final String cssClass;

        private HAlign(String cssClass) {
            this.cssClass = cssClass;

        }

        public String getCssClass() {
            return cssClass;
        }
    }

    public enum VAlign {
        BASELINE("baseline"), TOP("text-top"), BOTTOM("text_bottom"), CENTER(
                "middle");

        private final String cssClass;

        private VAlign(String cssClass) {
            this.cssClass = cssClass;

        }

        public String getCssClass() {
            return cssClass;
        }
    }

    public static class Cell {
        public Optional<Component> component = Optional.empty();
        public HAlign halign = null;
        public VAlign valign = null;

        public Cell() {

        }

        public Cell(LString text) {
            this(new CText(text));
        }

        public Cell(Component component) {
            this.component = Optional.of(component);
        }

        public Cell halign(HAlign halign) {
            this.halign = halign;
            return this;
        }

        public Cell valign(VAlign valign) {
            this.valign = valign;
            return this;
        }
    }

    public static class Column<T> {
        Supplier<Cell> headSupplier;
        Function<T, Cell> cellFactory;

        public Column(Supplier<Cell> headSupplier, Function<T, Cell> cellFactory) {
            super();
            this.headSupplier = headSupplier;
            this.cellFactory = cellFactory;
        }
    }

    private boolean itemsDirty = true;
    private boolean columnsDirty = true;
    private List<T> items;
    private List<Column<T>> columns = new ArrayList<>();

    @NoPropertyAccessor
    public void setItems(@SuppressWarnings("unchecked") T... items) {
        setItems(Arrays.asList(items));
    }

    public void setItems(List<T> items) {
        itemsDirty = true;
        this.items = new ArrayList<>(items);
    }

    public CDataGrid<T> setColumns(
            @SuppressWarnings("unchecked") Column<T>... columns) {
        setColumns(Arrays.asList(columns));
        return this;
    }

    public CDataGrid<T> setColumns(Collection<Column<T>> columns) {
        columnsDirty = true;
        this.columns = new ArrayList<>(columns);
        return this;
    }

    public CDataGrid<T> addColumn(Supplier<Cell> headSupplier,
            Function<T, Cell> cellFactory) {
        columns.add(new Column<>(headSupplier, cellFactory));
        return this;
    }

    private Map<Column<T>, Cell> headers = new HashMap<>();
    private Map<Pair<Column<T>, T>, Cell> cells = new HashMap<>();

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }

    void updateCells() {
        if (!columnsDirty && !itemsDirty)
            return;

        if (columnsDirty) {
            Map<Column<T>, Cell> newHeaders = headers;
            for (Column<T> column : columns) {
                Cell cell = headers.get(column);
                if (cell == null)
                    cell = column.headSupplier.get();
                newHeaders.put(column, cell);
            }
            headers = newHeaders;
        }
        {
            Map<Pair<Column<T>, T>, Cell> newCells = new HashMap<>();
            for (T item : items)
                for (Column<T> column : columns) {
                    Pair<Column<T>, T> key = Pair.of(column, item);

                    Cell cell = cells.get(key);
                    if (cell == null)
                        cell = column.cellFactory.apply(item);
                    newCells.put(key, cell);
                }
            cells = newCells;
        }
        columnsDirty = false;
        itemsDirty = false;
    }

    public Cell getHeaderCell(Column<T> column) {
        updateCells();
        return headers.get(column);
    }

    public Cell getCell(Column<T> column, T item) {
        updateCells();
        return cells.get(Pair.of(column, item));
    }

    @Override
    public Iterable<Component> getChildren() {
        updateCells();
        return Stream
                .concat(headers.values().stream(), cells.values().stream())
                .filter(x -> x.component.isPresent())
                .map(x -> x.component.get()).collect(toList());
    }

    public List<T> getItems() {
        return items;
    }

    public List<Column<T>> getColumns() {
        return columns;
    }
}
