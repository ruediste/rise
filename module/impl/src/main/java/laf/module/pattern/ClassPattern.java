package laf.module.pattern;

import java.util.regex.Pattern;

public class ClassPattern {

	private final String originalPattern;
	private final long score;
	private final Pattern pattern;

	public ClassPattern(String pattern) {
		originalPattern = pattern;
		score = calculateScore(pattern);
		this.pattern = createRegexpPattern(pattern);
	}

	private Pattern createRegexpPattern(String pattern2) {
		return Pattern.compile("foo");
	}

	private long calculateScore(String pattern) {
		String[] parts = pattern.split("\\.");
		long result = 0;
		long factor = 2;
		for (String part : parts) {
			int baseScore;
			if ("**".equals(part)) {
				baseScore = 4;
				factor = 1;
			} else if ("*".equals(part)) {
				baseScore = 8;
				factor = 1;
			} else if (part.contains("**")) {
				baseScore = 16;
				factor = 1;
			} else if (part.contains("*")) {
				baseScore = 32;
				factor = 1;
			} else {
				baseScore = 64;
			}

			result += baseScore * factor;
		}
		if (pattern.endsWith("!")) {
			result += 1000;
		}
		return result;
	}

	public String getOriginalPattern() {
		return originalPattern;
	}

	public long getScore() {
		return score;
	}

	public boolean matches(String qualifiedClassName) {
		return false;
	}
}
