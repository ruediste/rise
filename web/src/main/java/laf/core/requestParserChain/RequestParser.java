package laf.core.requestParserChain;

public interface RequestParser<T> {

	RequestParseResult parse(T request);
}
