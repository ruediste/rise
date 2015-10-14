package com.github.ruediste.rise.util;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class RiseUtil {

    private RiseUtil() {
    }

    public static String readFromClasspathAsString(String fullPath,
            ClassLoader classLoader) {
        return new String(readFromClasspath(fullPath, classLoader),
                Charsets.UTF_8);
    }

    public static byte[] readFromClasspath(String fullPath,
            ClassLoader classLoader) {
        InputStream in = classLoader.getResourceAsStream(fullPath);
        if (in == null) {
            return null;
        }
        try {
            byte[] bb = ByteStreams.toByteArray(in);
            return bb;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        "unable to close stream used to load resource from classpath",
                        e);
            }
        }
    }

    /**
     * Return the shortest path possible which resolves to targetPath against
     * basePath. Both basePath and targetPath need to be absolute.
     */
    public static String getShortestPath(String basePath, String targetPath) {
        String rel = relativizePath(basePath, targetPath);
        if (rel.length() < targetPath.length())
            return rel;
        else
            return targetPath;
    }

    public static String relativizePath(String basePath, String targetPath) {
        String[] baseParts = basePath.split("/", -1);
        String[] targetParts = targetPath.split("/", -1);
        int idx = 0;

        // skip common prefix
        while (idx < baseParts.length - 1 && idx < targetParts.length
                && baseParts[idx].equals(targetParts[idx])) {
            idx++;
        }

        // add ../s
        StringBuilder sb = new StringBuilder();
        for (int i = idx; i < baseParts.length - 1; i++) {
            sb.append("../");
        }

        // add remaining target path
        for (int i = idx; i < targetParts.length; i++) {
            if (i > idx)
                sb.append("/");
            sb.append(targetParts[i]);
        }
        return sb.toString();
    }

    public static String resolvePath(String basePath, String path) {
        if (path.startsWith("/"))
            return path;

        // relative path

        // combine parts
        String combined;
        if (basePath.endsWith("/"))
            combined = basePath + path;
        else
            // include the file name, but add a .. as well, such that it will be
            // removed in the next step
            combined = basePath + "/../" + path;

        // remove relative parts
        String[] parts = combined.split("/", -1);
        String[] newParts = new String[parts.length];
        int toDrop = 0;
        int idx = 0;
        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i];
            if ("..".equals(part)) {
                toDrop++;
            } else if (".".equals(part)) {
                // NOP
            } else if (toDrop > 0) {
                toDrop--;
            } else {
                newParts[idx++] = part;
            }
        }

        // create result
        StringBuilder sb = new StringBuilder();
        for (int i = idx - 1; i >= 0; i--) {
            sb.append(newParts[i]);
            if (i > 0)
                sb.append("/");
        }

        return sb.toString();
    }

}
