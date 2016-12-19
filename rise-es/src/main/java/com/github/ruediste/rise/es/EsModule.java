package com.github.ruediste.rise.es;

import javax.inject.Singleton;

import com.github.ruediste.rise.es.api.GsonFactory;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;
import com.google.gson.Gson;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class EsModule extends AbstractModule {

    private Gson gson;
    private JestClient client;

    @Override
    protected void configure() throws Exception {
        gson = GsonFactory.createGson();
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(
                new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).gson(gson).build());

        client = factory.getObject();

    }

    @Provides
    @Singleton
    public JestClient jestClient() {
        return client;
    }

    @Provides
    @Singleton
    public Gson gson() {
        return gson;
    }
}