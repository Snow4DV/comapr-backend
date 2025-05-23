package ru.snowadv.comaprbackend.controller


import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.snowadv.comaprbackend.dto.*
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.cooperation.SessionChatMessage
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
        if (user == null || (!session.public && !(session.creator == user || session.users.any { it.user == user }))) {
            return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        }
        val convertedToDto = converter.mapSessionToDto(session, user)
        return ResponseEntity.ok(convertedToDto)
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
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id == user.id || session.users.any { it.user == user }) return ResponseEntity.status(403)
            .body(MessageResponse("already_joined"))
        if (session.state != MapSession.State.LOBBY) {
            return ResponseEntity.status(403).body(MessageResponse("session_already_started"))
        }

        if (!sessionService.joinSession(id, user)) return ResponseEntity.status(403)
            .body(MessageResponse("already_joined"))
        return ResponseEntity.ok(converter.mapSessionToDto(session, user))
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("create")
    fun createSession(@RequestBody dto: ClearMapSessionDto): ResponseEntity<Any> {
        val user = getCurrentUser()
        val mapSession = converter.createSessionDtoToEntity(
            dto, user ?: return ResponseEntity.status(403).body(MessageResponse("unauthorized"))
        )
        val roadMap = roadMapService.getRoadMapById(dto.roadMapId) ?: return ResponseEntity.status(404)
            .body(MessageResponse("no_such_roadmap"))
        if (mapSession.creator.id != user.id) return ResponseEntity.status(403).body(MessageResponse("unauthorized"))
        val savedSession = sessionService.createSession(mapSession)
        return ResponseEntity.ok(converter.mapSessionToDto(savedSession, user))
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/update")
    fun updateSession(@RequestBody dto: ClearMapSessionDto, @PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val oldSession = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (oldSession.state != MapSession.State.LOBBY) return ResponseEntity.status(403)
            .body(MessageResponse("session_already_started"))
        if (oldSession.creator.id != user.id) return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val updatedSession = sessionService.updateSession(converter.updateSessionDtoToEntity(id, dto, user))
        return ResponseEntity.ok(converter.mapSessionToDto(updatedSession, user))
    }


    @GetMapping("list")
    fun fetchMaps(): ResponseEntity<List<MapSessionDto>> {
        return ResponseEntity.ok(
            sessionService.getPublicSessions(MapSession.State.LOBBY).map { converter.mapSessionToDto(it) })
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/leave")
    fun leaveSession(@PathVariable id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.state != MapSession.State.LOBBY) {
            return ResponseEntity.status(403).body(MessageResponse("session_already_started_or_finished"))
        }

        val resSession = if (session.creator.id == user.id) {
            sessionService.endSession(session.id ?: error("no_such_session")) ?: return ResponseEntity.status(403)
                .body(MessageResponse("could_not_end_session"))
        } else if (session.users.any { it.user == user }) {
            sessionService.leaveSession(id, user) ?: return ResponseEntity.status(403)
                .body(MessageResponse("not_in_session"))
        } else {
            return ResponseEntity.status(403).body(MessageResponse("not_in_session"))
        }


        return ResponseEntity.ok(converter.mapSessionToDto(resSession, user))
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/sendMessage")
    fun sendMessage(
        @PathVariable id: Long,
        @RequestBody message: NewSessionChatMessageDto
    ): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id && !session.users.any { it.user == user }) return ResponseEntity.status(403)
            .body(MessageResponse("not_in_session"))

        val newMessage = SessionChatMessage(creator = user, text = message.text)
        session.messages.add(newMessage)
        val messages = sessionService.updateSession(session).messages.map { converter.sessionChatMessageToDto(it) }
        return ResponseEntity.ok(messages)
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/markTask/{taskId}")
    fun markTask(
        @PathVariable id: Long,
        @PathVariable taskId: Long,
        @RequestParam state: Boolean
    ): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id && !session.users.any { it.user == user }) return ResponseEntity.status(403)
            .body(MessageResponse("not_in_session"))
        if (session.state != MapSession.State.STARTED) {
            return ResponseEntity.status(403).body(MessageResponse("session_not_started"))
        }
        val newSession = sessionService.markTask(
            id,
            taskId,
            user.id ?: error("not full user"),
            state
        )
        if (newSession == null) return ResponseEntity.status(403).body(MessageResponse("cant_mark"))
        val converted = converter.mapSessionToDto(newSession, user)
        return ResponseEntity.ok(converted)
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{id}/tasks/{taskId}/answerChallenges")
    fun answerChallenges(
        @PathVariable id: Long,
        @PathVariable taskId: Long,
        @RequestBody answers: Map<Long, String>,
    ): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id && !session.users.any { it.user == user }) return ResponseEntity.status(403)
            .body(MessageResponse("not_in_session"))
        if (session.state != MapSession.State.STARTED) {
            return ResponseEntity.status(403).body(MessageResponse("session_not_started"))
        }
        val isSuccessfulAnswer = sessionService.answerChallenges(
            sessionId = id,
            taskId = taskId,
            userId = user.id ?: error("not full user"),
            answers = answers,
        )

        return ResponseEntity.ok(AnswerResultDto(isSuccessfulAnswer))
    }

    private fun changeSessionState(start: Boolean, id: Long): ResponseEntity<Any> {
        val user = getCurrentUser() ?: return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        val session = sessionService.getById(id) ?: return ResponseEntity.status(404)
            .body(MessageResponse("such_session_doesnt_exist"))
        if (session.creator.id != user.id) return ResponseEntity.status(401).body(MessageResponse("not_authorized"))

        val resSession = if (start) {
            sessionService.startSession(session.id ?: -1)
        } else {
            sessionService.endSession(session.id ?: -1)
        } ?: return ResponseEntity.status(403).body(MessageResponse("cant_start_or_end_session"))

        return ResponseEntity.ok(converter.mapSessionToDto(resSession, user))
    }

    private fun getCurrentUser(): User? {
        return userService.getUserById(
            SecurityContextHolder.getContext().currentUserIdOrNull() ?: -1
        )
    }
}
