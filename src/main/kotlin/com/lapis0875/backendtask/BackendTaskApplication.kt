package com.lapis0875.backendtask

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
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

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): (WebSecurity) -> Unit {
        return { web: WebSecurity -> web.ignoring().antMatchers("/**") }
    }
}

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class BackendTaskApplication

fun main(args: Array<String>) {
    runApplication<BackendTaskApplication>(*args)
}
