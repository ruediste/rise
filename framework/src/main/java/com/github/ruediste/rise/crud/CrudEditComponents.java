package com.github.ruediste.rise.crud;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.ManyToOne;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudEditComponents
        extends
        FactoryCollectionNew<PropertyDeclaration, CrudEditComponents.CrudEditComponentFactory> {
    @Inject
    ComponentFactoryUtil util;

    @Inject
    CrudUtil crudUtil;
    @Inject
    LabelUtil labelUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    public interface CrudEditComponentFactory {
        Component create(PropertyDeclaration decl, BindingGroup<?> group);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    abstract class Targets {
        @GlyphiconIcon(Glyphicon.open)
        @Labeled
        abstract void pick();
    }

    public Component createEditComponent(PropertyDeclaration decl,
            BindingGroup<?> group) {
        return getFactory(decl).create(decl, group);
    }

    @PostConstruct
    public void initialize() {
        addFactory(
                decl -> String.class.equals(decl.getPropertyType()),
                (decl, group) -> new CTextField().setLabel(
                        labelUtil.getPropertyLabel(decl)).bindText(
                        () -> (String) decl.getValue(group.proxy())));

        addFactory(
                decl -> Long.TYPE.equals(decl.getPropertyType())
                        || Long.class.equals(decl.getPropertyType()),
                (decl, group) -> {
                    CInput result = new CInput(InputType.number)
                            .setLabel(labelUtil.getPropertyLabel(decl));

                    BindingUtil.bind(result, group, entity -> result
                            .setValue(String.valueOf(decl.getValue(entity))),
                            entity -> decl.setValue(entity,
                                    Long.parseLong(result.getValue())));
                    return result;
                });

        addFactory(
                decl -> decl.getBackingField() != null
                        && decl.getBackingField().isAnnotationPresent(
                                ManyToOne.class),
                (decl, group) -> {
                    ManagedType<?> managedType = persistenceUtil.getManagedType(
                            persistenceUtil.getEmQualifier(group.get()), group
                                    .get().getClass());

                    TypeToken<?> propertyType = TypeToken.of(decl
                            .getPropertyType());
                    Class<?> propertyCls = propertyType.getRawType();
                    Class<?> cls = propertyCls;

                    //@formatter:off
                    return toComponent(html -> html
                            .bFormGroup()
                              .label().content(labelUtil.getPropertyLabel(decl))
                            .span().B_FORM_CONTROL().DISABLED("disbled")
                                .render(x-> crudUtil.getStrategy(
                                                          IdentificationRenderer.class,
                                                          cls).renderIdenification(
                                                          html, decl.getValue(group.get())))
                            ._span()
                            .add(new CButton(this,(btn, c)->c.pick(btn)))
                            ._bFormGroup());
                    //@formatter:on);
                });
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.hand_right)
    void pick(CButton btn) {

    }

}
