package ru.snowadv.comaprbackend.utils

import ru.snowadv.comaprbackend.dto.*
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.cooperation.SessionChatMessage
import ru.snowadv.comaprbackend.entity.cooperation.UserMapCompletionState
import ru.snowadv.comaprbackend.entity.cooperation.UserTaskCompletionState
import ru.snowadv.comaprbackend.entity.roadmap.*


fun User.toDto(): UserDto {
    return UserDto(id, username, email, roles.firstOrNull()?.name?.name ?: "No role")
}

fun Task.toDto(): TaskDto {
    return TaskDto(id, name, description, url)
}

fun Node.toDto(): NodeDto {
    return NodeDto(id, name, description,  tasks.map { it.toDto() })
}


fun RoadMap.toDto(votes: List<Vote>? = null): RoadMapDto {
    val likes = votes?.count { it.like }
    val dislikes = votes?.let { it.size - (likes ?: 0)}
    return RoadMapDto(id, name, description, creator.toDto(), status.id, category.toDto(), nodes.map { it.toDto() }, likes, dislikes)
}

fun RoadMap.toDto(likes: Int, dislikes: Int): RoadMapDto {
    return RoadMapDto(id, name, description, creator.toDto(), status.id, category.toDto(), nodes.map { it.toDto() }, likes, dislikes)
}

fun Category.toDto(): CategoryDto {
    return CategoryDto(id, name)
}

fun CategoryDto.toEntity(): Category {
    return Category(id, name, mutableListOf())
}

fun  UserDto.toEntity(): User {
    return User(username, email, "")
}

fun TaskDto.toEntity(): Task {
    return Task(id, name, description, url)
}

fun NodeDto.toEntity(): Node {
    return Node(id, name, description, tasks.map { it.toEntity() }.toMutableList())
}

fun RoadMapDto.toEntity(): RoadMap {
    val status = RoadMap.VerificationStatus.entries.firstOrNull { it.id == statusId }
        ?: throw error("Invalid dto with status id $statusId that doesn't exist")
    return RoadMap(id, name, description, creator.toEntity(), status, category.toEntity(), nodes.map { it.toEntity() }.toMutableList())
}

fun UserTaskCompletionState.toDto(): UserTaskCompletionStateDto {
    return UserTaskCompletionStateDto(id, task.toDto(), state)
}
fun UserMapCompletionState.toDto(): UserMapCompletionStateDto {
    return UserMapCompletionStateDto(id, user.toDto(), name, tasksStates.map { it.toDto() }.toMutableList())
}

fun UserTaskCompletionStateDto.toEntity(): UserTaskCompletionState {
    return UserTaskCompletionState(id, task.toEntity(), state)
}

fun UserMapCompletionStateDto.toEntity(): UserMapCompletionState {
    return UserMapCompletionState(id, user.toEntity(), name, tasksStates.map { it.toEntity() }.toMutableList())
}

fun SessionChatMessage.toDto(): SessionChatMessageDto {
    return SessionChatMessageDto(id, creator.toDto(), timestamp, text)
}

fun SessionChatMessageDto.toEntity(): SessionChatMessage {
    return SessionChatMessage(id, creator.toEntity(), timestamp, text)
}

fun MapSession.toDto(): MapSessionDto {
    return MapSessionDto(id, creator.toDto(), users.map { it.toDto() }, public, startDate, state.name, groupChatUrl, messages.map { it.toDto() })
}

fun MapSessionDto.toEntity(): MapSession {
    return MapSession(
        id, creator.toEntity(), users.map { it.toEntity() }.toMutableList(), public, startDate,
        MapSession.State.valueOf(state), groupChatUrl, messages.map { it.toEntity() }.toMutableList()
    )
}