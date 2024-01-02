package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.User


@Repository
interface UserRepository : JpaRepository<User?, Long?> {
    fun findByUsername(username: String?): User?

    fun existsByUsername(username: String?): Boolean

    fun existsByEmail(email: String?): Boolean
}