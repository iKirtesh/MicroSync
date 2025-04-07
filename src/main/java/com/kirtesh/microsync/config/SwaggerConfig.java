package com.kirtesh.microsync.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MicroSync API",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                description = "MicroSync is a microservice for user synchronization which provides APIs for user registration, login, and verification.",
                contact = @Contact(
                        name = "Kirtesh Admute",
                        email = "kirteshadmute@gmail.com",
                        url = "https://ikirtesh.github.io"
                )
        ),
        servers = @Server(url = "http://localhost:8080"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authorization header using the Bearer scheme"
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Basic Authentication"

)

public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("MicroSync API")
                        .version("1.0.0")
                        .description("MicroSync is a microservice for user synchronization which provides APIs for user registration, login, and verification.")
                        .license(new io.swagger.v3.oas.models.info.License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .contact(new io.swagger.v3.oas.models.info.Contact().name("Kirtesh Admute").email("kirteshadmute@gmail.com").url("https://ikirtesh.github.io"))
                );
    }
}
