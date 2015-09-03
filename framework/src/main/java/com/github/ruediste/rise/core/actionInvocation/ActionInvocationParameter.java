package com.github.ruediste.rise.core.actionInvocation;

/**
 * Represent string values attached to {@link ActionInvocation}s. These values
 * are transferred along with the ActionPath to the client and back again. The
 * parameters are identified by their name
 */
public class ActionInvocationParameter {

    private final String name;

    public ActionInvocationParameter(String name) {
        this.name = name;

    }

    public void setMultiple(ActionInvocation<?> path, String[] values) {
        path.getParameters().put(this.getName(), values);
    }

    public void set(ActionInvocation<?> path, String value) {
        path.getParameters().put(this.getName(), new String[] { value });
    }

    public String[] getMultiple(ActionInvocation<?> path) {
        return path.parameters.get(this.getName());
    }

    public String get(ActionInvocation<?> path) {
        String[] result = path.parameters.get(this.getName());
        if (result == null || result.length == 0)
            return null;
        return result[0];
    }

    public String getName() {
        return name;
    }
}
