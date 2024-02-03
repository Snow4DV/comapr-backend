package ru.snowadv.comaprbackend.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User

data class UserMapCompletionStateDto(
    var id: Long? = null,
    var user: UserDto,
    val tasksStates: MutableList<UserTaskCompletionStateDto> = mutableListOf()
)