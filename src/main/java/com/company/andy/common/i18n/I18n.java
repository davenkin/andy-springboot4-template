package com.company.andy.common.i18n;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class I18n {
  private static final Locale DEFAULT_LOCALE = Locale.ROOT;
  private final MessageSource messageSource;

  public String get(String code) {
    return get(code, DEFAULT_LOCALE);
  }

  public String get(String code, Object... args) {
    return get(code, DEFAULT_LOCALE, args);
  }

  public String get(String code, Locale locale) {
    return messageSource.getMessage(code, null, locale);
  }

  public String get(String code, Locale locale, Object... args) {
    return messageSource.getMessage(code, args, locale);
  }
}
