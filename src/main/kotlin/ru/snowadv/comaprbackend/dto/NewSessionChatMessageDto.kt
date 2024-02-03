package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import java.time.LocalDateTime

data class NewSessionChatMessageDto(
    val text: String
)