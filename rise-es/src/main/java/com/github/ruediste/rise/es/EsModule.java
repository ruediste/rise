package com.github.ruediste.rise.es;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.CoreConfiguration;
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
        String esUrl = System.getenv("RISE_ES_URL");
        if (esUrl == null)
            esUrl = "http://localhost:9200";

        factory.setHttpClientConfig(new HttpClientConfig.Builder(esUrl).multiThreaded(true).gson(gson).build());

        client = factory.getObject();

        binder().requestInjection(new Object() {

            @Inject
            EsEntityArgumentSerializer serializer;

            @Inject
            CoreConfiguration config;

            @PostConstruct
            public void setup() {
                config.argumentSerializerSuppliers.add(() -> serializer);

            }
        });
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
