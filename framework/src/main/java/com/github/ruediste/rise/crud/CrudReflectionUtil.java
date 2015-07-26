package com.github.ruediste.rise.crud;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;

public class CrudReflectionUtil {

    public List<PropertyDeclaration> getDisplayProperties(Class<?> cls) {
        return new ArrayList<>(PropertyUtil.getPropertyIntroductionMap(cls)
                .values());
    }

    public List<PropertyDeclaration> getEditProperties(Class<?> cls) {
        return new ArrayList<>(PropertyUtil.getPropertyIntroductionMap(cls)
                .values());
    }

    public List<PropertyDeclaration> getBrowserProperties(Class<?> cls) {
        ArrayList<PropertyDeclaration> result = new ArrayList<>();

        Collection<PropertyDeclaration> allDeclarations = PropertyUtil
                .getPropertyIntroductionMap(cls).values();
        for (PropertyDeclaration declaration : allDeclarations) {
            Field backingField = declaration.getBackingField();
            if (backingField == null)
                continue;
            if (backingField.isAnnotationPresent(CrudBrowserColumn.class))
                result.add(declaration);
        }
        if (result.isEmpty())
            return new ArrayList<>(allDeclarations);
        else
            return result;
    }
}
