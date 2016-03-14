package com.github.ruediste.rise.component.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;

public class CDataGridTest {

    @Test
    public void setItemsIsPropertyAccessor() {
        Map<String, PropertyDeclaration> propertyDeclarations = PropertyUtil.getPropertyDeclarations(CDataGrid.class);
        assertTrue(propertyDeclarations.containsKey("items"));

        @SuppressWarnings("unchecked")
        PropertyPath path = PropertyUtil.getPropertyPath(CDataGrid.class, x -> x.setItems((List<?>) null));
        assertEquals("items", path.getAccessedProperty().getName());
    }
}
