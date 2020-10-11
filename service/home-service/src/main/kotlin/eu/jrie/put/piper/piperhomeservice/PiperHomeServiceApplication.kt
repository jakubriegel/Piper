package eu.jrie.put.piper.piperhomeservice

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class PiperHomeServiceApplication

fun main(args: Array<String>) {
    runApplication<PiperHomeServiceApplication>(*args)
}
