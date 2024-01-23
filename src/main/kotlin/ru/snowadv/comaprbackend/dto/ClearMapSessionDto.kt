package ru.snowadv.comaprbackend.dto

import java.time.LocalDateTime

class ClearMapSessionDto(
    val public: Boolean = false,
    val startDate: LocalDateTime,
    val groupChatUrl: String?,
    val messages: List<SessionChatMessageDto> = listOf(),
    val roadMapId: Long
)