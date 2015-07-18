package com.github.ruediste.rise.crud;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.crud.annotations.CrudColumn;

public class CrudReflectionUtil {

    List<PropertyDeclaration> getBrowserProperties(Class<?> cls) {
        Map<String, PropertyInfo> infoMap = PropertyUtil
                .getPropertyInfoMap(cls);
        ArrayList<PropertyDeclaration> result = new ArrayList<>();
        for (PropertyDeclaration declaration : PropertyUtil
                .getPropertyIntroductionMap(cls).values()) {
            Field backingField = declaration.getBackingField();
            if (backingField == null)
                continue;
            if (backingField.isAnnotationPresent(CrudColumn.class))
                result.add(declaration);
        }
        return result;
    }
}
