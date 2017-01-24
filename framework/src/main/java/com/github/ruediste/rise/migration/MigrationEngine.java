package com.github.ruediste.rise.migration;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ClasspathResourceIndex;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Engine performing all migration tasks
 */
public class MigrationEngine {
    @Inject
    org.slf4j.Logger log;

    @Inject
    ClassHierarchyIndex idx;

    @Inject
    ClasspathResourceIndex resourceIndex;

    @Inject
    ClassLoader cl;

    @Inject
    Injector injector;

    @Inject
    Provider<MigrationTaskSQLFile> fileTaskProvider;

    private List<MigrationTask> tasks = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        tasks.addAll(idx.getAllChildClasses(MigrationTask.class, cl).stream()
                .filter(t -> !Modifier.isAbstract(t.getModifiers()))
                .filter(t -> !t.isAnnotationPresent(MigrationTaskNoAutoDiscover.class))
                .map(t -> injector.getInstance(t)).collect(toList()));
    }

    /**
     * 
     * @param target
     * @param sourceAnnotation
     *            may be null
     * @param scriptGlob
     *            location of the SQL scripts, for example "migrations/*.sql"
     */
    public void addSQLTasks(Class<? extends MigrationTargetSQLFileBase> target, String scriptGlob) {
        tasks.addAll(resourceIndex.getResourcesByGlob(scriptGlob).stream()
                .map(name -> fileTaskProvider.get().initialize(name, target)).collect(toList()));
    }

    public void addTask(MigrationTask task) {
        tasks.add(task);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void performMigration() {

        // sort tasks by id
        tasks = tasks.stream().sorted(Comparator.comparing(x -> x.id)).collect(toList());

        if (log.isDebugEnabled())
            log.debug("Found tasks {}", tasks.stream().map(x -> x.toString()).collect(joining("\n")));

        // collect targets
        Map<Class<? extends MigrationTarget<?>>, MigrationTarget<?>> targets = new HashMap<>();
        for (MigrationTask task : tasks) {
            targets.computeIfAbsent(task.target, cls -> injector.getInstance(cls));
        }

        // filter non-executed
        tasks = tasks.stream().filter(x -> !targets.get(x.target).isAlreadyExecuted(x.id)).collect(toList());

        if (tasks.isEmpty()) {
            log.info("No Migration tasks pending");
            return;
        }

        // execute tasks
        log.info("The following tasks will be executed:\n{}",
                tasks.stream().map(x -> x.toString()).collect(joining("\n")));
        tasks.forEach(t -> {
            log.info("Executing {} ...", t);
            ((MigrationTarget) targets.get(t.target)).execute(t);
            log.info("Execution of {} completed", t);
        });

        log.info("Migration complete");
    }
}
