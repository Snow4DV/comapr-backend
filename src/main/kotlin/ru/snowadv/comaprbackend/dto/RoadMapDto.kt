package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node

class RoadMapDto(
    val id: Long?,
    val name: String,
    val description: String,
    val creator: UserDto,
    val statusId: Int,
    var category: CategoryDto, // Category.class
    var nodes: List<NodeDto> = mutableListOf(),
    val likes: Int? = null,
    val dislikes: Int? = null
)