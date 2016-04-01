package com.github.ruediste.rise.component.generic;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.github.ruediste.rise.component.binding.transformers.Transformers;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CCheckBox;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CDirectRender;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.crud.CrudUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.lambdaPegParser.Var;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

@Singleton
public class DefaultEditComponentFactory implements EditComponentFactory {

    @Inject
    Transformers transformers;

    @Inject
    PersisteUnitRegistry registry;

    @Inject
    ComponentFactoryUtil util;

    @Inject
    CrudUtil crudUtil;

    @Inject
    DisplayRenderers renderers;

    private List<EditComponentFactory> factories = new ArrayList<>();

    @PostConstruct
    void postConstruct() {
        addTransformerFactory(Integer.class, InputType.number, transformers.intToStringTransformer);
        addTransformerFactory(Long.class, InputType.number, transformers.longToStringTransformer);
        addTransformerFactory(Short.class, InputType.number, transformers.shortToStringTransformer);
        addStringTransformerFactory(byte[].class, transformers.byteArrayToHexStringTransformer);
        addStringTransformerFactory(String.class, transformers.identityTransformer());
        addCheckBoxFactory();
        addCollectionFactory();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addCollectionFactory() {

        factories.add((type, testName, info, qualifier) -> {
            if (!Collection.class.isAssignableFrom(type.getRawType())) {
                return Optional.empty();
            }
            TypeToken<?> entityType = type.resolveType(Collection.class.getTypeParameters()[0]);
            ManagedType<?> managedType = registry.getManagedTypeMap(qualifier.orElse(null))
                    .orElseThrow(
                            () -> new RuntimeException("No persistence unit for qualifier " + qualifier + " found"))
                    .get(entityType.getRawType());
            if (managedType == null)
                throw new RuntimeException(
                        entityType.getRawType() + " is not managed by persistence unit " + qualifier);

            IdentificationRenderer renderer = crudUtil.getStrategy(CrudUtil.IdentificationRenderer.class,
                    entityType.getRawType());

            Var<Collection<?>> collection = Var.of(new ArrayList<>());
            Component component = util.toComponent((Renderable<BootstrapRiseCanvas<?>>) html -> html.div()
                    .add(new CDirectRender((Renderable<BootstrapRiseCanvas<?>>) x -> collection.getValue().stream()
                            .forEach(entity -> renderer.renderIdenification(x, entity))))
                    .add(new CButton(this, (btn, x) -> x.chooseItems(() -> {

                        CrudList list = crudUtil.getStrategy(CrudUtil.CrudListFactory.class, entityType.getRawType())
                                .createList(qualifier.orElse(null), entityType.getRawType(), null);
                        list.setItemActionsFactory(obj -> {
                            CSwitch<Boolean> cswitch = new CSwitch<>();
                            cswitch.put(true, new CButton(this, c -> c.remove(() -> {
                                collection.getValue().remove(obj);
                                cswitch.setOption(false);
                            })));
                            cswitch.put(false, new CButton(this, c -> c.add(() -> {
                                ((Collection) collection.getValue()).add(obj);
                                cswitch.setOption(true);
                            })));
                            cswitch.setOption(collection.getValue().contains(obj));
                            return new CDataGrid.Cell(cswitch);
                        });
                        list.setBottomActions(new CButton(this, x1 -> x1.back(() -> CComponentStack.raisePop(btn))));

                        CComponentStack.raisePush(btn, list);
                    })))._div());

            return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<Collection<?>>() {

                @Override
                public Component getComponent() {
                    return component;
                }

                @Override
                public Collection<?> getValue() {
                    return collection.getValue();
                }

                @Override
                public EditComponentWrapper<Collection<?>> setValue(Collection<?> value) {
                    collection.setValue(value);
                    return this;
                }

                @Override
                public EditComponentWrapper<Collection<?>> bindValue(Supplier<Collection<?>> accessor) {
                    BindingUtil.bindModelProperty(component, accessor, x -> {
                        collection.getValue().clear();
                        ((Collection) collection.getValue()).addAll(accessor.get());
                    }, x -> {
                        x.clear();
                        ((Collection) x).addAll(collection.getValue());
                    });
                    return this;
                }

            });
        });
    }

    private void addCheckBoxFactory() {

        factories.add((type, testName, info, qualifier) -> {
            if (Boolean.class.equals(Primitives.wrap(type.getRawType()))) {
                CCheckBox checkBox = new CCheckBox();
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<Boolean>() {

                    @Override
                    public Component getComponent() {
                        return checkBox;
                    }

                    @Override
                    public Boolean getValue() {
                        return checkBox.isChecked();
                    }

                    @Override
                    public EditComponentWrapper<Boolean> setValue(Boolean value) {
                        checkBox.setChecked(value);
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<Boolean> bindValue(Supplier<Boolean> accessor) {
                        checkBox.bindLabelProperty(c -> c.setChecked(accessor.get()));
                        return this;
                    }

                });
            } else
                return Optional.empty();
        });
    }

    private <T> void addTransformerFactory(Class<T> propertyType, InputType inputType,
            TwoWayBindingTransformer<T, String> transformer) {
        factories.add((type, testName, info, qualifier) -> {
            if (propertyType.equals(Primitives.wrap(type.getRawType()))) {
                CInput component = new CInput(inputType);
                testName.ifPresent(x -> component.TEST_NAME(x));
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<T>() {

                    @Override
                    public Component getComponent() {
                        return component;
                    }

                    @Override
                    public T getValue() {
                        return transformer.transformInv(component.getValue());
                    }

                    @Override
                    public EditComponentWrapper<T> setValue(T value) {
                        component.setValue(transformer.transform(value));
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<T> bindValue(Supplier<T> accessor) {
                        component.bindValue(() -> transformer.transform(accessor.get()));
                        return this;
                    }
                });
            }
            return Optional.empty();
        });

    }

    private <T> void addStringTransformerFactory(Class<T> propertyType,
            TwoWayBindingTransformer<T, String> transformer) {
        factories.add((type, testName, info, qualifier) -> {
            if (propertyType.equals(Primitives.wrap(type.getRawType()))) {
                CTextField component = new CTextField();
                testName.ifPresent(x -> component.TEST_NAME(x));
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<T>() {

                    @Override
                    public Component getComponent() {
                        return component;
                    }

                    @Override
                    public T getValue() {
                        return transformer.transformInv(component.getText());
                    }

                    @Override
                    public EditComponentWrapper<T> setValue(T value) {
                        component.setText(transformer.transform(value));
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<T> bindValue(Supplier<T> accessor) {
                        component.bindText(() -> transformer.transform(accessor.get()));
                        return this;
                    }
                });
            }
            return Optional.empty();
        });

    }

    @Override
    public Optional<EditComponentWrapper<?>> getComponent(TypeToken<?> type, Optional<String> name,
            Optional<PropertyInfo> info, Optional<Class<? extends Annotation>> qualifier) {
        return factories.stream().map(f -> f.getComponent(type, name, info, qualifier)).filter(Optional::isPresent)
                .<EditComponentWrapper<?>> map(x -> x.get()).findFirst();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.plus)
    void add(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.minus)
    void remove(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    void chooseItems(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    void back(Runnable callback) {
        callback.run();
    }
}
