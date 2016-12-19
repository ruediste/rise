package com.github.ruediste.rise.nonReloadable.front.reload;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.ResourceChangeNotifier.ResourceChangeTransaction;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.RiseUtil;

@Singleton
@NonRestartable
public class ClasspathResourceIndex {

    @Inject
    ResourceChangeNotifier notifier;

    TreeSet<String> resources = new TreeSet<>();

    @PostConstruct
    public void postConstruct() {
        notifier.addPreListener(this::onChange);
    }

    private void onChange(ResourceChangeTransaction trx) {
        resources.removeAll(trx.removedResources);
        resources.addAll(trx.addedResources.keySet());
    }

    public Set<String> getResourcesByGlob(String glob) {
        Pair<String, String> prefixAndRegex = RiseUtil.toPrefixAndRegex(glob);
        return getResources(prefixAndRegex.getA(), prefixAndRegex.getB());
    }

    public Set<String> getResources(String prefix, String regex) {
        HashSet<String> result = new HashSet<>();
        Pattern regexPattern = Pattern.compile(regex + "$");
        for (String resource : resources.tailSet(prefix)) {
            if (!resource.startsWith(prefix))
                break;
            if (regexPattern.matcher(resource.substring(prefix.length())).matches())
                result.add(resource);
        }
        return result;
    }

}
