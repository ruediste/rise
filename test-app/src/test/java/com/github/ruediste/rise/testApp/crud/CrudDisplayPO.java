package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;

import org.openqa.selenium.By;

import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.test.PageObject;

public class CrudDisplayPO extends PageObject {

    @Override
    protected void initialize() {
        assertPage(CrudControllerBase.class, x -> x.display(null));
    }

    public String getPropertyText(String name) {
        return findElement(By.cssSelector(dataTestSelector("properties") + dataTestSelector(name))).getText();
    }

    public CrudBrowserPO browse() {
        findElement(byDataTestName(CrudControllerBase.class, x -> x.browse(null, null))).click();
        return pageObject(CrudBrowserPO.class);
    }

    public CrudEditPO edit() {
        findElement(byDataTestName(CrudControllerBase.class, x -> x.edit(null))).click();
        return pageObject(CrudEditPO.class);
    }

    public CrudDeletePO delete() {
        findElement(byDataTestName(CrudControllerBase.class, x -> x.delete(null))).click();
        return pageObject(CrudDeletePO.class);
    }

    public List<String> getPropertyTestNames() {
        return findElements(By.cssSelector(dataTestSelector("properties") + "*[data-test-name]")).stream()
                .map(x -> x.getAttribute("data-test-name")).collect(toList());
    }

    public CrudListPO<?> showItems(String name) {
        findElement(By.cssSelector(dataTestSelector("properties") + dataTestSelector(name))).click();
        return pageObject(CrudListPO.class);
    }

    public <T> ActionMethodInvocationPO invokeAction(Class<T> cls, Consumer<T> accessor) {
        findElement(byDataTestName("actions")).findElement(byDataTestName(cls, accessor)).click();
        return pageObject(ActionMethodInvocationPO.class);
    }
}
