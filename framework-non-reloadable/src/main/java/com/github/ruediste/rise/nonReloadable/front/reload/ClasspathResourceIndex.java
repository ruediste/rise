package com.github.ruediste.rise.nonReloadable.front.reload;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.front.reload.ResourceChangeNotifier.ResourceChangeTransaction;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.RiseUtil;

@Singleton
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
        return getResourcesByGlob(RiseUtil.toPrefixAndRegex(glob));
    }

    public Set<String> getResourcesByGlob(Pair<String, String> prefixAndRegex) {
        HashSet<String> result = new HashSet<>();
        Pattern regex = Pattern.compile(prefixAndRegex.getB() + "$");
        String prefix = prefixAndRegex.getA();
        for (String resource : resources.tailSet(prefix)) {
            if (!resource.startsWith(prefix))
                break;
            if (regex.matcher(resource.substring(prefix.length())).matches())
                result.add(resource);
        }
        return result;
    }

}
