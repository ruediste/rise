package com.github.ruediste.rise.es.api;

public class EsNameHelper {
    public static String DEFAULT_INDEX = "default";

    public static String type(Object entity) {
        return type(entity.getClass());
    }

    public static String type(Class<?> entityClass) {
        EsRoot esRoot = entityClass.getAnnotation(EsRoot.class);
        if (esRoot == null || "".equals(esRoot.type()))
            return upperCamelToLowerHyphen(entityClass.getSimpleName());
        return esRoot.type();
    }

    static String upperCamelToLowerHyphen(String input) {
        StringBuilder result = new StringBuilder();

        int idx = 0;
        int wordStart = 0;
        while (idx < input.length()) {
            idx = input.offsetByCodePoints(idx, 1);
            if (idx > 0) {
                if (idx >= input.length() || Character.isUpperCase(input.codePointAt(idx))) {
                    if (wordStart > 0)
                        result.append("-");
                    result.appendCodePoint(Character.toLowerCase(input.codePointAt(wordStart)));
                    result.append(input.substring(input.offsetByCodePoints(wordStart, 1), idx));
                    wordStart = idx;
                }
            }
        }
        return result.toString();
    }

    public static String index(Class<?> cls) {
        EsRoot esRoot = cls.getAnnotation(EsRoot.class);
        if (esRoot == null)
            return DEFAULT_INDEX;
        String index = esRoot.index();
        if (IndexSuffixExtractor.class.equals(esRoot.suffixExtractor()))
            if ("".equals(index))
                return DEFAULT_INDEX;
            else
                return index;
        else {
            throw new RuntimeException(
                    "Index of " + cls.getName() + " includes a suffix, cannot determine index name from class");
        }

    }

    @SuppressWarnings("unchecked")
    public static String index(Object entity) {
        EsRoot esRoot = entity.getClass().getAnnotation(EsRoot.class);
        if (esRoot == null)
            return DEFAULT_INDEX;
        String index = esRoot.index();
        if (IndexSuffixExtractor.class.equals(esRoot.suffixExtractor()))
            if ("".equals(index))
                return DEFAULT_INDEX;
            else
                return index;
        else {
            String suffix;
            try {
                suffix = esRoot.suffixExtractor().newInstance().extract(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return index + "-" + suffix;
        }
    }

    public static String indexPattern(Class<?> entityClass) {
        EsRoot esRoot = entityClass.getAnnotation(EsRoot.class);
        if (esRoot == null)
            return DEFAULT_INDEX;
        String index = esRoot.index();
        if (IndexSuffixExtractor.class.equals(esRoot.suffixExtractor()))
            if ("".equals(index))
                return DEFAULT_INDEX;
            else
                return index;
        else {
            return index + "-*";
        }
    }

}
