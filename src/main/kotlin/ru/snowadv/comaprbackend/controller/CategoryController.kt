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
import ru.snowadv.comaprbackend.dto.CategoryDto
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
import ru.snowadv.comaprbackend.service.CategoryService
import ru.snowadv.comaprbackend.service.DtoConverterService
import ru.snowadv.comaprbackend.service.RoadMapService
import ru.snowadv.comaprbackend.service.VoteService
import ru.snowadv.comaprbackend.utils.currentUserDetailsOrNull
import ru.snowadv.comaprbackend.utils.currentUserIdOrNull
import java.util.function.Consumer
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/category")
class CategoryController(
    private val categoryService: CategoryService
) {


    @GetMapping("list")
    fun getCategories(@RequestParam id: Long?): ResponseEntity<Any> {
        return ResponseEntity.ok(categoryService.getAllCategories())
    }

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<Any> {
        val category = categoryService.getCategoryById(id) ?: return ResponseEntity.status(403).body(MessageResponse("not_found"))
        return ResponseEntity.ok(category)
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping("create")
    fun createCategory(@RequestBody dto: CategoryDto): ResponseEntity<Any> {
        return if(categoryService.createNewCategory(dto.name)) {
            ResponseEntity.ok(MessageResponse("created"))
        } else {
            ResponseEntity.status(409).body(MessageResponse("already_exists"))
        }
    }



}
