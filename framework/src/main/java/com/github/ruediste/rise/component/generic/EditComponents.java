package com.github.ruediste.rise.component.generic;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.github.ruediste.rise.component.binding.transformers.Transformers;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CCheckBox;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CDirectRender;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CInputGroup;
import com.github.ruediste.rise.component.components.CInputGroupAddon;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.generic.EditComponentFactory.EditComponentSpecification;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.crud.CrudUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.RiseUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.lambdaPegParser.Var;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

@Singleton
public class EditComponents {

    @Inject
    Strategies strategies;

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

    public class PropertyApi<T> {

        private PropertyInfo property;
        private Optional<Class<? extends Annotation>> qualifier = Optional.empty();
        private Optional<Boolean> nullable = Optional.empty();

        private PropertyApi(PropertyInfo property) {
            this.property = property;
        }

        public PropertyApi<T> qualifier(Class<? extends Annotation> qualifier) {
            this.qualifier = Optional.of(qualifier);
            return this;
        }

        public PropertyApi<T> nullable(boolean nullable) {
            this.nullable = Optional.of(nullable);
            return this;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No edit component found for " + property));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            return strategies.getStrategy(EditComponentFactory.class).element(property).cached(property)
                    .get(f -> (Optional) f.getComponent(new EditComponentSpecification(property.getPropertyType(),
                            nullable.orElseGet(() -> RiseUtil.isNullable(property)), Optional.of(property.getName()),
                            Optional.of(property), qualifier)));
        }
    }

    public PropertyApi<?> property(PropertyInfo info) {
        return new PropertyApi<>(info);
    }

    public <T, P> PropertyApi<P> property(Class<T> startClass, Function<T, P> propertyAccessor) {
        return new PropertyApi<>(
                PropertyUtil.getPropertyPath(startClass, x -> propertyAccessor.apply(x)).getAccessedProperty());
    }

    @SuppressWarnings("unchecked")
    public <T, P> PropertyApi<P> property(T start, Function<T, P> propertyAccessor) {
        return new PropertyApi<>(PropertyUtil.getPropertyPath(start.getClass(), x -> propertyAccessor.apply((T) x))
                .getAccessedProperty());
    }

    public class InstanceApi<T> {

        private Object start;
        private PropertyPath propertyPath;

        public InstanceApi(Object start, PropertyPath propertyPath) {
            this.start = start;
            this.propertyPath = propertyPath;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(
                    () -> new RuntimeException("No edit component found for " + propertyPath.getAccessedProperty()));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            Optional<EditComponentWrapper<T>> editComponent = (Optional) property(propertyPath.getAccessedProperty())
                    .tryGet();
            editComponent.ifPresent(c -> c.bindValue(() -> (T) propertyPath.evaluate(start)));
            return editComponent;
        }
    }

    /**
     * created edit components will be bound to the accessed property
     */
    @SuppressWarnings("unchecked")
    public <T, P> InstanceApi<P> instance(T start, Function<T, P> propertyAccessor) {
        PropertyPath propertyPath = PropertyUtil.getPropertyPath((Class<T>) start.getClass(),
                (T t) -> propertyAccessor.apply(t));
        return new InstanceApi<P>(start, propertyPath);
    }

    public class TypeApi<T> {

        private TypeToken<T> type;
        private Optional<String> testName = Optional.empty();
        private Optional<Class<? extends Annotation>> qualifier = Optional.empty();
        private boolean nullable = true;

        private TypeApi(TypeToken<T> cls) {
            this.type = cls;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No edit component found for type " + type));
        }

        public TypeApi<T> testName(String testName) {
            this.testName = Optional.of(testName);
            return this;
        }

        public TypeApi<T> qualifier(Class<? extends Annotation> qualifier) {
            this.qualifier = Optional.of(qualifier);
            return this;
        }

