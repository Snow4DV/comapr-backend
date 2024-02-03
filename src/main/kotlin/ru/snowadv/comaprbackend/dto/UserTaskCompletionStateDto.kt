package ru.snowadv.comaprbackend.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.roadmap.Task


data class UserTaskCompletionStateDto(
    var id: Long? = null,
    val taskId: Long,
    var state: Boolean = false
)