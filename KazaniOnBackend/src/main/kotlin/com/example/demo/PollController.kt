package com.example.demo

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/polls")
@CrossOrigin(origins = ["*"])
class PollController(private val pollRepository: PollRepository) {

    @GetMapping
    fun getAllPolls(): List<Poll> {
        return pollRepository.findAll()
    }

    @GetMapping("/active")
    fun getActivePolls(): List<Poll> {
        return pollRepository.findActivePolls()
    }

    @GetMapping("/active/link")
    fun getActiveLinkPolls(): List<Poll> {
        return pollRepository.findActiveLinkPolls()
    }

    @GetMapping("/location-based")
    fun getLocationBasedPolls(): List<LocationPollDto> {
        return pollRepository.findLocationBasedPolls().map { poll ->
            LocationPollDto(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                points = poll.points,
                latitude = poll.latitude ?: 0.0,
                longitude = poll.longitude ?: 0.0,
                radius = poll.radius ?: 1000.0
            )
        }
    }

    @PostMapping
    fun createPoll(@RequestBody request: CreatePollRequest): ResponseEntity<Poll> {
        val poll = Poll(
            title = request.title,
            description = request.description ?: "",
            price = request.price,
            points = (request.price * 10).toInt(), // 1 TL = 10 points
            isActive = request.isActive ?: true,
            latitude = request.latitude,
            longitude = request.longitude,
            linkUrl = request.link,
            createdAt = LocalDateTime.now()
        )
        
        val savedPoll = pollRepository.save(poll)
        return ResponseEntity.ok(savedPoll)
    }

    @PutMapping("/{pollId}")
    fun updatePoll(@PathVariable pollId: Long, @RequestBody request: CreatePollRequest): ResponseEntity<Poll> {
        val existingPoll = pollRepository.findById(pollId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val updatedPoll = existingPoll.copy(
            title = request.title,
            description = request.description ?: existingPoll.description,
            price = request.price,
            points = (request.price * 10).toInt(),
            isActive = request.isActive ?: existingPoll.isActive,
            latitude = request.latitude,
            longitude = request.longitude,
            linkUrl = request.link
        )
        
        val savedPoll = pollRepository.save(updatedPoll)
        return ResponseEntity.ok(savedPoll)
    }

    @DeleteMapping("/{pollId}")
    fun deletePoll(@PathVariable pollId: Long): ResponseEntity<Map<String, Any>> {
        if (!pollRepository.existsById(pollId)) {
            return ResponseEntity.notFound().build()
        }
        
        pollRepository.deleteById(pollId)
        return ResponseEntity.ok(mapOf(
            "success" to true,
            "message" to "Poll deleted successfully"
        ))
    }

    @PostMapping("/{pollId}/complete")
    fun completePoll(@PathVariable pollId: Long, @RequestBody request: CompletePollRequest): ResponseEntity<CompletePollResponse> {
        val poll = pollRepository.findById(pollId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        // Here you would typically save the completion to a separate table
        // For now, just return success
        return ResponseEntity.ok(CompletePollResponse(
            success = true,
            pointsEarned = poll.points,
            message = "Poll completed successfully!"
        ))
    }

    @PostMapping("/init-sample-data")
    fun initSamplePolls(): ResponseEntity<Map<String, Any>> {
        try {
            val existingCount = pollRepository.count()
            if (existingCount == 0L) {
                val samplePolls = listOf(
                    Poll(
                        title = "İstanbul Şehir Anketi",
                        description = "İstanbul'da yaşam kalitesi hakkında anket",
                        price = 10.0,
                        points = 100,
                        isActive = true,
                        latitude = 41.0082,
                        longitude = 28.9784,
                        createdAt = LocalDateTime.now()
                    ),
                    Poll(
                        title = "Ankara Ulaşım Anketi",
                        description = "Ankara'da toplu taşıma hakkında anket",
                        price = 15.0,
                        points = 150,
                        isActive = true,
                        latitude = 39.9334,
                        longitude = 32.8597,
                        createdAt = LocalDateTime.now()
                    ),
                    Poll(
                        title = "Online Alışveriş Anketi",
                        description = "E-ticaret alışkanlıkları hakkında anket",
                        price = 20.0,
                        points = 200,
                        isActive = true,
                        linkUrl = "https://forms.google.com/sample-survey",
                        createdAt = LocalDateTime.now()
                    ),
                    Poll(
                        title = "İzmir Turizm Anketi",
                        description = "İzmir'de turizm deneyimleri",
                        price = 12.0,
                        points = 120,
                        isActive = true,
                        latitude = 38.4192,
                        longitude = 27.1287,
                        createdAt = LocalDateTime.now()
                    ),
                    Poll(
                        title = "Teknoloji Kullanımı Anketi",
                        description = "Günlük teknoloji kullanım alışkanlıkları",
                        price = 8.0,
                        points = 80,
                        isActive = true,
                        linkUrl = "https://forms.google.com/tech-survey",
                        createdAt = LocalDateTime.now()
                    )
                )
                
                val savedPolls = pollRepository.saveAll(samplePolls)
                println("✅ Created ${savedPolls.size} sample polls")
                
                return ResponseEntity.ok(mapOf(
                    "success" to true,
                    "message" to "Sample polls created successfully",
                    "pollsCreated" to savedPolls.size,
                    "polls" to savedPolls
                ))
            } else {
                return ResponseEntity.ok(mapOf(
                    "success" to false,
                    "message" to "Polls already exist in database",
                    "existingCount" to existingCount
                ))
            }
        } catch (e: Exception) {
            return ResponseEntity.ok(mapOf(
                "success" to false,
                "message" to "Error creating sample polls: ${e.message}"
            ))
        }
    }
}

// Data classes for API requests and responses
data class CreatePollRequest(
    val title: String,
    val description: String?,
    val price: Double,
    val locationBased: Boolean?,
    val latitude: Double?,
    val longitude: Double?,
    val link: String?,
    val isActive: Boolean?
)

data class LocationPollDto(
    val id: Long,
    val title: String,
    val description: String,
    val points: Int,
    val latitude: Double,
    val longitude: Double,
    val radius: Double
)

data class CompletePollRequest(
    val answers: List<String>
)

data class CompletePollResponse(
    val success: Boolean,
    val pointsEarned: Int,
    val message: String
) 