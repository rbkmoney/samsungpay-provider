package com.rbkmoney.provider.samsungpay.config;

import com.rbkmoney.provider.samsungpay.iface.transaction.DumbRequestTransactionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(DumbRequestTransactionController.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build();
        Set<String> producesSet = new HashSet<>();
        producesSet.add("application/json");
        docket.produces(producesSet);
        docket.forCodeGeneration(true);
        return docket;
    }
}
