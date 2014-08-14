package laf.core.requestParserChain;

public interface RequestParseResult<T> {

	void handle(T request);
}
