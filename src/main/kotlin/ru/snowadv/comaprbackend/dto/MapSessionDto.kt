package ru.snowadv.comaprbackend.dto

import java.time.LocalDateTime

data class MapSessionDto(
    val id: Long? = null,
    val creator: UserDto,
    val users: List<UserMapCompletionStateDto>,
    val public: Boolean = false,
    val startDate: LocalDateTime,
    val stateId: Int, // State.class
    val groupChatUrl: String?,
    val messages: List<SessionChatMessageDto> = listOf(),
    val roadMap: RoadMapDto,
    val joined: Boolean = false
)