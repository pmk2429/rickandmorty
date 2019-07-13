package com.delivery.rickandmorty.networking;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Makes the Retrofit Call retry on failure
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Retry {
  int value() default 3;
}