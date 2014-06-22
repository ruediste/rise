package laf.component.transaction;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TestEntity.class)
public abstract class TestEntity_ {

	public static volatile SingularAttribute<TestEntity, Long> id;
	public static volatile SingularAttribute<TestEntity, String> value;
	public static volatile SingularAttribute<TestEntity, TestEntity> parent;
	public static volatile SetAttribute<TestEntity, TestEntity> children;

}

