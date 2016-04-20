package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.SingularAttribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CSelect;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.generic.DisplayRenderers;
import com.github.ruediste.rise.component.generic.EditComponentWrapper;
import com.github.ruediste.rise.component.generic.EditComponents;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.EMUtil.PersistenceFilterContext;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.util.NOptional;
import com.github.ruediste.rise.util.RiseUtil;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;
import com.google.common.base.Strings;
import com.google.common.primitives.Primitives;

@Singleton
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CrudPropertyFilters {

    @Inject
    Strategies strategies;

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentFactoryUtil componentFactoryUtil;

    @Inject
    CrudReflectionUtil reflectionUtil;

    @Inject
    EditComponents editComponents;

    @Inject
    DisplayRenderers displayRenderers;

    @Inject
    CrudUtil crudUtil;

    @Inject
    ComponentUtil componentUtil;

    @TMessages
    public interface Messages {
        @TMessage("Minimum of {property}")
        PatternString minNumber(LString property);

        @TMessage("Maximum of {property}")
        PatternString maxNumber(LString property);

        TranslatedString min();

        TranslatedString max();
    }

    /**
     * A component defining a filter on a property.
     */
    public interface CrudPropertyFilter {
        Component getComponent();

        void applyFilter(PersistenceFilterContext<?> ctx);
    }

    /**
     * Strategy to create a filter for a property. Use
     * {@link CrudPropertyFilters} to retrieve instances.
     */
    public interface CrudPropertyFilterFactory extends Strategy {
        Optional<CrudPropertyFilter> createFilter(CrudPropertyInfo property);
    }

    public CrudPropertyFilter create(CrudPropertyInfo info) {
        AnnotatedElement element = null;
        Member member = info.getAttribute().getJavaMember();
        if (member instanceof AnnotatedElement) {
            element = (AnnotatedElement) member;
        }

        return strategies.getStrategy(CrudPropertyFilterFactory.class).element(element).get(f -> f.createFilter(info))
                .orElseThrow(() -> new RuntimeException("No filter found for " + info));
    }

    void addFactory(Predicate<CrudPropertyInfo> filter, Function<CrudPropertyInfo, CrudPropertyFilter> factory) {
        strategies.putStrategy(CrudPropertyFilterFactory.class, info -> {
            if (filter.test(info))
                return Optional.of(factory.apply(info));
            return Optional.empty();
        });
    }

    @Inject
    Messages messages;

    /**
     * <img src="doc-files/hello.png" alt=""> t
     */
    @PostConstruct
    void initialize() {
        addStringFactory();
        addNumberFactory(Long.class, Long::parseLong);
        addNumberFactory(Integer.class, Integer::parseInt);
        addNumberFactory(Short.class, Short::parseShort);
        addNumberFactory(Byte.class, Byte::parseByte);
        addBooleanFactory();
        addDurationFactory();
        addZoneIdFactory();
        addLocalTimeFactory();
        addOneToManyFactory();
        addManyToOneFactory();
    }

    @MembersLabeled
    private enum OneToManyChoices {
        @Label("-") NONE, EMPTY, ALL_OF, ANY_OF, NONE_OF;

    }

    private void addOneToManyFactory() {
        // TODO Auto-generated method stub

    }

    @MembersLabeled
    enum ManyToOneChoice {
        NO_FILTER {
            @Override
            void applyFilter(CrudPropertyInfo info, PersistenceFilterContext<?> ctx,
                    LinkedHashSet<Object> selectedEntities) {
                // NOP

            }
        },
        NULL {
            @Override
            void applyFilter(CrudPropertyInfo info, PersistenceFilterContext<?> ctx,
                    LinkedHashSet<Object> selectedEntities) {
                ctx.addWhere(ctx.cb().isNull(ctx.root().get(info.getAttribute().getName())));

            }
        },
        ONE_OF {
            @Override
            void applyFilter(CrudPropertyInfo info, PersistenceFilterContext<?> ctx,
                    LinkedHashSet<Object> selectedEntities) {
                // TODO Auto-generated method stub

            }
        },
        NONE_OF {

            @Override
            void applyFilter(CrudPropertyInfo info, PersistenceFilterContext<?> ctx,
                    LinkedHashSet<Object> selectedEntities) {
                // TODO Auto-generated method stub

            }
        };

        abstract void applyFilter(CrudPropertyInfo info, PersistenceFilterContext<?> ctx,
                LinkedHashSet<Object> selectedEntities);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return componentFactoryUtil.toComponent(renderer);
    }

    private void addManyToOneFactory() {
        addFactory(info -> info.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                info -> {
                    LinkedHashSet<Object> selectedEntities = new LinkedHashSet<>();

                    EditComponentWrapper oneOf = editComponents
                            .type(RiseUtil.collectionTypeToken(info.getAttribute().getJavaType()))
                            .qualifier(info.getEmQualifier()).get();
                    oneOf.setValue(selectedEntities);

                    EditComponentWrapper noneOf = editComponents
                            .type(RiseUtil.collectionTypeToken(info.getAttribute().getJavaType()))
                            .qualifier(info.getEmQualifier()).get();
                    noneOf.setValue(selectedEntities);

                    CSwitch<ManyToOneChoice> cSwitch = new CSwitch<ManyToOneChoice>()
                            .put(ManyToOneChoice.NO_FILTER, new CGroup()).put(ManyToOneChoice.NULL, new CText("null"))
                            .put(ManyToOneChoice.ONE_OF, oneOf.getComponent())
                            .put(ManyToOneChoice.NONE_OF, noneOf.getComponent());

                    CSelect<ManyToOneChoice> cSelect = new CSelect<ManyToOneChoice>()
                            .addItemAndSelect(ManyToOneChoice.NO_FILTER);
                    if (RiseUtil.isNullable(info.getProperty())) {
                        cSelect.addItem(ManyToOneChoice.NULL);
                    }
                    cSelect.addItem(ManyToOneChoice.ONE_OF);
                    cSelect.addItem(ManyToOneChoice.NONE_OF);
                    cSelect.setSelectionHandler(selected -> selected.ifPresent(cSwitch::setOption));

                    return new CrudPropertyFilter() {

                        @Override
                        public Component getComponent() {
                            return toComponent(html -> html.div().bInputGroup().add(cSelect.CLASS("input-group-addon"))
                                    .add(cSwitch)._bInputGroup());
                        }

                        @Override
                        public void applyFilter(PersistenceFilterContext<?> ctx) {
                            // cSwitch.getOption().applyFilter(info, ctx,
                            // selectedEntities);
                        }
                    };
                });
    }

    @Labeled
    void back(Runnable callback) {
        callback.run();
    }

    @Labeled
    void add(Runnable callback) {
        callback.run();
    }

    @Labeled
    void remove(Runnable callback) {
        callback.run();
    }

    @Labeled
    void pick(Runnable callback) {
        callback.run();
    }

    @Labeled
    void clear(Runnable callback) {
        callback.run();
    }

    private void addZonedTimeSpanFactory() {
        // TODO Auto-generated method stub

    }

    private void addStringFactory() {
        addFactory(p -> String.class.equals(p.getAttribute().getJavaType()), p -> {
            PropertyInfo property = p.getProperty();
            CTextField textField = new CTextField().setText("").TEST_NAME(property.getName())
                    .setLabel(labelUtil.property(property).label()).setRenderFormGroup(false);

            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return textField;
                }

                @Override
                public void applyFilter(PersistenceFilterContext ctx) {

                    String text = textField.getText();
                    if (!Strings.isNullOrEmpty(text)) {
                        CriteriaBuilder cb = ctx.cb();
                        Path<String> path = ctx.root().get((SingularAttribute) p.getAttribute());
                        ctx.addWhere(cb.or(path.isNull(), cb.like(path, "%" + text + "%")));
                    }
                }
            };
        });
    }

    private void addLocalTimeFactory() {
        // TODO Auto-generated method stub
    }

    private void addZoneIdFactory() {
        // TODO Auto-generated method stub

    }

    private void addDurationFactory() {
        // TODO Auto-generated method stub
    }

    private Predicate<CrudPropertyInfo> isOfType(Class<?> cls) {
        return p -> cls.equals(Primitives.wrap(p.getAttribute().getJavaType()));
    }

    @MembersLabeled
    enum EnumChoices {
        @Label("-") NONE(NOptional.empty()), NULL(NOptional.of(null)), TRUE(NOptional.of(true)), FALSE(
                NOptional.of(false));
        final NOptional<Boolean> value;

        private EnumChoices(NOptional<Boolean> value) {
            this.value = value;
        }
    }

    private void addBooleanFactory() {
        addFactory(isOfType(Boolean.class), p -> {
            List<EnumChoices> choices = new ArrayList<>();
            choices.add(EnumChoices.NONE);
            if (p.isOptional())
                choices.add(EnumChoices.NULL);
            choices.add(EnumChoices.TRUE);
            choices.add(EnumChoices.FALSE);

            CSelect<EnumChoices> select = new CSelect<EnumChoices>().setItems(choices).selectFirst()
                    .setChildComponentFactory(choice -> new CText(labelUtil.enumMember(choice).label()));
            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return select;
                }

                @Override
                public void applyFilter(PersistenceFilterContext<?> ctx) {
                    select.getSelectedItem().ifPresent(choice -> choice.value
                            .ifPresent(value -> ctx.addWhere(ctx.cb().equal(ctx.root().get(p.getName()), value))));
                }
            };
        });

    }

    private <T extends Comparable> void addNumberFactory(Class<T> boxCls, Function<String, T> parse) {

        addFactory(isOfType(boxCls), p -> {
            PropertyInfo property = p.getProperty();
            LString propertyLabel = labelUtil.property(property).label();
            CInput min = new CInput(InputType.number).setLabel(messages.minNumber(propertyLabel)).setValue("")
                    .setRenderFormGroup(false).TEST_NAME("min");

            CInput max = new CInput(InputType.number).setLabel(messages.maxNumber(propertyLabel)).setValue("")
                    .setRenderFormGroup(false).TEST_NAME("max");

            // @formatter:off
                        Component component = componentFactoryUtil.toComponent((BootstrapRiseCanvas<?> html) ->
                          html
                              .div().BformInline().TEST_NAME(p.getName())
                                .bInputGroup()//.CLASS(x->x.sm(6))
                                  .bInputGroupAddon().content(messages.min())
                                  .add(min)
                                ._bInputGroup()
                                .bInputGroup()//LASS(x->x.sm(6))
                                  .bInputGroupAddon().content(messages.max())
                                  .add(max)
                                ._bInputGroup()
                            ._div()
                         );
                        // @formatter:on

            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return component;
                }

                @Override
                public void applyFilter(PersistenceFilterContext ctx) {
                    if (!Strings.isNullOrEmpty(min.getValue())) {
                        ctx.addWhere(ctx.cb().greaterThanOrEqualTo(ctx.root().get(property.getName()),
                                parse.apply(min.getValue())));

                    }
                    if (!Strings.isNullOrEmpty(max.getValue())) {
                        ctx.addWhere(ctx.cb().lessThanOrEqualTo(ctx.root().get(property.getName()),
                                parse.apply(max.getValue())));

                    }

                }
            };
        });
    }

}
