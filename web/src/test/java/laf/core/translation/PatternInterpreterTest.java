package laf.core.translation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.time.*;
import java.util.*;
import java.util.function.Function;

import laf.core.translation.PatternInterpreter.FormatHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PatternInterpreterTest {

	@Mock
	FormatHandler formatHandler;

	@InjectMocks
	PatternInterpreter format;

	@Before
	public void before() {
		HashMap<String, FormatHandler> handlers = PatternInterpreter
				.defaultHandlerMap();
		handlers.put("aFormat", formatHandler);
		format.initialize(handlers);
	}

	@Test
	public void testSimple() {
		assertEquals("foo",
				format.interpret("foo", new HashMap<>(), Locale.ENGLISH));
	}

	@Test
	public void testSimpleEscape() {
		assertEquals("foo$",
				format.interpret("foo$$", new HashMap<>(), Locale.ENGLISH));
		assertEquals("foo{",
				format.interpret("foo${", new HashMap<>(), Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameter() {
		assertEquals("the 4", format.interpret("the {param}", map("param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameterEscape() {
		assertEquals("the 4", format.interpret("the {pa$}ram}",
				map("pa}ram", 4), Locale.ENGLISH));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParameterFormatType() {
		when(
				formatHandler.handle(eq(Locale.ENGLISH), eq(4), eq(null),
						any(Function.class))).thenReturn("formatted");
		assertEquals("the formatted", format.interpret("the {param, aFormat}",
				map("param", 4), Locale.ENGLISH));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParameterFormatTypeStyle() {
		when(
				formatHandler.handle(eq(Locale.ENGLISH), eq(4), eq(" aSt$yle"),
						any(Function.class))).thenReturn("formatted");
		assertEquals(
				"the formatted",
				format.interpret("the {param, aFormat, aSt$yle}",
						map("param", 4), Locale.ENGLISH));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDate() {
		assertEquals(
				"the 2/1/14",
				format.interpret("the {param, date, short}",
						map("param", new Date(2014, 1, 1)), Locale.ENGLISH));
	}

	@Test
	public void testLocalDate() {
		assertEquals(
				"the 1/1/14",
				format.interpret("the {param, date, short}",
						map("param", LocalDate.of(2014, 1, 1)), Locale.ENGLISH));
	}

	@Test
	public void testDateTimeDate() {
		assertEquals(
				"the 2014-01-01T10:00:00Z[UTC]",
				format.interpret(
						"the {param, dateTime}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 00),
								ZoneId.of("UTC"))), Locale.ENGLISH));
	}

	@Test
	public void testChoice() {
		String template = "there {param, choice, 1#is one onion| 2#are two onions| 2<are {param, number} onions}";
		assertEquals("there is one onion",
				format.interpret(template, map("param", 1), Locale.ENGLISH));
		assertEquals("there are two onions",
				format.interpret(template, map("param", 2), Locale.ENGLISH));
		assertEquals("there are 3 onions",
				format.interpret(template, map("param", 3), Locale.ENGLISH));
	}

	Map<String, Object> map(String key, Object value) {
		Map<String, Object> result = new HashMap<>();
		result.put(key, value);
		return result;
	}
}
