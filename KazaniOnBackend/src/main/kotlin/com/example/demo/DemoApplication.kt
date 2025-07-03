package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@RestController
class TestController {
    @GetMapping("/")
    fun hello(): String {
        return "KazaniOn Backend is running!"
    }
    
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "UP", "message" to "Application is healthy")
    }
}
