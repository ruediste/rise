package laf.core.requestParserChain;

import java.util.Deque;
import java.util.LinkedList;

public class RequestParserChain<T> {

	public final Deque<RequestParser<T>> parsers = new LinkedList<>();

	public RequestParseResult<T> parse(T request) {
		for (RequestParser<T> parser : parsers) {
			RequestParseResult<T> result = parser.parse(request);
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
