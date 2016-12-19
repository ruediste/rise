package com.github.ruediste.rise.es.api;

@EsRoot
public abstract class EsEntity {

    private @NotStored String id;

    private @NotStored Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
