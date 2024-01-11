package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL
import ru.snowadv.comaprbackend.entity.User
import java.time.LocalDateTime

class MapSessionDto(
    val id: Long? = null,
    val creator: UserDto,
    val users: List<UserMapCompletionStateDto>,
    val public: Boolean = false,
    val startDate: LocalDateTime,
    val state: String, // State.class
    val groupChatUrl: String?,
    val messages: List<SessionChatMessageDto> = listOf()
)