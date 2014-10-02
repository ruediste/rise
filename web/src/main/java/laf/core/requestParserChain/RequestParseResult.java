package laf.core.requestParserChain;

/**
 * Result of a {@link RequestParser}
 */
public interface RequestParseResult {

	/**
	 * Handle the parsed request
	 */
	void handle();
}