        public TypeApi<T> nullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            return (Optional) strategies.getStrategy(EditComponentFactory.class).cached(type).get(f -> f.getComponent(
                    new EditComponentSpecification(type, nullable, testName, Optional.empty(), qualifier)));
        }
    }

    public <T> TypeApi<T> type(Class<T> cls) {
        return new TypeApi<>(TypeToken.of(cls));
    }

    public <T> TypeApi<T> type(TypeToken<T> type) {
        return new TypeApi<>(type);
    }

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

        strategies.putStrategy(EditComponentFactory.class, spec -> {
            if (!Collection.class.isAssignableFrom(spec.type.getRawType())) {
                return Optional.empty();
            }
            TypeToken<?> entityType = spec.type.resolveType(Collection.class.getTypeParameters()[0]);
            ManagedType<?> managedType = registry.getManagedTypeMap(spec.qualifier.orElse(null))
                    .orElseThrow(() -> new RuntimeException(
                            "No persistence unit for qualifier " + spec.qualifier + " found"))
                    .get(entityType.getRawType());
            if (managedType == null)
                throw new RuntimeException(
                        entityType.getRawType() + " is not managed by persistence unit " + spec.qualifier);

            IdentificationRenderer renderer = crudUtil.getStrategy(CrudUtil.IdentificationRenderer.class,
                    entityType.getRawType());

            Var<Collection<?>> collection = Var.of(new ArrayList<>());
            Component component = util.toComponent((Renderable<BootstrapRiseCanvas<?>>) html -> html.div()
                    .add(new CDirectRender((Renderable<BootstrapRiseCanvas<?>>) x2 -> collection.getValue().stream()
                            .forEach(entity -> renderer.renderIdenification(x2, entity))))
                    .add(new CButton(EditComponents.this, (btn, x3) -> x3.chooseItems(() -> {

                CrudList list = crudUtil.getStrategy(CrudUtil.CrudListFactory.class, entityType.getRawType())
                        .createList(spec.qualifier.orElse(null), entityType.getRawType(), null);
                list.setItemActionsFactory(obj -> {
                    CSwitch<Boolean> cswitch = new CSwitch<>();
                    cswitch.put(true, new CButton(EditComponents.this, c1 -> c1.remove(() -> {
                        collection.getValue().remove(obj);
                        cswitch.setOption(false);
                    })));
                    cswitch.put(false, new CButton(EditComponents.this, c2 -> c2.add(() -> {
                        ((Collection) collection.getValue()).add(obj);
                        cswitch.setOption(true);
                    })));
                    cswitch.setOption(collection.getValue().contains(obj));
                    return new CDataGrid.Cell(cswitch);
                });
                list.setBottomActions(
                        new CButton(EditComponents.this, x1 -> x1.back(() -> CComponentStack.raisePop(btn))));

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
                    } , x -> {
                        x.clear();
                        ((Collection) x).addAll(collection.getValue());
                    });
                    return this;
                }

            });
        });
    }

    private void addCheckBoxFactory() {

        strategies.putStrategy(EditComponentFactory.class, spec -> {
            if (Boolean.class.equals(Primitives.wrap(spec.type.getRawType()))) {
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
        strategies.putStrategy(EditComponentFactory.class, new EditComponentFactory() {
            @Override
            public Optional<EditComponentWrapper<?>> getComponent(EditComponentSpecification spec) {
                return getComponent(spec.type, spec.testName, spec.info, spec.qualifier);
            }

            public Optional<EditComponentWrapper<?>> getComponent(TypeToken<?> type, Optional<String> testName,
                    Optional<PropertyInfo> info, Optional<Class<? extends Annotation>> qualifier) {
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
            }
        });

    }

    private <T> void addStringTransformerFactory(Class<T> propertyType,
            TwoWayBindingTransformer<T, String> transformer) {
        strategies.putStrategy(EditComponentFactory.class, new EditComponentFactory() {
            @Override
            public Optional<EditComponentWrapper<?>> getComponent(EditComponentSpecification spec) {
                if (propertyType.equals(Primitives.wrap(spec.type.getRawType()))) {
                    Component component;
                    CTextField textField = new CTextField();
                    CCheckBox nullCheckBox = new CCheckBox();
                    spec.testName.ifPresent(x -> textField.TEST_NAME(x));
                    if (spec.nullable) {
                        nullCheckBox.setToggledHandler(checked -> {
                            if (checked) {
                                textField.setText("");
                            }
                        }).setChecked(true);
                        component = new CInputGroup().add(new CInputGroupAddon().add(nullCheckBox, new CText("null")))
                                .add(textField);
                    } else {
                        component = textField;
                    }
                    return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<T>() {

                        @Override
                        public Component getComponent() {
                            return component;
                        }

                        @Override
                        public T getValue() {
                            if (spec.nullable)
                                return nullCheckBox.isChecked() ? null : transformer.transformInv(textField.getText());
                            else
                                return transformer.transformInv(textField.getText());
                        }

                        @Override
                        public EditComponentWrapper<T> setValue(T value) {
                            if (spec.nullable) {
                                if (transformer.transform(value) == null) {
                                    textField.setText("");
                                    nullCheckBox.setChecked(true);
                                } else {
                                    textField.setText(transformer.transform(value));
                                    nullCheckBox.setChecked(false);
                                }
                            } else
                                textField.setText(transformer.transform(value));
                            return this;
                        }

                        @Override
                        public EditComponentWrapper<T> bindValue(Supplier<T> accessor) {
                            Pair<BindingGroup<?>, Binding<?>> pair = BindingUtil.bindModelProperty(component, accessor,
                                    x -> setValue(x), () -> getValue());
                            textField.setLabelProperty(pair.getB());
                            return this;
                        }
                    });
                }
                return Optional.empty();
            }
        });

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
