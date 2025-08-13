package com.solo.portfolio.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Portfolio API",
        version = "v1",
        description = "API documentation for Portfolio backend, supporting CRUD and auth.",
        contact = @Contact(name = "Solo", email = "")
    )
)
@SecurityScheme(
    name = OpenApiConfig.BEARER_SCHEME_NAME,
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
    public static final String BEARER_SCHEME_NAME = "bearerAuth";
}



