package com.github.ruediste.rise.core.front.reload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import com.github.ruediste.rise.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.base.Objects;

/**
 * Information cache storing information about the class space classes are in
 */
@Singleton
public class ClassSpaceCache {
	@Inject
	ClassChangeNotifier notifier;

	@PostConstruct
	public void setup() {
		notifier.addListener(this::onChange);
	}

	Map<String, Class<?>> classMap = new HashMap<>();

	void onChange(ClassChangeTransaction trx) {
		for (String name : trx.removedClasses) {
			classMap.remove(name);
			outerClassNames.remove(name);
		}

		updateMap(trx.addedClasses);
		updateMap(trx.modifiedClasses);
	}

	private void updateMap(Set<ClassNode> classes) {
		for (ClassNode cls : classes) {
			updateMap(cls);
		}
	}

	private Map<String, String> outerClassNames = new HashMap<>();

	void updateMap(ClassNode cls) {
		// System.out.println(cls.name + "->" + cls.outerClass);
		if (cls.outerClass != null) {
			outerClassNames.put(cls.name, cls.outerClass);
		}
		if (cls.innerClasses != null) {
			for (Object obj : cls.innerClasses) {
				InnerClassNode node = (InnerClassNode) obj;
				if (node.outerName != null) {
					outerClassNames.put(node.name, node.outerName);
				} else {
					if (!Objects.equal(cls.name, node.name))
						outerClassNames.put(node.name, cls.name);
				}
			}
		}
		if (cls.visibleAnnotations == null) {
			return;
		}
		for (Object o : cls.visibleAnnotations) {
			AnnotationNode a = (AnnotationNode) o;
			if (Type.getDescriptor(DynamicSpace.class).equals(a.desc)) {
				classMap.put(cls.name, DynamicSpace.class);
			} else if (Type.getDescriptor(PermanentSpace.class).equals(a.desc)) {
				classMap.put(cls.name, PermanentSpace.class);
			}
		}
	}

	public Class<?> getClassSpace(String className) {
		String internalName = className.replace('.', '/');
		return getClassSpaceInternal(internalName);
	}

	private Class<?> getClassSpaceInternal(String internalName) {
		{
			Class<?> result = classMap.get(internalName);
			if (result != null) {
				return result;
			}
		}
		{
			String outer = outerClassNames.get(internalName);
			if (outer != null) {
				return getClassSpaceInternal(outer);
			}
		}

		String[] parts = internalName.split("/");
		return getPackageSpace(parts, parts.length - 1);
	}

	private Class<?> getPackageSpace(String[] parts, int length) {
		if (length == 0) {
			return PermanentSpace.class;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				sb.append("/");
			}
			sb.append(parts[i]);
		}
		sb.append("/package-info");
		Class<?> result = classMap.get(sb.toString());
		if (result != null) {
			return result;
		}
		return getPackageSpace(parts, length - 1);
	}
}
