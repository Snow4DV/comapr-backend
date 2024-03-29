package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node

data class RoadMapDto(
    val id: Long?,
    val name: String,
    val description: String,
    var categoryId: Long, // Category.class
    var nodes: List<NodeDto> = mutableListOf(),
    val likes: Int? = null,
    val dislikes: Int? = null,
    val categoryName: String = "",
    val statusId: Int = 0,
    val tasksCount: Int
)