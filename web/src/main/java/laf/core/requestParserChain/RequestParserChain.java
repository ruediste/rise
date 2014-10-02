package laf.core.requestParserChain;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A chain of {@link RequestParser}s, which can be tried one after the other
 * until the first returns a {@link RequestParseResult}.
 */
public class RequestParserChain<T> {

	public final Deque<RequestParser<T>> parsers = new LinkedList<>();

	public RequestParseResult parse(T request) {
		for (RequestParser<T> parser : parsers) {
			RequestParseResult result = parser.parse(request);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public void add(RequestParser<T> parser) {
		if (parser != null) {
			parsers.add(parser);
		}
	}
}
