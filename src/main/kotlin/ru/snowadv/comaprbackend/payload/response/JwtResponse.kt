package ru.snowadv.comaprbackend.payload.response



class JwtResponse(
    val accessToken: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
) {

    companion object {
        const val tokenType: String = "Bearer"
    }
}