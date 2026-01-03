package com.financecore.library.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DTO record class for auth properties
 *
 * @author Roshan
 */
@ConfigurationProperties(prefix = "fc.auth")
public record AuthProperties(@NotBlank String url, Client client) {

    /**
     * DTO record class for client properties
     */
    public record Client(@NotBlank String id, @NotBlank String secret){}
}
