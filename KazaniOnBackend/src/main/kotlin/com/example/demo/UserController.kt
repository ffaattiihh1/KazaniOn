package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(private val userRepository: UserRepository) {
    
    data class CreateUserRequest(val username: String)
    
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userRepository.findAll()
        println("🔍 ADMIN PANEL DEBUG: Found ${users.size} users")
        users.forEach { user ->
            println("👤 User: ID=${user.id}, username=${user.username}")
        }
        return ResponseEntity.ok(users)
    }
    
    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<User> {
        val user = User(username = request.username)
        val savedUser = userRepository.save(user)
        println("✅ Created user: ${savedUser.username} with ID: ${savedUser.id}")
        return ResponseEntity.ok(savedUser)
    }
    
    @PostMapping("/init-sample-data")
    fun initSampleData(): ResponseEntity<Map<String, Any>> {
        try {
            val existingCount = userRepository.count()
            if (existingCount == 0L) {
                val sampleUsers = listOf(
                    User(username = "fatihsakar1905"),
                    User(username = "fatih"),
                    User(username = "admin"),
                    User(username = "testuser"),
                    User(username = "demo_user")
                )
                
                val savedUsers = userRepository.saveAll(sampleUsers)
                println("✅ Created ${savedUsers.size} sample users")
                
                return ResponseEntity.ok(mapOf(
                    "success" to true,
                    "message" to "Sample users created successfully",
                    "usersCreated" to savedUsers.size,
                    "users" to savedUsers
                ))
            } else {
                return ResponseEntity.ok(mapOf(
                    "success" to false,
                    "message" to "Users already exist in database",
                    "existingCount" to existingCount
                ))
            }
        } catch (e: Exception) {
            return ResponseEntity.ok(mapOf(
                "success" to false,
                "message" to "Error creating sample users: ${e.message}"
            ))
        }
    }
    
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userRepository.findById(id).orElse(null)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/count")
    fun getUserCount(): ResponseEntity<Map<String, Int>> {
        val count = userRepository.count().toInt()
        return ResponseEntity.ok(mapOf("count" to count))
    }
} 