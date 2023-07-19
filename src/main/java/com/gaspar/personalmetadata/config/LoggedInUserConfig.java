package com.gaspar.personalmetadata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class LoggedInUserConfig {

    private String username;
    private String userId;

}
