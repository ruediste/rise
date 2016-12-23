package com.github.ruediste.rise.es.api;

@EsRoot
public abstract class EsEntity<T extends EsEntity<T>> {

    private @NotStored String id;

    private @NotStored Long version;

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public T setVersion(Long version) {
        this.version = version;
        return self();
    }

}
