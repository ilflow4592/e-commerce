package com.example.ecommerce.api.port_one;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "port-one-api.auth")
public class ApiAuthProperties {

    private final String token;

}
