package ru.snowadv.comaprbackend.controller


import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.snowadv.comaprbackend.dto.ClearMapSessionDto
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.payload.response.MessageResponse
import ru.snowadv.comaprbackend.security.service.UserService
import ru.snowadv.comaprbackend.service.DtoConverterService
import ru.snowadv.comaprbackend.service.RoadMapService
import ru.snowadv.comaprbackend.service.SessionService
import ru.snowadv.comaprbackend.service.VoteService
import ru.snowadv.comaprbackend.utils.currentUserIdOrNull

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/session")
class SessionController(
    private val roadMapService: RoadMapService,
    private val voteService: VoteService,
    private val userService: UserService,
    private val converter: DtoConverterService,
    private val sessionService: SessionService
) {
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    fun getSession(@PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser()
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if(user == null || (!session.public && !(session.creator == user || session.users.any { it.user == user }))) {
            return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        }
        return ResponseEntity.ok(converter.mapSessionToDto(session))
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/create")
    fun createSession(@RequestBody dto: ClearMapSessionDto, @PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser()
        val mapSession = converter.createSessionDtoToEntity(
            dto, user ?: return ResponseEntity.status(403).body(MessageResponse("unauthorized"))
        )
        if (mapSession.creator.id != user.id) return ResponseEntity.status(403).body(MessageResponse("unauthorized"))
        sessionService.createSession(mapSession)
        return ResponseEntity.ok(MessageResponse("success"))
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/update")
    fun updateSession(@RequestBody dto: ClearMapSessionDto, @PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        val oldSession = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (oldSession.state != MapSession.State.LOBBY) return ResponseEntity.status(403)
            .body(MessageResponse("session_already_started"))
        if (oldSession.creator.id != user.id) return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        sessionService.updateSession(converter.updateSessionDtoToEntity(id, dto, user))
        return ResponseEntity.ok(MessageResponse("success"))
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/start")
    fun startSession(@PathVariable id: Long): ResponseEntity<Any> {
        return changeSessionState(true, id)
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/end")
    fun endSession(@PathVariable id: Long): ResponseEntity<Any> {
        return changeSessionState(false, id)
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/join")
    fun joinSession(@PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id == user.id || session.users.any { it.user == user }) return ResponseEntity.status(403).body(MessageResponse("already_joined"))
        if (session.state != MapSession.State.LOBBY) {
            return ResponseEntity.status(403).body(MessageResponse("session_already_started"))
        }

        if(!sessionService.joinSession(id, user)) return ResponseEntity.status(403).body(MessageResponse("already_joined"))
        return ResponseEntity.ok(MessageResponse("success"))
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/leave")
    fun leaveSession(@PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id && !session.users.any { it.user == user }) return ResponseEntity.status(403).body(MessageResponse("not_in_session"))
        if (session.state != MapSession.State.LOBBY) {
            return ResponseEntity.status(403).body(MessageResponse("session_already_started"))
        }
        if(!sessionService.leaveSession(id, user)) return ResponseEntity.status(403).body(MessageResponse("not_in_session"))
        return ResponseEntity.ok(MessageResponse("success"))
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/markTask/{taskId}")
    fun markTask(@PathVariable id: Long, @PathVariable taskId: Long, @RequestParam state: Boolean): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id && !session.users.any { it.user == user }) return ResponseEntity.status(403).body(MessageResponse("not_in_session"))
        if (session.state != MapSession.State.STARTED) {
            return ResponseEntity.status(403).body(MessageResponse("session_not_started"))
        }
        if(!sessionService.markTask(id, taskId, user.id ?: error("not full user"), state)) return ResponseEntity.status(403).body(MessageResponse("cant_mark"))
        return ResponseEntity.ok(MessageResponse("success"))
    }


    private fun changeSessionState(start: Boolean, id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id) return ResponseEntity.status(403).body(MessageResponse("not_authorized"))
        if (start && !sessionService.startSession(session.id ?: -1) || !start && !sessionService.endSession(session.id ?: -1)) {
            return ResponseEntity.status(403).body(MessageResponse("cant_start_or_end_session"))
        }
        return ResponseEntity.ok(MessageResponse("success"))
    }



    private fun getCurrentUser(): User? {
        return userService.getUserById(
            SecurityContextHolder.getContext().currentUserIdOrNull() ?: -1
        )
    }

}
