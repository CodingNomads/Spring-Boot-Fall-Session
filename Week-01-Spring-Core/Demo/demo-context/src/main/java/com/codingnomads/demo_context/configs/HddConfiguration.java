package com.codingnomads.demo_context.configs;

import com.codingnomads.demo_context.components.simple.Hdd;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HddConfiguration {
    @Bean
    public Hdd seagate() {
        return new Hdd("seagate");
    }

    @Bean("westernDigital")
    public Hdd thisWillBeIgnored() {
        return new Hdd("westernDigital");
    }

    @Bean
    public Hdd hdd() {
        return new Hdd("spare hdd");
    }

}
