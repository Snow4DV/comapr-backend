package ru.snowadv.comaprbackend.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User

class UserMapCompletionStateDto(
    var id: Long? = null,
    var user: UserDto,
    var name: String,
    val tasksStates: MutableList<UserTaskCompletionStateDto> = mutableListOf()
)