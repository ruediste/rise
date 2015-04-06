package com.github.ruediste.laf.core.classReload;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.sf.cglib.asm.Type;

import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.classReload.ClassChangeNotifier.ClassChangeTransaction;
import com.github.ruediste.laf.core.web.resource.StaticWebResourceBundle;

/**
 * Cache containing a list of {@link StaticWebResourceBundle} classes.
 */
@Singleton
public class ResourceBundleCache {

	@Inject
	ClassChangeNotifier notifier;

	private Set<String> bundles = new HashSet<>();

	@PostConstruct
	public void setup() {
		notifier.addListener(this::changeOccurred);
	}

	private void changeOccurred(ClassChangeTransaction trx) {
		for (String name : trx.removedClasses) {
			bundles.remove(name);
		}
		String superName = Type.getInternalName(StaticWebResourceBundle.class);

		for (ClassNode cls : trx.addedClasses) {
			if (!superName.equals(cls.superName)) {
				continue;
			}
			bundles.add(cls.name);
		}
	}

	public List<Class<?>> getBundleClasses() {
		return getBundleClasses(Thread.currentThread().getContextClassLoader());
	}

	public List<Class<?>> getBundleClasses(ClassLoader cl) {
		ArrayList<Class<?>> result = new ArrayList<>();
		for (String name : bundles) {
			String binaryName = name.replace('/', '.');
			try {
				result.add(cl.loadClass(binaryName));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Error wile loading class "
						+ binaryName, e);
			}
		}
		return result;
	}
}
