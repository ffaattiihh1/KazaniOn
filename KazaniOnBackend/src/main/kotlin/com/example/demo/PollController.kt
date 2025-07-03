package com.example.demo

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

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
}

// Data classes for API responses
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