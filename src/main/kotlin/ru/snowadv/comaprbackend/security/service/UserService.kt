package ru.snowadv.comaprbackend.security.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.repository.UserRepository


@Service
class UserService(
    val userRepository: UserRepository
) : UserDetailsService {


    fun getUserById(id: Long): User? {
        return  userRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not Found with username: $username")
        return UserDetailsImpl.build(user)
    }
}
