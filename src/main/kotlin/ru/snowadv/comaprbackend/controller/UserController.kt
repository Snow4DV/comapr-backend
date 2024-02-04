package ru.snowadv.comaprbackend.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.snowadv.comaprbackend.dto.UserAndSessionsDto
import ru.snowadv.comaprbackend.payload.response.MessageResponse
import ru.snowadv.comaprbackend.security.service.UserService
import ru.snowadv.comaprbackend.service.DtoConverterService
import ru.snowadv.comaprbackend.service.SessionService
import ru.snowadv.comaprbackend.utils.currentUserIdOrNull


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val converter: DtoConverterService,
    private val sessionService: SessionService
) {
    @GetMapping("info")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    fun getActiveSessions(@RequestHeader("Authorization") authorizationHeader: String?): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().currentUserIdOrNull()?.let { userService.getUserById(it) }
            ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val userDto = converter.userToDto(user)
        val sessionsDto = sessionService.getAllSessionsByUser(user.id ?: error("not persisted user"))
            .map { converter.mapSessionToDto(it) }
        return ResponseEntity.ok(UserAndSessionsDto(userDto, sessionsDto))
    }

    @GetMapping("activeSessions")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    fun fetchMaps(): ResponseEntity<Any> {
        val userId = SecurityContextHolder.getContext().currentUserIdOrNull()
            ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))

        return ResponseEntity.ok(
            sessionService.getAllActiveSessionsByUser(userId).map { converter.mapSessionToDto(it) })
    }
}