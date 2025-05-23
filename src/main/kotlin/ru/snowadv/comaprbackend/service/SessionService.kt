package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.cooperation.UserMapCompletionState
import ru.snowadv.comaprbackend.entity.cooperation.UserTaskCompletionState
import ru.snowadv.comaprbackend.entity.roadmap.*
import ru.snowadv.comaprbackend.exception.DuplicateException
import ru.snowadv.comaprbackend.exception.NoSuchEntityException
import ru.snowadv.comaprbackend.repository.ChallengeRepository
import ru.snowadv.comaprbackend.repository.RoadMapRepository
import ru.snowadv.comaprbackend.repository.SessionRepository
import ru.snowadv.comaprbackend.repository.TaskRepository
import ru.snowadv.comaprbackend.repository.VoteRepository
import ru.snowadv.comaprbackend.security.service.UserService


@Service
class SessionService(
    private val repo: SessionRepository,
    private val roadMapService: RoadMapService,
    private val challengeRepository: ChallengeRepository,
    private val taskRepository: TaskRepository,
) {

    fun answerChallenges(sessionId: Long, taskId: Long, userId: Long, answers: Map<Long, String>): Boolean {
        val task = taskRepository.findByIdOrNull(taskId) ?: return false

        val rightAnswersCount = answers.count { (challengeId, answer) ->
            challengeRepository.findByIdOrNull(challengeId)?.rightAnswer == answer
        }

        val wrongAnswersCount = task.challenges.size - rightAnswersCount

        val wasTaskFinished = rightAnswersCount >= wrongAnswersCount

        markTask(
            sessionId,
            taskId,
            userId,
            wasTaskFinished
        )

        return wasTaskFinished
    }

    fun markTask(id: Long, taskId: Long, userId: Long, newState: Boolean): MapSession? {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        val userState = session.users.first { it.user.id == userId }
        val userTasks = userState.tasksStates
        val task = userTasks.firstOrNull { it.task.id == taskId }
            ?: UserTaskCompletionState(task = roadMapService.getTaskById(taskId) ?: return null, state = newState)
        task.state = newState

        userTasks.add(task)
        return updateSession(session)
    }

    fun getById(id: Long): MapSession? {
        return repo.findByIdOrNull(id)
    }

    fun startSession(id: Long): MapSession? {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if (session.state != MapSession.State.LOBBY) return null
        session.state = MapSession.State.STARTED
        return updateSession(session)
    }

    fun endSession(id: Long): MapSession? {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        session.state = MapSession.State.FINISHED
        return updateSession(session)
    }

    fun joinSession(id: Long, user: User): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if (session.state != MapSession.State.LOBBY || session.users.any { it.id != null && it.id == user.id }) return false
        session.users.add(UserMapCompletionState(user = user))
        updateSession(session)
        return true
    }


    fun leaveSession(id: Long, user: User): MapSession? {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if (!session.users.any { it.user.id == user.id }) return null
        session.users.removeIf { it.user == user }
        return updateSession(session)
    }




    fun getCreatorById(id: Long): User? {
        return repo.findByIdOrNull(id)?.creator
    }

    fun createSession(session: MapSession): MapSession {
        if (session.id != null && repo.findByIdOrNull(session.id) != null) throw DuplicateException("session")
        session.users.add(
            UserMapCompletionState(
                id = null,
                user = session.creator
            )
        )
        return repo.save(session)
    }

    fun updateSession(session: MapSession): MapSession {
        val initSession = repo.findByIdOrNull(session.id) ?: throw NoSuchEntityException("session", session.id)
        return repo.save(session)
    }


    fun getPublicSessions(state: MapSession.State): List<MapSession> {
        return repo.findAllByPublicIsTrueAndStateIs(state = state)
    }


    fun getAllSessionsByUser(id: Long): List<MapSession> {
        return repo.findAllSessionsByUser(id)
    }

    fun getAllActiveSessionsByUser(id: Long): List<MapSession> {
        return repo.findAllActiveSessionsByUser(id)
    }

    fun getChallengesForTask(taskId: Long): List<Challenge>? {
        return taskRepository.findByIdOrNull(taskId)?.challenges
    }


}
