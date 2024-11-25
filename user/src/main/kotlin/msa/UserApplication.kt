package msa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
