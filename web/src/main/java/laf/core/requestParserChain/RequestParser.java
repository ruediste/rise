package laf.core.requestParserChain;

public interface RequestParser<T> {

	RequestParseResult<T> parse(T request);
}
