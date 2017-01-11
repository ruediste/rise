package com.github.ruediste.rise.es.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class GsonFactory {

    private static final class NotStoredExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(NotStored.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private static class PolymorphismAdapter<T> extends TypeAdapter<T> {

        private Gson gson;
        private TypeAdapterFactory factory;
        private Class<?> type;

        public PolymorphismAdapter(TypeAdapterFactory factory, Gson gson, Class<?> type) {
            this.factory = factory;
            this.gson = gson;
            this.type = type;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null)
                out.nullValue();
            else {
                out.beginArray();
                out.value(value.getClass().getName());
                ((TypeAdapter) gson.getDelegateAdapter(factory, TypeToken.get(value.getClass()))).write(out, value);
                out.endArray();
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL)
                return null;
            in.beginArray();
            String className = in.nextString();
            Class<?> klass = null;
            try {
                klass = type.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }

            T instance = (T) ((TypeAdapter) gson.getDelegateAdapter(factory, TypeToken.get(klass))).read(in);
            in.endArray();

            return instance;
        }
    }

    private static class PolymorphismAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<? super T> rawType = type.getRawType();
            if (rawType.isAnnotationPresent(GsonPolymorph.class)) {
                return new PolymorphismAdapter<>(this, gson, rawType);
            }
            return null;
        }

    }

    private static class OpaqueTypeAdapter<T> extends TypeAdapter<T> {
        static Charset utf8 = Charset.forName("UTF-8");

        private TypeAdapter<T> delegate;

        public OpaqueTypeAdapter(TypeAdapter<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null)
                out.nullValue();
            else {

                StringWriter sw = new StringWriter();

                try (JsonWriter jw = new JsonWriter(sw)) {
                    delegate.write(jw, value);
                }
                // out.value(Base64.getEncoder().encodeToString(sw.toString().getBytes(utf8)));
                out.value(sw.toString());
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL)
                return null;
            String s = in.nextString();
            // StringReader sr = new StringReader(new
            // String(Base64.getDecoder().decode(s), utf8));
            StringReader sr = new StringReader(new String(s));
            return delegate.read(new JsonReader(sr));
        }
    }

    private static class OpaqueTypeAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType().isAnnotationPresent(Opaque.class)) {
                TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                return new OpaqueTypeAdapter<>(delegate);
            }
            return null;
        }
    }

    private static class NamingStrategy implements FieldNamingStrategy {

        @Override
        public String translateName(Field f) {
            EsRoot esRoot = f.getDeclaringClass().getAnnotation(EsRoot.class);
            if (esRoot != null && esRoot.typedFieldNames()) {
                // direct EsFieldPrefix annotation
                {
                    EsFieldPrefix prefix = f.getAnnotation(EsFieldPrefix.class);
                    if (prefix != null) {
                        return prefix.value() + "_" + f.getName();
                    }
                }

                // Meta EsFieldPrefixAnnotation
                for (Annotation annotation : f.getAnnotations()) {
                    EsFieldPrefix prefix = annotation.annotationType().getAnnotation(EsFieldPrefix.class);
                    if (prefix != null) {
                        return prefix.value() + "_" + f.getName();
                    }
                }

                if (String.class.equals(f.getType()) || new TypeToken<List<String>>() {
                }.getType().equals(f.getGenericType())) {
                    if (f.isAnnotationPresent(NotIndexed.class))
                        return "tni_" + f.getName();
                    else if (f.isAnnotationPresent(Keyword.class))
                        return "k_" + f.getName();
                    else
                        return "t_" + f.getName();
                }

                if (Long.class.equals(f.getType()) || Long.TYPE.equals(f.getType()))
                    return "l_" + f.getName();

                if (Integer.class.equals(f.getType()) || Integer.TYPE.equals(f.getType()))
                    return "i_" + f.getName();

                if (Boolean.class.equals(f.getType()) || Boolean.TYPE.equals(f.getType()))
                    return "b_" + f.getName();

                if (Instant.class.equals(f.getType()))
                    return "d_" + f.getName();

                if (f.getType().isAnnotationPresent(Opaque.class))
                    return "sni_" + f.getName();
            }
            return f.getName();
        }

    }

    public static class InstantTypeAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            if (value == null)
                out.nullValue();
            else
                out.value(value.toEpochMilli());
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Instant.ofEpochMilli(in.nextLong());
        }
    }

    public static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter out, Duration value) throws IOException {
            if (value == null)
                out.nullValue();
            else
                out.value(value.toMillis());
        }

        @Override
        public Duration read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Duration.ofMillis(in.nextLong());
        }
    }

    public static Gson createGson() {
        return createBaseBuilder().setExclusionStrategies(new NotStoredExclusionStrategy())
                .setFieldNamingStrategy(new NamingStrategy()).registerTypeAdapterFactory(new OpaqueTypeAdapterFactory())
                .create();
    }

    public static GsonBuilder createBaseBuilder() {
        return new GsonBuilder().registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapterFactory(new PolymorphismAdapterFactory());
    }
}
