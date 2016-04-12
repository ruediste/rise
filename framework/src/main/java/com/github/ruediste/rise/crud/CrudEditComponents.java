package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CSelect;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CValue;
import com.github.ruediste.rise.component.generic.EditComponents;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.CrudPicker;
import com.github.ruediste.rise.crud.CrudUtil.CrudPickerFactory;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

/**
 * Please not that components can operate directly on the entity instead of
 * waiting for the binding to be triggered.
 */
@Singleton
public class CrudEditComponents {
    @Inject
    ComponentFactoryUtil util;

    @Inject
    CrudUtil crudUtil;

    @Inject
    LabelUtil labelUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    @Inject
    ComponentUtil componentUtil;

    @Inject
    Strategies strategies;

    @Inject
    EditComponents editComponents;

    public interface CrudEditComponentFactory extends Strategy {
        Optional<Component> create(CrudPropertyHandle handle);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    abstract class Targets {
        @GlyphiconIcon(Glyphicon.open)
        @Labeled
        abstract void pick();
    }

    public Component createEditComponent(CrudPropertyHandle handle) {
        AnnotatedElement element = null;
        Member member = handle.info().getAttribute().getJavaMember();
        if (member instanceof AnnotatedElement) {
            element = (AnnotatedElement) member;
        }

        return strategies.getStrategy(CrudEditComponentFactory.class).element(element).get(f -> f.create(handle))
                .orElseThrow(() -> new RuntimeException("No Edit component found for " + handle.info()));
    }

    void addFactory(Predicate<CrudPropertyInfo> filter, Function<CrudPropertyHandle, Component> factory) {
        strategies.putStrategy(CrudEditComponentFactory.class, handle -> {
            if (filter.test(handle.info()))
                return Optional.of(factory.apply(handle));
            return Optional.empty();
        });
    }

    @PostConstruct
    public void initialize() {

        addManyToOneFactory();
        addOneToManyFactory();

        addEnumElementCollectionFactory();

        addEmbeddedFactory();
        addEditComponentFactory();

    }

    private void addEditComponentFactory() {
        strategies.putStrategy(CrudEditComponentFactory.class, new CrudEditComponentFactory() {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Optional<Component> create(CrudPropertyHandle handle) {
                return editComponents.property(handle.info().getProperty()).tryGet().map(wrapper -> {
                    Component component = wrapper.getComponent();
                    wrapper.bindValue((Supplier) () -> handle.getValue());
                    return component;
                });
            }
        });
    }

    private Component toComponentBound(Supplier<?> bindingAccessor, Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponentBound(bindingAccessor, renderer);
    }

    @Inject
    CrudReflectionUtil crudReflectionUtil;

    private void addEmbeddedFactory() {
        addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.EMBEDDED,
                handle -> toComponentBound(() -> handle.proxy(), html -> {

                    Object value = handle.getValue();
                    if (value == null) {
                        Class<?> rawType = handle.info().getProperty().getPropertyType().getRawType();
                        try {
                            value = rawType.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Error while instantiating " + rawType + " for embeddable "
                                    + handle.info().getProperty() + " which was null");
                        }
                        handle.setValue(value);
                    }

                    html.bCol(x -> x.xs(11).xsOffset(1));
                    for (CrudPropertyInfo property : crudReflectionUtil.getDisplayProperties(
                            crudReflectionUtil.getPersistentType(handle.info().getEmQualifier(), value.getClass()))) {
                        CrudPropertyHandle subHandle = CrudPropertyHandle.create(property, handle::rootEntity,
                                () -> handle.getValue(), handle.group());
                        html.bFormGroup().label().content(labelUtil.property(property.getProperty()).label())
                                .add(createEditComponent(subHandle))._bFormGroup();
                    }
                    html._bCol();
                }));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Collection<Object> castToObjectCollection(Object input) {
        return (Collection) input;
    }

