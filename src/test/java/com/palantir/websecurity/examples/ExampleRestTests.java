/*
 * (c) Copyright 2016 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.websecurity.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.net.HttpHeaders;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Tests for {@link Example.ExampleRestApplication}. */
public final class ExampleRestTests {

    public static final String ORIGIN_VALUE = "http://origin.com";

    @ExtendWith(DropwizardExtensionsSupport.class)
    public static final DropwizardAppExtension<Example.ExampleConfiguration> RULE =
            new DropwizardAppExtension<>(
                    Example.ExampleRestApplication.class,
                    Example.ExampleRestApplication.class
                            .getClassLoader()
                            .getResource("example-rest.yml")
                            .getPath());

    private static Client client;

    @BeforeAll
    public static void BeforeAll() {
        client =
                new JerseyClientBuilder(RULE.getEnvironment())
                        .withProperty(ClientProperties.CONNECT_TIMEOUT, 10000)
                        .withProperty(ClientProperties.READ_TIMEOUT, 10000)
                        .build("tests");
    }

    @Test
    public void testCorsHeadersAppliedToApi() {
        Response response =
                client.target(
                                String.format(
                                        "http://localhost:%d/example-context/hello",
                                        RULE.getLocalPort()))
                        .request()
                        .header(HttpHeaders.ORIGIN, ORIGIN_VALUE)
                        .get();

        // check basic functionality
        assertEquals(200, response.getStatus());
        assertEquals(Example.EXAMPLES_RESOURCE_RESPONSE, response.readEntity(String.class));

        // check for a YAML defined CORS entry
        assertEquals(
                ORIGIN_VALUE, response.getHeaderString(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testWebSecurityHeadersNotAppliedToApi() {
        Response response =
                client.target(
                                String.format(
                                        "http://localhost:%d/example-context/hello",
                                        RULE.getLocalPort()))
                        .request()
                        .get();

        // check basic functionality
        assertEquals(200, response.getStatus());

        // check that no web security headers are on the response
        assertNull(response.getHeaderString(HttpHeaders.CONTENT_SECURITY_POLICY));
        assertNull(response.getHeaderString(HttpHeaders.X_CONTENT_TYPE_OPTIONS));
        assertNull(response.getHeaderString(HttpHeaders.X_FRAME_OPTIONS));
    }
}
