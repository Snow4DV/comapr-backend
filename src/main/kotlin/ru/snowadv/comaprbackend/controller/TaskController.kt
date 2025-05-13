package ru.snowadv.comaprbackend.controller


import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.snowadv.comaprbackend.dto.CategoryDto
import ru.snowadv.comaprbackend.payload.response.MessageResponse
import ru.snowadv.comaprbackend.service.CategoryService
import ru.snowadv.comaprbackend.service.SessionService

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/task")
class TaskController(
    private val sessionService: SessionService,
) {


    @GetMapping("{id}/challenges")
    fun getChallenges(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(sessionService.getChallengesForTask(id))
    }
}