    public void addEnumElementCollectionFactory() {
        addFactory(p -> {
            if (p.getAttribute().getPersistentAttributeType() != PersistentAttributeType.ELEMENT_COLLECTION)
                return false;
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) p.getAttribute();
            return pluralAttribute.getElementType().getJavaType().isEnum();
        } , (decl) -> {
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) decl.info().getAttribute();
            Class<?> enumType = pluralAttribute.getElementType().getJavaType();
            return toComponent(new Renderable<BootstrapRiseCanvas<?>>() {

                @Override
                public void renderOn(BootstrapRiseCanvas<?> html) {
                    CDataGrid<Object> grid = new CDataGrid<Object>();
                    grid.addColumn(() -> new CDataGrid.Cell(r -> "Name"),
                            o -> new CDataGrid.Cell(r -> String.valueOf(o)))

                    .addColumn(() -> new CDataGrid.Cell(r -> ""),
                            o -> new CDataGrid.Cell(new CButton(CrudEditComponents.this,
                                    x -> x.remove(() -> grid.updateItems(i -> i.remove(o))))))

                    .bind(() -> decl.proxy(),
                            (g, obj) -> g.setItems(new ArrayList<Object>(castToObjectCollection(decl.getValue()))),
                            (g, obj) -> {
                        castToObjectCollection(decl.getValue()).clear();
                        castToObjectCollection(decl.getValue()).addAll(g.getItems());

                    });
                    CSelect<Object> select = new CSelect<>().setItems(Arrays.asList(enumType.getEnumConstants()))
                            .setAllowEmpty(true);
                    html.add(grid).add(select).add(new CButton(CrudEditComponents.this,
                            x -> x.add(() -> select.getSelectedItem().map(o -> grid.updateItems(i1 -> i1.add(o))))));
                }
            });
        });
    }

    public void addManyToOneFactory() {
        // Many to One
        addFactory(decl -> decl.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (decl) -> {
                    Class<?> cls = decl.info().getAttribute().getJavaType();

                    CValue<Object> cValue = new CValue<>(v -> toComponent(html -> crudUtil
                            .getStrategy(IdentificationRenderer.class, cls).renderIdenification(html, v)))
                                    .bindValue(() -> decl.getValue());

                    // @formatter:off
					return toComponent(html -> html.bInputGroup().span().BformControl().DISABLED()
							.TEST_NAME(decl.info().getAttribute().getName()).add(cValue)._span().bInputGroupBtn()
							.add(new CButton(this, (btn, c) -> c.pick(() -> {
								CrudPicker picker = crudUtil.getStrategy(CrudPickerFactory.class, cls)
										.createPicker(decl.info().getEmQualifier(), cls);
								picker.pickerClosed().addListener(value -> {
									if (value != null) {
										cValue.setValue(value);
									}
									ComponentTreeUtil.raiseEvent(btn, new CComponentStack.PopComponentEvent());
								});
								ComponentTreeUtil.raiseEvent(btn,
										new CComponentStack.PushComponentEvent(new CController(picker)));
							}))).add(new CButton(this, c -> c.clear(() -> cValue.setValue(null))))._bInputGroupBtn()
							._bInputGroup());
					// @formatter:on);
                });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addOneToManyFactory() {
        // @formatter:off
		addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
				(decl) -> toComponent(html -> html.div().add(new CButton(this, (btn, x) -> x.chooseItems(() -> {
					PluralAttribute attr = (PluralAttribute) decl.info().getAttribute();
					OneToMany oneToMany = ((AnnotatedElement) attr.getJavaMember()).getAnnotation(OneToMany.class);
					Collection collection = (Collection) decl.getValue();

					CrudList list = crudUtil
							.getStrategy(CrudUtil.CrudListFactory.class, attr.getElementType().getJavaType())
							.createList(decl.info().getEmQualifier(), attr.getElementType().getJavaType(), null);
					list.setItemActionsFactory(obj -> {
						CSwitch<Boolean> result = new CSwitch<>();
						if ("".equals(oneToMany.mappedBy())) {
							// this is the owning side
							result.put(true, new CButton(this, c -> c.remove(() -> {
								collection.remove(obj);
								result.setOption(false);
							})));
							result.put(false, new CButton(this, c -> c.add(() -> {
								collection.add(obj);
								result.setOption(true);
							})));
						} else {
							PropertyInfo owningProperty = PropertyUtil
									.getPropertyInfo(attr.getElementType().getJavaType(), oneToMany.mappedBy());
							result.put(true, new CButton(this, c -> c.remove(() -> {
								collection.remove(obj);
								owningProperty.setValue(obj, null);
								result.setOption(false);
							})));
							result.put(false, new CButton(this, c -> c.add(() -> {
								collection.add(obj);
								owningProperty.setValue(obj, decl.rootEntity());
								result.setOption(true);
							})));
						}
						result.setOption(collection.contains(obj));
						return new CDataGrid.Cell(result);
					});
					list.setBottomActions(new CButton(this, x1 -> x1
							.back(() -> CComponentStack.raisePop(btn))));

					CComponentStack.raisePush(btn, list);
				})))._div()));
		// @formatter:on
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    void chooseItems(Runnable callback) {
        callback.run();
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
    @GlyphiconIcon(Glyphicon.hand_right)
    void pick(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.remove)
    void clear(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    void back(Runnable callback) {
        callback.run();
    }
}
