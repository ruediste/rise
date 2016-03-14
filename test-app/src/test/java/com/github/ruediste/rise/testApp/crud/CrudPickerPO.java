package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.crud.DefaultCrudPickerController;

public class CrudPickerPO extends CrudListPO<CrudPickerPO> {

    /**
     * Choose the entity with the given index
     */
    public CrudEditPO choose(int rowIndex) {
        getActions(rowIndex).findElement(byDataTestName(DefaultCrudPickerController.class, x -> x.pick(null))).click();
        return pageObject(CrudEditPO.class);
    }
}
