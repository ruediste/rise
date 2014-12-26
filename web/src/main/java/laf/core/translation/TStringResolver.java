package laf.core.translation;

import java.util.Locale;

public interface TStringResolver {

	String resolve(TString str, Locale locale);
}
