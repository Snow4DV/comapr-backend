package ru.snowadv.comaprbackend.controller


import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.snowadv.comaprbackend.dto.RoadMapDto
import ru.snowadv.comaprbackend.entity.ERole
import ru.snowadv.comaprbackend.entity.Role
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.RoadMap
import ru.snowadv.comaprbackend.payload.request.LoginRequest
import ru.snowadv.comaprbackend.payload.request.SignupRequest
import ru.snowadv.comaprbackend.payload.response.JwtResponse
import ru.snowadv.comaprbackend.payload.response.MessageResponse
import ru.snowadv.comaprbackend.repository.RoleRepository
import ru.snowadv.comaprbackend.repository.UserRepository
import ru.snowadv.comaprbackend.security.jwt.JwtUtils
import ru.snowadv.comaprbackend.security.service.UserDetailsImpl
import ru.snowadv.comaprbackend.security.service.UserService
import ru.snowadv.comaprbackend.service.RoadMapService
import ru.snowadv.comaprbackend.service.VoteService
import ru.snowadv.comaprbackend.utils.currentUserDetailsOrNull
import ru.snowadv.comaprbackend.utils.toDto
import java.util.function.Consumer
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/roadmap")
class RoadMapController(private val roadMapService: RoadMapService, private val voteService: VoteService,
    private val userService: UserService) {

    @GetMapping
    fun fetchRoadMap(@RequestParam id: Long?): ResponseEntity<Any> {
        val map = roadMapService.getRoadMapById(id ?: -1)?.toDto(voteService.getVotesForRoadMap(id ?: -1))
        return map?.let { ResponseEntity.ok(it) } ?: run { ResponseEntity.badRequest().body(MessageResponse("map_not_found")) }
    }

    @GetMapping("list")
    fun fetchMaps(@RequestParam statusId: Int?, @RequestParam categoryId: Long?): ResponseEntity<List<RoadMapDto>> {
        val maps = roadMapService.getRoadMapsWithStatusAndOrCategory(statusId, categoryId).map {
            it.toDto(voteService.getVotesForRoadMap(it.id ?: -1))
        }
        return ResponseEntity.ok(maps)
    }

    @PostMapping("vote")
    fun voteForRoadmap(@RequestParam id: Long, @RequestParam like: Boolean?): ResponseEntity<MessageResponse> {
        voteService.changeVoteToRoadMap(like, id,
            SecurityContextHolder.getContext().currentUserDetailsOrNull()?.id ?: error("user with id $id not found"))
        return ResponseEntity.ok(MessageResponse("Voted successfully"))
    }


}
