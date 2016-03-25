package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.SingleChildRelation;

@DefaultTemplate(RenderChildrenTemplate.class)
public class CZoneId extends CInputBase<CZoneId> {

    SingleChildRelation<Component, CZoneId> child = new SingleChildRelation<>(this);

    CSelect<String> select = new CSelect<>();

    public CZoneId() {
        ArrayList<String> ids = new ArrayList<String>();
        ids.addAll(ZoneId.getAvailableZoneIds().stream().sorted().collect(toList()));
        select.setAllowEmpty(true);
        select.setItems(ids);
        child.set(select);
    }

    public Optional<ZoneId> getValue() {
        return select.getSelectedItem().map(id -> ZoneId.of(id));
    }

    public void setValue(Optional<ZoneId> value) {
        select.setSelectedItem(value.map(x -> x.getId()));
    }
}
