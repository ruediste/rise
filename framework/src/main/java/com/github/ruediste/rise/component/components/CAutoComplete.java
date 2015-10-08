package com.github.ruediste.rise.component.components;

import java.util.List;
import java.util.function.Supplier;

/**
 * Text Field with support for autocompletion.
 * 
 * <p>
 * The text field will typically be used to search for existing entries.
 * 
 */
@DefaultTemplate(CAutoCompleteTemplate.class)
public class CAutoComplete<Titem, Tid>
        extends CInputBase<CAutoComplete<Titem, Tid>> {

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
    }

    private Titem chosenItem;

    private String text;

    private boolean itemChosen;

    private final CAutoCompleteParameters<Titem, Tid> parameters;

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
        return itemChosen;
    }

    /**
     * The current item of the auto complete. If the user did not choose an
     * item, but edited the text before calling this method, a search is
     * triggered. If only a single element is found it is returned. Otherwise
     * (user chose an item), the chosen item is returned
     */
    public Titem getItem() {
        if (isItemChosen())
            return chosenItem;
        List<Titem> items = getParameters().search(getText());
        if (items.size() == 1)
            return items.get(0);
        return null;
    }

    /**
     * Set the currently chosen item. Equivalent to
     * {@link #setChosenItem(Object)}
     */
    public CAutoComplete<Titem, Tid> setItem(Titem item) {
        if (item == null)
            return setText("");
        else
            return setChosenItem(item);
    }

    /**
     * Item which was chosen from the suggestions. Will be null if the user
     * edited the value text afterwards.
     * 
     * @see #getText()
     */
    public Titem getChosenItem() {
        return chosenItem;
    }

    public CAutoComplete<Titem, Tid> setChosenItem(Titem chosenItem) {
        this.chosenItem = chosenItem;
        this.text = null;
        itemChosen = true;
        return this;
    }

    /**
     * Text the user entered. Null if an item was chosen
     * 
     * @see #getChosenItem()
     */
    public String getText() {
        return text;
    }

    public CAutoComplete<Titem, Tid> setText(String text) {
        this.text = text;
        this.chosenItem = null;
        itemChosen = false;
        return this;
    }

    public CAutoCompleteParameters<Titem, Tid> getParameters() {
        return parameters;
    }

    public CAutoComplete<Titem, Tid> bindItem(Supplier<Titem> itemAccessor) {
        return bindLabelProperty(c -> c.setItem(itemAccessor.get()));
    }

}
