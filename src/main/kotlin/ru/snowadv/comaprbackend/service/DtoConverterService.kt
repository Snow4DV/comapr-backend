package ru.snowadv.comaprbackend.service

import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.dto.*
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.cooperation.SessionChatMessage
import ru.snowadv.comaprbackend.entity.cooperation.UserMapCompletionState
import ru.snowadv.comaprbackend.entity.cooperation.UserTaskCompletionState
import ru.snowadv.comaprbackend.entity.roadmap.*
import ru.snowadv.comaprbackend.exception.NoSuchEntityException
import ru.snowadv.comaprbackend.security.service.UserService
import java.time.LocalDateTime
import java.util.HashMap
import java.util.TreeMap


@Service
class DtoConverterService(
    private val roadMapService: RoadMapService,
    private val voteService: VoteService,
    private val categoryService: CategoryService,
    private val sessionService: SessionService,
    private val userService: UserService,
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
        val likes = votes.filter { it.liked }.count()
        val dislikes = votes.size - likes
        return roadMap.run {
            RoadMapDto(
                id,
                name,
                description,
                category.id ?: throw IllegalStateException("category with no id"),
                nodes.map { nodeToDto(it) },
                likes,
                dislikes,
                category.name,
                roadMap.status.id
            )
        }
    }

    fun roadMapToSimpleDto(roadmap: RoadMap): SimpleRoadMapDto? {
        return roadmap.id?.let { SimpleRoadMapDto(it, roadmap.name) }
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

    fun taskDtoToEntity(dto: TaskDto, creator: User? = null): Task {
        return dto.run { Task(id, name, description, creator ?: roadMapService.getCreatorForMapId(id ?: -1), url) }
    }

    fun nodeDtoToEntity(dto: NodeDto, creator: User? = null): Node {
        return dto.run {
            Node(
                id,
                name,
                description,
                creator ?: roadMapService.getCreatorForNodeId(id ?: -1),
                tasks.map { taskDtoToEntity(it, creator) }.toMutableList()
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
                id?.let { roadMapService.getStatusFormapId(it) } ?: RoadMap.VerificationStatus.UNVERIFIED,
                categoryService.getCategoryById(categoryId) ?: error("no category with id $categoryId"),
                nodes.map { nodeDtoToEntity(it, creator) }.toMutableList()
            )
        }
    }


    fun newRoadMapDtoToEntity(dto: RoadMapDto, creator: User? = null): RoadMap {
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
        return userTaskState.run { UserTaskCompletionStateDto(id, task.id ?: error("not persisted entity"), state) }
    }

    fun userMapCompletionStateToDto(userMapState: UserMapCompletionState): UserMapCompletionStateDto {
        return userMapState.run {
            UserMapCompletionStateDto(
                id,
                userToDto(user),
                tasksStates.map { userTaskCompletionStateToDto(it) }.toMutableList()
            )
        }
    }

    fun userTaskCompletionStateDtoToEntity(dto: UserTaskCompletionStateDto): UserTaskCompletionState {
        return dto.run {
            UserTaskCompletionState(
                id,
                roadMapService.getTaskById(taskId) ?: error("no task with id $taskId"),
                state
            )
        }
    }

    fun userMapCompletionStateDtoToEntity(dto: UserMapCompletionStateDto): UserMapCompletionState {
        return dto.run {
            UserMapCompletionState(
                id,
                userDtoToEntity(user),
                tasksStates.map { userTaskCompletionStateDtoToEntity(it) }.toMutableSet()
            )
        }
    }

    fun sessionChatMessageToDto(message: SessionChatMessage): SessionChatMessageDto {
        return message.run { SessionChatMessageDto(id, userToDto(creator), timestamp, text) }
    }

    fun sessionChatMessageDtoToEntity(dto: SessionChatMessageDto, overrideDate: Boolean = true): SessionChatMessage {
        return dto.run {
            SessionChatMessage(
                id,
                userDtoToEntity(creator),
                if (!overrideDate && timestamp != null) timestamp else LocalDateTime.now(),
                text
            )
        }
    }

    fun mapSessionToDto(session: MapSession, currentUser: User? = null): MapSessionDto {
        return session.run {
            MapSessionDto(
                id,
                userToDto(creator),
                users.map { userMapCompletionStateToDto(it) },
                public,
                startDate,
                state.id,
                groupChatUrl,
                messages.map { sessionChatMessageToDto(it) },
                roadMapToDto(session.roadMap),
                users.any { it.user.id != null && it.user.id == currentUser?.id },
                (currentUser != null && currentUser.id == creator.id),
                users.firstOrNull { it.id != null && it.id == currentUser?.id }?.tasksStates?.filter { it.state }
                    ?.mapNotNull { it.task.id } ?: emptyList()
            )
        }
    }

    fun mapSessionDtoToEntity(dto: MapSessionDto, newCreator: User? = null): MapSession {
        return dto.run {
            MapSession(
                id,
                newCreator ?: userDtoToEntity(creator ?: error("user not set in dto!")),
                users.map { userMapCompletionStateDtoToEntity(it) }.toMutableList(),
                public,
                startDate,
                MapSession.State.getById(stateId),
                groupChatUrl,
                messages.map { sessionChatMessageDtoToEntity(it) }.toMutableList(),
                roadMap = roadMapDtoToEntity(dto.roadMap, newCreator)
            )
        }
    }

    fun createSessionDtoToEntity(dto: ClearMapSessionDto, creator: User): MapSession {
        return dto.run {
            MapSession(
                null,
                creator,
                mutableListOf(),
                public,
                startDate,
                MapSession.State.LOBBY,
                groupChatUrl,
                mutableListOf(),
                roadMap = roadMapService.getRoadMapById(dto.roadMapId) ?: error("no_such_roadmap")
            )
        }
    }

    fun updateSessionDtoToEntity(id: Long, dto: ClearMapSessionDto, creator: User): MapSession {
        return dto.run {
            val prevSession = sessionService.getById(id) ?: throw NoSuchEntityException("session", id)
            prevSession.public = public
            prevSession.groupChatUrl = groupChatUrl
            prevSession.startDate = startDate
            prevSession
        }
    }

    fun roadMapDtoListToCategorizedRoadMapsDtoList(maps: List<RoadMapDto>): List<CategorizedRoadMapsDto> {
        val hashMap = HashMap<Long, CategorizedRoadMapsDto>()
        maps.forEach { roadMap ->
            if(hashMap[roadMap.categoryId] == null) {
                hashMap[roadMap.categoryId] = CategorizedRoadMapsDto(roadMap.categoryName, roadMap.categoryId)
            }
            hashMap[roadMap.categoryId]?.roadMaps?.add(roadMap)
        }

        return hashMap.values.sortedBy { it.categoryName }

    }
}