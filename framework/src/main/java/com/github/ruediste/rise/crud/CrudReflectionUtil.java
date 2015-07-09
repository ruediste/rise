package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;

public class CrudReflectionUtil {

    List<PropertyInfo> getListProperties(Class<?> cls) {
        Map<String, PropertyInfo> propertyInfoMap = PropertyUtil
                .getPropertyInfoMap(cls);
        return new ArrayList<>(propertyInfoMap.values());
    }
}
