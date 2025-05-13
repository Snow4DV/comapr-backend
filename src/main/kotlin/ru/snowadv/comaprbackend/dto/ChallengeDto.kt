package ru.snowadv.comaprbackend.dto

data class ChallengeDto(
    val id: Long?,
    val description: String,
    val answers: List<String>,
    val rightAnswer: String?,
)
