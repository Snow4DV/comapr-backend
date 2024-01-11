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
)