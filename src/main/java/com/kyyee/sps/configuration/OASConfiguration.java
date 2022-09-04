package com.kyyee.sps.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Swagger配置类
 */
@Configuration
public class OASConfiguration {

    @Value("${spring.application.name:NA}")
    private String appName;

    @Resource
    SpringDocConfigProperties properties;

    @Bean
    public GroupedOpenApi spsApi() {
        return GroupedOpenApi.builder()
            .group(appName)
            .pathsToMatch(properties.getPathsToMatch().toArray(new String[]{}))
            .packagesToScan(properties.getPackagesToScan().toArray(new String[]{}))
            .build();
    }

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("OAS接口文档")
                .description("更多请查看 https://github.com/kyyee/springboot-project-seed")
                .contact(new Contact()
                    .name("kyyee")
                    .url("https://github.com/kyyee")
                    .email("kyyeeyoung@163.com"))
                .version("v1.0.0")
                .license(new License()
                    .name("Apache 2.0")
//                    https://github.com/kyyee/springboot-project-seed/LICENSE
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .externalDocs(new ExternalDocumentation()
                .description("替代 Springfox 的 Springdoc 入门 文档")
                .url("https://github.com/kyyee/springboot-project-seed"));
    }

}
