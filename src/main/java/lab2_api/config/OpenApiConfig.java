
package lab2_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Chinook Album API")
                        .version("1.0")
                        .description(
                                "REST API secured with Spring Security. "
                                + "Authenticated USER and ADMIN accounts "
                                + "can read albums. Only ADMIN accounts "
                                + "can create, update, or delete albums."
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )
                .components(new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description(
                                                "Enter your application username "
                                                + "and password."
                                        )
                        )
                );
    }
}
