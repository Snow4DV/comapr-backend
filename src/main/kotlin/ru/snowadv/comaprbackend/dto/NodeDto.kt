package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.roadmap.Task

data class NodeDto(
    val id: Long?,
    val name: String,
    var description: String?,
    val tasks: List<TaskDto> = listOf()
)