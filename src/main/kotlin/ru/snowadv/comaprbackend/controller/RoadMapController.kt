package ru.snowadv.comaprbackend.controller


import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
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
import ru.snowadv.comaprbackend.service.RoadMapService
import java.util.function.Consumer
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/roadmap")
class RoadMapController(val roadMapService: RoadMapService) {

    @GetMapping
    fun fetchRoadMap(@RequestParam id: Long?): ResponseEntity<Any> {
        val map = roadMapService.getRoadMapById(id ?: -1)
        return map?.let { ResponseEntity.ok(it) } ?: run { ResponseEntity.badRequest().body(MessageResponse("map_not_found")) }
    }
    @GetMapping("list")
    fun fetchMaps(@RequestParam statusId: Int?, @RequestParam categoryId: Long?): ResponseEntity<List<RoadMap>> {
        val maps = roadMapService.getRoadMapsWithStatusAndOrCategory(statusId, categoryId)
        return ResponseEntity.ok(maps)
    }
}
