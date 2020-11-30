package eu.jrie.put.piper.piperhomeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PiperHomeServiceApplication

fun main(args: Array<String>) {
    runApplication<PiperHomeServiceApplication>(*args)
}
