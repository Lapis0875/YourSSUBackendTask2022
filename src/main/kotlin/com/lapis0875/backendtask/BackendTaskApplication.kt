package com.lapis0875.backendtask

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientCodecCustomizer
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class AppConfig {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate? {
        return builder
            .rootUri("http://localhost:8080/api")
            .setConnectTimeout(Duration.ofMillis(3000))
            .setReadTimeout(Duration.ofMillis(3000))
            .build()
    }
}

@SpringBootApplication
class BackendTaskApplication

fun main(args: Array<String>) {
    runApplication<BackendTaskApplication>(*args)
}
