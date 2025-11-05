package com.financecore.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("account", r -> r
                        .path("/fc/account/**")
                        .filters(f -> f
                                .rewritePath("/fc/accounts/(?<segment>.*)", "/${segment}"))
                        .uri("lb://ACCOUNTS"))
                .route("auth", r -> r
                        .path("/fc/auth/**")
                        .filters(f -> f
                                .rewritePath("/fc/auth/(?<segment>.*)", "/${segment}"))
                        .uri("lb://AUTH"))
                .route("customer", r -> r
                        .path("/fc/auth/**")
                        .filters(f -> f
                                .rewritePath("/fc/customers/(?<segment>.*)", "/${segment}"))
                        .uri("lb://CUSTOMER"))
                .route("transaction", r -> r
                        .path("/fc/transaction/**")
                        .filters(f -> f
                                .rewritePath("/fc/transactions/(?<segment>.*)", "/${segment}"))
                        .uri("lb://TRANSACTION"))
                .build();
    }
}
