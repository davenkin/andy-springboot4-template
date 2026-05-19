package com.company.andy.common.security;

import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

// todo: imple, may not extends

public class AnonymousAuthenticationTokenFilter extends AnonymousAuthenticationFilter {
    public AnonymousAuthenticationTokenFilter(String key) {
        super(key);
    }
}
