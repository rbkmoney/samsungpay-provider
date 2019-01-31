package com.rbkmoney.provider.samsungpay.config;

import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.provider.samsungpay.iface.decrypt.ProviderHandler;
import com.rbkmoney.provider.samsungpay.service.SPayClient;
import com.rbkmoney.provider.samsungpay.service.SPayService;
import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import com.rbkmoney.woody.api.flow.WFlow;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class ApplicationConfig {

    public static final String HEALTH = "/actuator/health";

    @Value("${server.rest_port}")
    private int restPort;

    @Value("/${server.rest_path_prefix}/")
    private String httpPathPrefix;

    @Bean
    public SPayClient transactionClient(
            @Value("${samsung.trans_url_template}") String transactionURLTemplate,
            @Value("${samsung.cred_url_template}") String credentialsURLTemplate,
            @Value("${samsung.conn_timeout_ms}") int connTimeoutMs,
            @Value("${samsung.read_timeout_ms}") int readTimeoutMs,
            @Value("${samsung.write_timeout_ms}") int writeTimeoutMs) {
        return new SPayClient(transactionURLTemplate, credentialsURLTemplate, connTimeoutMs, readTimeoutMs, writeTimeoutMs);
    }

    @Bean
    public SPKeyStore keyStore(@Value("${keys_path}") String keysPath) {
        return new SPKeyStore(keysPath);
    }

    @Bean
    public SPayService transactionService(SPayClient SPayClient, SPKeyStore spKeyStore) {
        return new SPayService(SPayClient, spKeyStore);
    }

    @Bean
    public PaymentToolProviderSrv.Iface providerHandler(SPayService sPayService) {
        return new ProviderHandler(sPayService);
    }


    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector connector = new Connector();
        connector.setPort(restPort);
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    public FilterRegistrationBean externalPortRestrictingFilter() {
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getLocalPort() == restPort) {
                    if (!(request.getServletPath().startsWith(httpPathPrefix) || request.getServletPath().startsWith(HEALTH))) {
                        response.sendError(404, "Unknown address");
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }
        };

        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-100);
        filterRegistrationBean.setName("httpPortFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean woodyFilter() {
        WFlow wFlow = new WFlow();
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getLocalPort() == restPort) {
                    if (request.getServletPath().startsWith(httpPathPrefix)) {
                        wFlow.createServiceFork(() -> {
                            try {
                                filterChain.doFilter(request, response);
                            } catch (IOException | ServletException e) {
                                sneakyThrow(e);
                            }
                        }).run();
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }

            private <E extends Throwable, T> T sneakyThrow(Throwable t) throws E {
                throw (E) t;
            }
        };

        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-50);
        filterRegistrationBean.setName("woodyFilter");
        filterRegistrationBean.addUrlPatterns(httpPathPrefix + "*");
        return filterRegistrationBean;
    }
}
