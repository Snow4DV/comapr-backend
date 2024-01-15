package ru.snowadv.comaprbackend.controller


import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
import ru.snowadv.comaprbackend.service.DtoConverterService
import ru.snowadv.comaprbackend.service.RoadMapService
import ru.snowadv.comaprbackend.service.VoteService
import ru.snowadv.comaprbackend.utils.currentUserDetailsOrNull
import ru.snowadv.comaprbackend.utils.currentUserIdOrNull
import java.util.function.Consumer
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/roadmap")
class RoadMapController(
    private val roadMapService: RoadMapService, private val voteService: VoteService,
    private val userService: UserService, private val converter: DtoConverterService
) {


    @GetMapping
    fun fetchRoadMap(@RequestParam id: Long?): ResponseEntity<Any> {
        val map = converter.roadMapToDto(
            roadMapService.getRoadMapById(id ?: -1) ?: return ResponseEntity.badRequest()
                .body(MessageResponse("map_not_found"))
        )
        return ResponseEntity.ok(map)
    }


    @GetMapping("list")
    fun fetchMaps(@RequestParam statusId: Int?, @RequestParam categoryId: Long?): ResponseEntity<List<RoadMapDto>> {
        val maps = roadMapService.getRoadMapsWithStatusAndOrCategory(statusId, categoryId).map {
            converter.roadMapToDto(it)
        }
        return ResponseEntity.ok(maps)
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("vote")
    fun voteForRoadmap(@RequestParam id: Long, @RequestParam like: Boolean?): ResponseEntity<Any> {
        voteService.changeVoteToRoadMap(
            like, id,
            SecurityContextHolder.getContext().currentUserDetailsOrNull()?.id
                ?: return ResponseEntity.badRequest().build()
        )
        return ResponseEntity.ok(MessageResponse("success"))
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping("changeStatus")
    fun changeVerificationStatus(@RequestParam id: Long, statusId: Int): ResponseEntity<Any> {
        val status = RoadMap.VerificationStatus.fromId(statusId)
        val map =
            roadMapService.getRoadMapById(id) ?: return ResponseEntity.badRequest().body(MessageResponse("no_such_map"))
        map.status = status
        roadMapService.update(map)
        return ResponseEntity.ok(MessageResponse("success"))
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("update")
    fun updateRoadMap(@RequestBody dto: RoadMapDto): ResponseEntity<Any> {
        val map = converter.roadMapDtoToEntity(dto)
        if (map.deepCheckCreator(SecurityContextHolder.getContext().currentUserIdOrNull() ?: -1)) {
            return ResponseEntity.status(403).body(MessageResponse("no_permission_to_update"))
        }
        return if(!roadMapService.updateKeepCreatorAndStatus(map)) {
            ResponseEntity.status(403).body(MessageResponse("name_already_used"))
        } else {
            ResponseEntity.ok(MessageResponse("success"))
        }

    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("create")
    fun createRoadMap(@RequestBody dto: RoadMapDto): ResponseEntity<Any> {
        val map = converter.roadMapDtoToEntity(
            dto,
            userService.getUserById(
                SecurityContextHolder.getContext().currentUserIdOrNull() ?: return ResponseEntity.status(403)
                    .body(MessageResponse("not_authorized"))
            )
        )
        return if (!roadMapService.createNew(map)) {
            ResponseEntity.status(403).body(MessageResponse("name_already_used"))
        } else {
            ResponseEntity.ok(MessageResponse("success"))
        }

    }


}
