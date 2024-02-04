package ru.snowadv.comaprbackend.dto

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class TaskDto(
    val id: Long?,
    val name: String,
    val description: String?,
    val url: String?,
    val finishedUserIds: List<Long>? = null
)