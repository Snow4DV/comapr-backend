package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size



class UserDto(
    val id: Long?,
    val username: String,
    val email: String,
    val role: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDto

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}