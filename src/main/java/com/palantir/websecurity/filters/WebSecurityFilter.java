/*
 * (c) Copyright 2016 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.websecurity.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import com.palantir.websecurity.WebSecurityConfiguration;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that injects the App Security headers using a {@link WebSecurityHeaderInjector} to every
 * request.
 */
public final class WebSecurityFilter implements Filter {

    private final WebSecurityHeaderInjector injector;

    public WebSecurityFilter(WebSecurityConfiguration config) {
        checkNotNull(config);

        this.injector = new WebSecurityHeaderInjector(config);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // intentionally left blank
    }

    @Override
    public void destroy() {
        // intentionally left blank
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        checkNotNull(request);
        checkNotNull(response);
        checkNotNull(chain);

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            this.injector.injectHeaders(
                    (HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }
}
