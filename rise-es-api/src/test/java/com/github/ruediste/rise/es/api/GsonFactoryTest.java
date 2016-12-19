package com.github.ruediste.rise.es.api;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonFactoryTest {

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = GsonFactory.createGson();
    }

    @EsRoot
    public static class TestEntity {
        String indexedField;

        @Opaque
        public static class Data {
            String nonIndexedField;
        }

        Data data = new Data();
    }

    @Test
    public void testSerializeEntity() {
        TestEntity e = new TestEntity();
        e.indexedField = "foo";
        e.data.nonIndexedField = "bar";

        JsonObject parsed = new JsonParser().parse(gson.toJson(e)).getAsJsonObject();
        assertEquals("foo", parsed.get("s_indexedField").getAsString());
        assertEquals("eyJub25JbmRleGVkRmllbGQiOiJiYXIifQ==", parsed.get("bl_data").getAsString());

        e = gson.fromJson(parsed, TestEntity.class);
        assertEquals("bar", e.data.nonIndexedField);
    }
}
