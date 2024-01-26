package ru.snowadv.comaprbackend.dto

data class UserAndSessionsDto(
    val user: UserDto,
    val sessions: List<MapSessionDto>
)