package com.github.ruediste.rise.component.components;

import java.util.List;
import java.util.function.Supplier;

import com.github.ruediste.c3java.invocationRecording.TerminalType;

/**
 * Text Field with support for autocompletion.
 * 
 * <p>
 * The text field will typically be used to search for existing entries.
 * 
 * <p>
 * <b> Auto Search Mode </b> <br>
 * When the user enters a text but does not select a proposed item, a search
 * with the entered text can be performed prior to returning the chosen item. In
 * {@link AutoSearchMode#SINGLE} an item is considered selected if only a single
 * match is found for the text. In {@link AutoSearchMode#SINGLE_MATCHING}
 * (default mode), an item is considered selected if only a single match is
 * found and that item has a {@link CAutoCompleteParameters#getValue(Object)}
 * equal to the current text.
 */
@DefaultTemplate(CAutoCompleteTemplate.class)
public class CAutoComplete<Titem, Tid>
        extends CInputBase<CAutoComplete<Titem, Tid>> {

    public enum AutoSearchMode {
        NONE, SINGLE, SINGLE_MATCHING, SINGLE_MATCHING_IGNORE_CASE;
    }

    @TerminalType
    public static class AutoCompleteValue<T> {
        private final boolean isItemChosen;
        private final T item;
        private final String text;

        private AutoCompleteValue(boolean isItemChosen, T item, String text) {
            this.isItemChosen = isItemChosen;
            this.item = item;
            this.text = text;
        }

        public static <T> AutoCompleteValue<T> ofItem(T item) {
            return new AutoCompleteValue<T>(true, item, null);
        }

        public static <T> AutoCompleteValue<T> ofText(String text) {
            return new AutoCompleteValue<T>(false, null, text);
        }

        public boolean isItemChosen() {
            return isItemChosen;
        }

        public T getItem() {
            return item;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "AutoCompleteValue("
                    + (isItemChosen ? "item=" + item : "text=" + text) + ")";
        }
    }

    public interface CAutoCompleteParameters<Titem, Tid> {
        /**
         * Function to search items for a search term
         */
        List<Titem> search(String term);

        /**
         * Function to extract the suggested text from an item
         */
        String getSuggestion(Titem item);

        /**
         * Function to extract the value to set the text field to from an item
         */
        String getValue(Titem item);

        /**
         * Get the serializable form of an item, which will be signed and sent
         * to the client.
         */
        Tid getId(Titem item);

        /**
         * Load the item for a an id.
         */
        Titem load(Tid id);

        /**
         * Get the test name for the item, used to identify the item in tests
         */
        String getTestName(Titem item);
    }

    private AutoCompleteValue<Titem> value;

    private AutoSearchMode autoSearchMode = AutoSearchMode.SINGLE_MATCHING;

    private final CAutoCompleteParameters<Titem, Tid> parameters;

    /**
     * for proxy generation
     */
    CAutoComplete() {
        parameters = null;
    }

    public CAutoComplete(CAutoCompleteParameters<Titem, Tid> parameters) {
        this.parameters = parameters;
    }

    /**
     * If true, an item has been chosen and the text is null.
     */
    public boolean isItemChosen() {
        return value.isItemChosen;
    }

    public CAutoComplete<Titem, Tid> setValue(AutoCompleteValue<Titem> value) {
        this.value = value;
        return this;
    }

    /**
     * Return the current value of the auto complete. If the user did not choose
     * an item, or edited the text after choosing, a search with the current
     * text is attempted according to {@link #getAutoSearchMode()}.
     */
    public AutoCompleteValue<Titem> getValue() {
        if (value.isItemChosen() || autoSearchMode == AutoSearchMode.NONE)
            return value;
        List<Titem> items = getParameters().search(value.getText());
        if (items.size() == 1) {
            Titem singleItem = items.get(0);
            AutoCompleteValue<Titem> singleValue = AutoCompleteValue
                    .ofItem(singleItem);
            switch (autoSearchMode) {
            case SINGLE: {
                return singleValue;
            }
            case SINGLE_MATCHING:
                if (value.getText().equals(parameters.getValue(singleItem)))
                    return singleValue;
                break;
            case SINGLE_MATCHING_IGNORE_CASE:
                if (value.getText()
                        .equalsIgnoreCase(parameters.getValue(singleItem)))
                    return singleValue;
                break;
            default:
                throw new UnsupportedOperationException();
            }
        }
        return value;
    }

    public Titem getItem() {
        AutoCompleteValue<Titem> v = getValue();
        if (v.isItemChosen())
            return v.getItem();
        else
            return null;
    }

    /**
     * Set the currently chosen item. If the item is null, the text is set to
     * empty instead
     */
    public CAutoComplete<Titem, Tid> setItem(Titem item) {
        if (item == null)
            return setText("");
        value = AutoCompleteValue.ofItem(item);
        return this;
    }

    /**
     * Text the user entered. Null if an item was chosen
     */
    public String getText() {
        return value.isItemChosen() ? null : value.getText();
    }

    public CAutoComplete<Titem, Tid> setText(String text) {
        value = AutoCompleteValue.ofText(text);
        return this;
    }

    public CAutoCompleteParameters<Titem, Tid> getParameters() {
        return parameters;
    }

    public CAutoComplete<Titem, Tid> bindItem(Supplier<Titem> itemAccessor) {
        return bindLabelProperty(c -> c.setItem(itemAccessor.get()));
    }

    public AutoSearchMode getAutoSearchMode() {
        return autoSearchMode;
    }

    public CAutoComplete<Titem, Tid> setAutoSearchMode(
            AutoSearchMode autoSearchMode) {
        this.autoSearchMode = autoSearchMode;
        return this;
    }

}
