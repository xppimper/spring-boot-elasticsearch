package com.projetsperso.predictice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Music Library for album management",
                description = "The API allows users to search albums and visualize their details",
                version = "0.9"
        ),
        servers = {
                @Server(description = "Local ENV", url = "http://localhost:8080")
        }
)
public class OpenApiConfig {
}
