package ru.snowadv.comaprbackend.service

import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.dto.*
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.cooperation.SessionChatMessage
import ru.snowadv.comaprbackend.entity.cooperation.UserMapCompletionState
import ru.snowadv.comaprbackend.entity.cooperation.UserTaskCompletionState
import ru.snowadv.comaprbackend.entity.roadmap.*


@Service
class DtoConverterService(
    val roadMapService: RoadMapService,
    val voteService: VoteService,
    val categoryService: CategoryService
) {
    fun userToDto(user: User): UserDto {
        return user.run { UserDto(id, username, email, roles.firstOrNull()?.name?.name ?: "No role") }
    }

    fun taskToDto(task: Task): TaskDto {
        return task.run { TaskDto(id, name, description, url) }
    }

    fun nodeToDto(node: Node): NodeDto {
        return node.run { NodeDto(id, name, description, tasks.map { taskToDto(it) }) }
    }


    fun roadMapToDto(roadMap: RoadMap): RoadMapDto {
        val votes = voteService.getVotesForRoadMap(roadMap.id ?: error("roadMap id is null"))
        val likes = votes.filter { it.like }.count()
        val dislikes = votes.size - likes
        return roadMap.run {
            RoadMapDto(
                id,
                name,
                description,
                category.id ?: throw IllegalStateException("category with no id"),
                nodes.map { nodeToDto(it) },
                likes,
                dislikes
            )
        }
    }

    fun categoryToDto(category: Category): CategoryDto {
        return category.run { CategoryDto(id, name) }
    }

    fun categoryDtoToEntity(dto: CategoryDto): Category {
        return dto.run { Category(id, name, mutableListOf()) }
    }

    fun userDtoToEntity(dto: UserDto): User {
        return dto.run { User(username, email, "") }
    }

    fun taskDtoToEntity(dto: TaskDto): Task {
        return dto.run { Task(id, name, description, roadMapService.getCreatorForMapId(id ?: -1), url) }
    }

    fun nodeDtoToEntity(dto: NodeDto): Node {
        return dto.run {
            Node(
                id,
                name,
                description,
                roadMapService.getCreatorForNodeId(id ?: -1),
                tasks.map { taskDtoToEntity(it) }.toMutableList()
            )
        }
    }

    fun roadMapDtoToEntity(dto: RoadMapDto, creator: User? = null): RoadMap {
        return dto.run {
            RoadMap(
                id,
                name,
                description,
                creator ?: roadMapService.getCreatorForMapId(id ?: error("this roadmap doesn't have id")),
                roadMapService.getStatusFormapId(id ?: error("this roadmap doesn't have id")),
                categoryService.getCategoryById(categoryId) ?: error("no category with id $categoryId"),
                nodes.map { nodeDtoToEntity(it) }.toMutableList()
            )
        }
    }

    fun userTaskCompletionStateToDto(userTaskState: UserTaskCompletionState): UserTaskCompletionStateDto {
        return userTaskState.run { UserTaskCompletionStateDto(id, taskToDto(task), state) }
    }

    fun userMapCompletionStateToDto(userMapState: UserMapCompletionState): UserMapCompletionStateDto {
        return userMapState.run {
            UserMapCompletionStateDto(
                id,
                userToDto(user),
                name,
                tasksStates.map { userTaskCompletionStateToDto(it) }.toMutableList()
            )
        }
    }

    fun userTaskCompletionStateDtoToEntity(dto: UserTaskCompletionStateDto): UserTaskCompletionState {
        return dto.run { UserTaskCompletionState(id, taskDtoToEntity(task), state) }
    }

    fun userMapCompletionStateDtoToEntity(dto: UserMapCompletionStateDto): UserMapCompletionState {
        return dto.run {
            UserMapCompletionState(
                id,
                userDtoToEntity(user),
                name,
                tasksStates.map { userTaskCompletionStateDtoToEntity(it) }.toMutableList()
            )
        }
    }

    fun sessionChatMessageToDto(message: SessionChatMessage): SessionChatMessageDto {
        return message.run { SessionChatMessageDto(id, userToDto(creator), timestamp, text) }
    }

    fun sessionChatMessageDtoToEntity(dto: SessionChatMessageDto): SessionChatMessage {
        return dto.run { SessionChatMessage(id, userDtoToEntity(creator), timestamp, text) }
    }

    fun mapSessionToDto(session: MapSession): MapSessionDto {
        return session.run { MapSessionDto(id,
            userToDto(creator),
            users.map { userMapCompletionStateToDto(it) },
            public,
            startDate,
            state.name,
            groupChatUrl,
            messages.map { sessionChatMessageToDto(it) }) }
    }

    fun mapSessionDtoToEntity(dto: MapSessionDto): MapSession {
        return dto.run {
            MapSession(
                id,
                userDtoToEntity(creator),
                users.map { userMapCompletionStateDtoToEntity(it) }.toMutableList(),
                public,
                startDate,
                MapSession.State.valueOf(state),
                groupChatUrl,
                messages.map { sessionChatMessageDtoToEntity(it) }.toMutableList()
            )
        }
    }
}