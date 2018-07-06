package com.rbkmoney.provider.samsungpay.config;

import com.rbkmoney.provider.samsungpay.iface.session.DumbRequestTransactionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;

/**
 * Created by vpankrashkin on 04.04.18.
 */
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
        docket.produces(new HashSet(){{add("application/json");}});
        docket.forCodeGeneration(true);
        return docket;
    }
}