package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.support.SessionStatus
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node


@Repository
interface SessionRepository : JpaRepository<MapSession?, Long?> {
    fun findAllByPublicIsTrueAndStateIs(state: MapSession.State): List<MapSession>
}
