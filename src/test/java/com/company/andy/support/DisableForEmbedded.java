package com.company.andy.support;

import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@DisabledIf(expression = "#{environment.acceptsProfiles(T(org.springframework.core.env.Profiles).of('local-it-embedded'))}", loadContext = true)
public @interface DisableForEmbedded {
}
