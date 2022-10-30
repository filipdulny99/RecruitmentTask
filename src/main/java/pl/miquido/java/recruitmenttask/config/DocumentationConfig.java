package pl.miquido.java.recruitmenttask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class DocumentationConfig {

    @Bean
    public Docket swaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/starwars/**"))
                .build()
                .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Star Wars Character Management",
                "Load and retrieve characters.",
                "1.0",
                "Free to use",
                new Contact("Filip Dulny", "-", "filipdulny.recruitment@gmail.com"),
                "API License",
                "https://wiki.creativecommons.org/wiki/Open_license",
                Collections.emptyList());
    }
}
