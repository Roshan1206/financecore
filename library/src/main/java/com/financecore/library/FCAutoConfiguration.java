package com.financecore.library;

import com.financecore.library.config.oauth2.OAuth2ClientConfig;
import com.financecore.library.config.security.ActuatorSecurityConfig;
import com.financecore.library.properties.AuthProperties;
import com.financecore.library.config.security.JwtTokenSecurityConfig;
import com.financecore.library.config.security.OpaqueTokenSecurityConfig;
import com.financecore.library.config.web.WebClientConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Configuration class to be added in imports for auto configuration in consuming repos
 */
@AutoConfiguration
@Import({
        OAuth2ClientConfig.class,
        ActuatorSecurityConfig.class,
        JwtTokenSecurityConfig.class,
        OpaqueTokenSecurityConfig.class,
        WebClientConfig.class
})
@EnableConfigurationProperties({AuthProperties.class})
public class FCAutoConfiguration {
}
