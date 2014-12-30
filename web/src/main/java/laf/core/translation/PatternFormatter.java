package laf.core.translation;

import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Stream;

import laf.core.translation.PatternParser.FormatContext;
import laf.core.translation.PatternParser.FormatHandler;
import laf.core.translation.PatternParser.LiteralNode;
import laf.core.translation.PatternParser.Node;
import laf.core.translation.PatternParser.SequenceNode;
import laf.core.translation.parse.Parser;
import laf.core.translation.parse.ParsingContext;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralRules;

public class PatternFormatter {

	private Map<String, FormatHandler> handlers;

	public PatternFormatter(Map<String, FormatHandler> handlers) {
		this.handlers = handlers;
	}

	public String format(String pattern, Map<String, Object> arguments,
			Locale locale) {
		ParsingContext ctx = new ParsingContext(pattern);
		PatternParser parser = Parser.create(PatternParser.class, ctx);
		parser.formatHandlers = handlers;
		Node node = parser.pattern();
		FormatContext fCtx = new FormatContext();
		fCtx.locale = locale;
		fCtx.arguments = arguments;
		return node.format(fCtx);
	}

	public static Map<String, FormatHandler> defaultHandlers() {
		HashMap<String, FormatHandler> result = new HashMap<>();
		result.put("plural", new PluralHandler());
		return result;
	}

	static class PluralNode implements Node {

		public Map<Double, Node> explicitRules = new HashMap<>();
		public Map<String, Node> keywordRules = new HashMap<>();
		private String argumentName;

		PluralNode(String argumentName) {
			this.argumentName = argumentName;

		}

		@Override
		public String format(FormatContext ctx) {
			Object numberArg = ctx.arguments.get(argumentName);
			if (!(numberArg instanceof Number)) {
				throw new IllegalArgumentException("'" + numberArg
						+ "' is not a Number");
			}
			Number numberObject = (Number) numberArg;
			double number = numberObject.doubleValue();

			// try explicit rules
			{
				Node node = explicitRules.get(number);
				if (node != null) {
					return node.format(ctx);
				}
			}

			// try keyword rules
			PluralRules pluralRules = PluralRules.forLocale(ctx.locale);
			String keyword = pluralRules.select(number);
			Node node = keywordRules.get(keyword);
			if (node == null) {
				node = keywordRules.get(PluralRules.KEYWORD_OTHER);
			}
			if (node == null) {
				throw new RuntimeException("Number " + number
						+ " was mapped to keyword <" + keyword
						+ "> which is not defined, neither is <other>.");
			}
			return node.format(ctx);
		}

		@Override
		public Set<String> argumentNames() {
			return Stream
					.concat(explicitRules.values().stream(),
							keywordRules.values().stream())
					.flatMap(node -> node.argumentNames().stream())
					.collect(toSet());
		}

		public void addRule(String selector, Node node) {
			if (selector.startsWith("=")) {
				explicitRules.put(Double.valueOf(selector.substring(1)), node);
			} else {
				keywordRules.put(selector, node);
			}
		}
	}

	/**
	 *
	 * <blockquote>
	 *
	 * <pre>
	 * pluralStyle = (selector '{' subPattern() '}')+
	 * selector = explicitValue | keyword
	 * explicitValue = '=' number
	 * keyword = [^[[:Pattern_Syntax:][:Pattern_White_Space:]]]+
	 * subPattern: normal pattern format, # are replaced
	 * </pre>
	 *
	 * </blockquote>
	 */
	static class PluralParser extends Parser {

		public PluralParser(ParsingContext ctx) {
			super(ctx);
		}

		PluralNode style(String argumentName, PatternParser parser) {
			PluralNode result = new PluralNode(argumentName);
			Chars(",");
			parser.whiteSpace();
			OneOrMore(() -> {
				String selector = selector(parser);
				Chars("{");
				Node node = subPattern(argumentName, parser);
				Chars("}");
				parser.whiteSpace();
				result.addRule(selector, node);
			});
			return result;
		}

		String selector(PatternParser parser) {
			String result = Optional(() -> {
				Chars("=");
				parser.whiteSpace();
				return "=";
			}, "");
			result += OneOrMoreChars(Character::isLetterOrDigit);
			parser.whiteSpace();
			return result;
		}

		Node subPattern(String argumentName, PatternParser parser) {
			Node result = this.<Node, Node> ZeroOrMore(
					() -> FirstOf(() -> parser.placeHolder(), () -> this
							.<Node> Chars("#", () -> new Node() {

								@Override
								public String format(FormatContext ctx) {
									return NumberFormat.getNumberInstance(
											ctx.locale).format(
											ctx.arguments.get(argumentName));
								}

								@Override
								public Set<String> argumentNames() {
									return Collections.singleton(argumentName);
								}
							}), () -> new LiteralNode(
							OneOrMoreChars(cp -> cp != '{' && cp != '#'
									&& cp != '}'))), SequenceNode::new);
			return result;
		}
	}

	public static class PluralHandler implements FormatHandler {

		@Override
		public Node parse(String argumentName, ParsingContext ctx,
				PatternParser parser) {
			return Parser.create(PluralParser.class, ctx).style(argumentName,
					parser);
		}

	}
}
