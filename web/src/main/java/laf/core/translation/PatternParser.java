package laf.core.translation;

import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.function.Function;

import laf.core.translation.parse.*;

public class PatternParser extends Parser {

	public interface Node {
		String format(FormatContext ctx);

		Set<String> argumentNames();
	}

	public static final class SequenceNode implements Node {
		private Collection<Node> nodes;

		public SequenceNode(Collection<Node> nodes) {
			this.nodes = nodes;
		}

		@Override
		public String format(FormatContext ctx) {
			StringBuilder sb = new StringBuilder();
			nodes.stream().map(n -> n.format(ctx)).forEach(s -> sb.append(s));
			return sb.toString();
		}

		@Override
		public Set<String> argumentNames() {
			return nodes.stream().flatMap(n -> n.argumentNames().stream())
					.collect(toSet());
		}
	}

	public static class LiteralNode implements Node {

		private String literal;

		public LiteralNode(String literal) {
			this.literal = literal;
		}

		@Override
		public String format(FormatContext ctx) {
			return literal;
		}

		@Override
		public Set<String> argumentNames() {
			return Collections.emptySet();
		}

	}

	public static abstract class ArgumentNode implements Node {

		private String argumentName;

		public ArgumentNode(String argumentName) {
			this.argumentName = argumentName;
		}

		@Override
		public Set<String> argumentNames() {
			return Collections.singleton(argumentName);
		}

	}

	public static class FormatContext {
		public Locale locale;
		public Map<String, Object> arguments;
	}

	public interface FormatHandler {
		Node parse(String argumentName, ParsingContext ctx, PatternParser parser);
	}

	Map<String, FormatHandler> formatHandlers = new HashMap<>();

	public PatternParser(ParsingContext ctx) {
		super(ctx);
	}

	//@formatter:off
	public Node pattern() {
		Node result = this.<Node,Node>ZeroOrMore(() ->
			FirstOf(
				() -> placeHolder(),
				() -> new LiteralNode(OneOrMore(()->Char(cp -> cp != '{'), joinStrings))),
			SequenceNode::new);
		EOI();
		return result;
	}

	public static final Function<Collection<String>, String> joinStrings = strs -> strs
			.stream().collect(joining());

	public Node placeHolder() {
		Chars("{");
		whiteSpace();
		String argumentName = identifier();
		whiteSpace();
		String type = Optional(
			()->{
				Chars(",");
				whiteSpace();
				String result = identifier();
				whiteSpace();
				return result;
			}, (String) null);

		Node result;
		if (type==null){
			result = new ArgumentNode(argumentName) {
				@Override
				public String format(FormatContext ctx) {
					return Objects.toString(ctx.arguments.get(argumentName));
				}
			};
		}
		else{
			FormatHandler handler = formatHandlers.get(type);
			if (handler==null){
				throw new NoMatchException();
			}
			result=handler.parse(argumentName, getParsingContext(), this);
		}
		Chars("}");
		return result;
	}

	public String identifier() {
		String result = Char(Character::isJavaIdentifierStart)+ZeroOrMoreChars(Character::isJavaIdentifierPart);
		whiteSpace();
		return result;
	}

	public void whiteSpace() {
		ZeroOrMoreChars(Character::isWhitespace);
	}
}
