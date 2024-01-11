package ru.snowadv.comaprbackend.dto

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node
import ru.snowadv.comaprbackend.entity.roadmap.VerificationStatus

class CategoryDto(
    val id: Long?,
    val name: String
)