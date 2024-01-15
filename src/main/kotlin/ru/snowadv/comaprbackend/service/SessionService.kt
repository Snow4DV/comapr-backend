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
import ru.snowadv.comaprbackend.repository.RoadMapRepository
import ru.snowadv.comaprbackend.repository.SessionRepository
import ru.snowadv.comaprbackend.repository.VoteRepository
import ru.snowadv.comaprbackend.security.service.UserService


@Service
class SessionService(private val repo: SessionRepository, private val roadMapService: RoadMapService) {
    fun getById(id: Long): MapSession? {
        return repo.findByIdOrNull(id)
    }

    fun startSession(id: Long): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if(session.state != MapSession.State.LOBBY) return false
        session.state = MapSession.State.STARTED
        updateSession(session)
        return true
    }

    fun endSession(id: Long): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if(session.state != MapSession.State.STARTED) return false
        session.state = MapSession.State.STARTED
        updateSession(session)
        return true
    }

    fun joinSession(id: Long, user: User): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if(session.state != MapSession.State.LOBBY || session.users.any { it.id != null && it.id == user.id }) return false
        session.users.add(UserMapCompletionState(user = user))
        updateSession(session)
        return true
    }


    fun leaveSession(id: Long, user: User): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        if(!session.users.any { it.id == user.id }) return false
        session.users.removeIf { it.user == user }
        updateSession(session)
        return true
    }

    fun markTask(id: Long, taskId: Long, userId: Long, newState: Boolean): Boolean {
        val session = repo.findByIdOrNull(id) ?: throw NoSuchEntityException("session", id)
        val task = session.users.first { it.user.id == userId }.tasksStates.firstOrNull { it.task.id == taskId }
            ?: UserTaskCompletionState(task = roadMapService.getTaskById(taskId) ?: return false, state = newState)
        task.state = newState
        updateSession(session)
        return true
    }


    fun getCreatorById(id: Long): User? {
        return repo.findByIdOrNull(id)?.creator
    }

    fun createSession(session: MapSession) {
        if(repo.findByIdOrNull(session.id) != null) throw DuplicateException("session")
        repo.save(session)
    }

    fun updateSession(session: MapSession) {
        val initSession = repo.findByIdOrNull(session.id) ?: throw NoSuchEntityException("session", session.id)
        repo.save(session)
    }


    fun getPublicSessions(): List<MapSession> {
        return repo.findAllByPublicIsTrue()
    }
}