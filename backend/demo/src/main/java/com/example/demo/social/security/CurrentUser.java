package com.example.demo.social.security;

import java.lang.annotation.*;

/**
 * Resolves the currently authenticated user's ID.
 * TODO: Replace stub resolver with JWT extraction when Arek implements auth.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {}
