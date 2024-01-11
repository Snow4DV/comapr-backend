package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node
import ru.snowadv.comaprbackend.entity.roadmap.VerificationStatus

class RoadMapDto(
    val id: Long?,
    val name: String,
    val description: String,
    val creator: UserDto,
    val status: String, // VerificationStatus.class
    var category: CategoryDto, // Category.class
    var nodes: List<NodeDto> = mutableListOf()
)