package ru.snowadv.comaprbackend.dto

data class TaskDto(
    val id: Long?,
    val name: String,
    val description: String?,
    val url: String?,
    val finishedUserIds: List<Long>? = null,
    val challenges: List<ChallengeDto>,
)