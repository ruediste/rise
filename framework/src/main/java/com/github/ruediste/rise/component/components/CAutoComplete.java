package com.github.ruediste.rise.component.components;

import java.util.List;
import java.util.function.Function;

/**
 * Text Field with support for autocompletion.
 * 
 * <p>
 * The text field will typically be used to search for existing entries.
 * 
 */
@DefaultTemplate(CAutoCompleteTemplate.class)
public class CAutoComplete<T> extends CInputBase<CAutoComplete<T>> {

    /**
     * Item which was chosen from the suggestions. Will be null if the user
     * edited the value afterwards
     */
    private T chosenItem;

    /**
     * Text the user entered. Null if an item was chosen
     */
    private String text;

    /**
     * If set to true, an item has been chosen and the text is null.
     */
    private boolean itemChosen;

    /**
     * Function to search items for a search term
     */
    private Function<String, List<T>> searchFunction;

    /**
     * Function to extract the suggested text from an item
     */
    private Function<T, String> suggestionFunction;

    /**
     * Function to extract the value to set the text field to from an item
     */
    private Function<T, String> valueFunction;

    public Function<String, List<T>> getSearchFunction() {
        return searchFunction;
    }

    public CAutoComplete<T> setSearchFunction(
            Function<String, List<T>> searchFunction) {
        this.searchFunction = searchFunction;
        return this;
    }

    public Function<T, String> getSuggestionFunction() {
        return suggestionFunction;
    }

    public CAutoComplete<T> setSuggestionFunction(
            Function<T, String> suggestionFunction) {
        this.suggestionFunction = suggestionFunction;
        return this;
    }

    public Function<T, String> getValueFunction() {
        return valueFunction;
    }

    public CAutoComplete<T> setValueFunction(
            Function<T, String> valueFunction) {
        this.valueFunction = valueFunction;
        return this;
    }

    public boolean isItemChosen() {
        return itemChosen;
    }

    public T getChosenItem() {
        return chosenItem;
    }

    public CAutoComplete<T> setChosenItem(T chosenItem) {
        this.chosenItem = chosenItem;
        this.text = null;
        itemChosen = true;
        return this;
    }

    public String getText() {
        return text;
    }

    public CAutoComplete<T> setText(String text) {
        this.text = text;
        this.chosenItem = null;
        itemChosen = false;
        return this;
    }
}
