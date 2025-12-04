package org.otus.finalProject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Football Statistics API")
                        .description("CRUD по coaches/players/teams/matches/championships")
                        .version("v1.0.0")
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:9090").description("Local")
                ));
    }
}
