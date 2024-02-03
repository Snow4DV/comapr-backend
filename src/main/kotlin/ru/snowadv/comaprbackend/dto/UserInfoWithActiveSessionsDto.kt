package ru.snowadv.comaprbackend.dto

import ru.snowadv.comaprbackend.payload.response.JwtResponse

data class UserInfoWithActiveSessionsDto(
    val authData: JwtResponse,
    val activeSessions: List<MapSessionDto>
) {

}