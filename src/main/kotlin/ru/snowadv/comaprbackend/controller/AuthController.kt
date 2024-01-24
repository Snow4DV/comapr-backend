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
import ru.snowadv.comaprbackend.entity.ERole
import ru.snowadv.comaprbackend.entity.Role
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.payload.request.LoginRequest
import ru.snowadv.comaprbackend.payload.request.SignupRequest
import ru.snowadv.comaprbackend.payload.response.JwtResponse
import ru.snowadv.comaprbackend.payload.response.MessageResponse
import ru.snowadv.comaprbackend.repository.RoleRepository
import ru.snowadv.comaprbackend.repository.UserRepository
import ru.snowadv.comaprbackend.security.jwt.JwtUtils
import ru.snowadv.comaprbackend.security.service.UserDetailsImpl
import ru.snowadv.comaprbackend.security.service.UserService
import ru.snowadv.comaprbackend.utils.currentUserDetailsOrNull
import ru.snowadv.comaprbackend.utils.currentUserIdOrNull
import java.util.function.Consumer
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authenticationManager: AuthenticationManager,
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val encoder: PasswordEncoder,
    val jwtUtils: JwtUtils,
    val userService: UserService
) {
    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: @Valid LoginRequest?): ResponseEntity<JwtResponse> {
        if(loginRequest == null) {
            return ResponseEntity.badRequest().build()
        }
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt: String = jwtUtils.generateJwtToken(authentication)

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val roles: List<String> = userDetails.authorities
            .map { item -> item.authority }

        return ResponseEntity.ok(
            JwtResponse(
                jwt,
                userDetails.id,
                userDetails.username,
                userDetails.email,
                roles
            )
        )
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest?): ResponseEntity<*> {
        if(signUpRequest == null) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Missing request body"))
        }
        if (userRepository.existsByUsername(signUpRequest.username)) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Error: Username is already taken!"))
        }

        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Error: Email is already in use!"))
        }

        val user: User = User(
            signUpRequest.username,
            signUpRequest.email,
            encoder.encode(signUpRequest.password)
        )

        val strRoles = listOf("user")
        val roles: MutableSet<Role> = HashSet()

        strRoles.forEach { role ->
            when (role) {
                "admin" -> {
                    val adminRole: Role = roleRepository.findByName(ERole.ROLE_ADMIN)
                        ?: throw RuntimeException("Error: Role is not found.")
                    roles.add(adminRole)
                }

                "mod" -> {
                    val modRole: Role = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        ?: throw RuntimeException("Error: Role is not found.")
                    roles.add(modRole)
                }

                else -> {
                    val userRole: Role = roleRepository.findByName(ERole.ROLE_USER)
                        ?: throw RuntimeException("Error: Role is not found.")
                    roles.add(userRole)
                }
            }
        }

        user.roles = roles
        val newUser = userRepository.save(user)

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(signUpRequest.username, signUpRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt: String = jwtUtils.generateJwtToken(authentication)


        return ResponseEntity.ok(
            JwtResponse(
                jwt,
                newUser.id!!,
                newUser.username,
                newUser.email,
                newUser.roles
                    .map { it.name.name }
            )
        )
    }


    @PostMapping("/authenticate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    fun checkAuth(@RequestHeader("Authorization") authorizationHeader: String?): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().currentUserIdOrNull()?.let { userService.getUserById(it) }
        val token = authorizationHeader?.let { if(it.length < 8) null else it.substring(7) }
        if(token == null || user == null) return ResponseEntity.status(401).body(MessageResponse("not_authorized"))
        return ResponseEntity.ok(JwtResponse(token, user.id ?: error("user has no id"), user.username, user.email, user.roles.map { it.name.name }))
    }

}
