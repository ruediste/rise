package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.crud.CrudControllerBase;

public class CrudBrowserPO extends CrudListPO<CrudBrowserPO> {

    @Override
    protected void initialize() {
        assertPage(CrudControllerBase.class, x -> x.browse(null, null));
    }

    /**
     * Open the view for the given row index
     */
    public CrudDisplayPO display(int rowIndex) {
        getActions(rowIndex).findElement(byDataTestName(CrudControllerBase.class, x -> x.display(null))).click();
        return pageObject(CrudDisplayPO.class);
    }

    public CrudEditPO edit(int rowIndex) {
        getActions(rowIndex).findElement(byDataTestName(CrudControllerBase.class, x -> x.edit(null))).click();
        return pageObject(CrudEditPO.class);
    }

    public CrudDeletePO delete(int rowIndex) {
        getActions(rowIndex).findElement(byDataTestName(CrudControllerBase.class, x -> x.delete(null))).click();
        return pageObject(CrudDeletePO.class);
    }
}
