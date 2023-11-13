/*
 * (c) Copyright 2016 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.websecurity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Used by the application's {@link io.dropwizard.core.Configuration} to provide a {@link
 * WebSecurityConfiguration}.
 */
public interface WebSecurityConfigurable {

    @NotNull
    @Valid
    WebSecurityConfiguration getWebSecurityConfiguration();
}
