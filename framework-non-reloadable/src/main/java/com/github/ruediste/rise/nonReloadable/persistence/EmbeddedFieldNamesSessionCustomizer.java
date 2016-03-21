package com.github.ruediste.rise.nonReloadable.persistence;

import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

public class EmbeddedFieldNamesSessionCustomizer implements SessionCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public void customize(Session session) throws Exception {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
        // iterate all descriptors
        for (ClassDescriptor classDescriptor : descriptors.values()) {
            // iterate the mappings of each descriptor
            for (DatabaseMapping databaseMapping : classDescriptor.getMappings()) {
                // process embedded properties
                if (databaseMapping.isAggregateObjectMapping()) {
                    AggregateObjectMapping m = (AggregateObjectMapping) databaseMapping;
                    Map<String, DatabaseField> fieldMapping = m.getAggregateToSourceFields();

                    // iterate the mappings of the embeddable class
                    for (DatabaseMapping refMapping : descriptors.get(m.getReferenceClass()).getMappings()) {
                        if (refMapping.isDirectToFieldMapping()) {
                            DirectToFieldMapping refDirectMapping = (DirectToFieldMapping) refMapping;
                            String refFieldName = refDirectMapping.getField().getName();
                            if (!fieldMapping.containsKey(refFieldName)) {
                                DatabaseField mappedField = refDirectMapping.getField().clone();
                                mappedField.setName(m.getAttributeName() + "_" + mappedField.getName());
                                fieldMapping.put(refFieldName, mappedField);
                            }
                        }

                    }
                }

            }
        }
    }

}
