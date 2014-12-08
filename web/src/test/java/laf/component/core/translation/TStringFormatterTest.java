package laf.component.core.translation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.*;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TStringFormatterTest {

	@Mock
	ResourceResolver resourceResolver;

	@Mock
	FormatHandler formatHandler;

	@InjectMocks
	TStringFormatter format;

	@Before
	public void before() {
		HashMap<String, FormatHandler> handlers = new HashMap<>();
		handlers.put("aFormat", formatHandler);
		handlers.put("date", TStringFormatter.createDateHandler());
		handlers.put("time", TStringFormatter.createTimeHandler());
		handlers.put("dateTime", TStringFormatter.createDateTimeHandler());
		handlers.put("choice", TStringFormatter.createChoiceHandler());
		handlers.put("number", TStringFormatter.createNumberHandler());
		format.initialize(handlers);
	}

	@Test
	public void testSimple() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"foo");
		assertEquals("foo", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleEscape() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"foo");
		assertEquals("foo", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameter() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param}");
		assertEquals("the 4", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameterEscape() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {pa$}ram}");
		assertEquals("the 4", format.format(new TString("resKey", "pa}ram", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testParameterFormatType() {
		TString tString = new TString("resKey", "param", 4);
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param, aFormat}");
		when(formatHandler.handle(Locale.ENGLISH, 4, null, format, tString))
				.thenReturn("formatted");
		assertEquals("the formatted", format.format(tString, Locale.ENGLISH));
	}

	@Test
	public void testParameterFormatTypeStyle() {
		TString tString = new TString("resKey", "param", 4);
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param, aFormat, aSt$yle}");
		when(
				formatHandler.handle(Locale.ENGLISH, 4, " aSt$yle", format,
						tString)).thenReturn("formatted");
		assertEquals("the formatted", format.format(tString, Locale.ENGLISH));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDate() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param, date, short}");
		assertEquals("the 2/1/14", format.format(new TString("resKey", "param",
				new Date(2014, 1, 1)), Locale.ENGLISH));
	}

	@Test
	public void testLocalDate() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param, date, short}");
		assertEquals("the 1/1/14", format.format(new TString("resKey", "param",
				LocalDate.of(2014, 1, 1)), Locale.ENGLISH));
	}

	@Test
	public void testDateTimeDate() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param, dateTime}");
		assertEquals("the 2014-01-01T10:00:00Z[UTC]",
				format.format(
						new TString("resKey", "param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 00),
								ZoneId.of("UTC"))), Locale.ENGLISH));
	}

	@Test
	public void testChoice() {
		when(resourceResolver.resolve("resKey", Locale.ENGLISH))
				.thenReturn(
						"there {param, choice, 1#is one onion| 2#are two onions| 2<are {param, number, } onions}");
		assertEquals("there is one onion", format.format(new TString("resKey",
				"param", 1), Locale.ENGLISH));
		assertEquals("there are two onions", format.format(new TString(
				"resKey", "param", 2), Locale.ENGLISH));
		assertEquals("there are 3 onions", format.format(new TString("resKey",
				"param", 3), Locale.ENGLISH));
	}
}
