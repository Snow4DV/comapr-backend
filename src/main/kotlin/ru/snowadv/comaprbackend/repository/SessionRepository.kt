package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.support.SessionStatus
import ru.snowadv.comaprbackend.entity.cooperation.MapSession
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node


@Repository
interface SessionRepository : JpaRepository<MapSession?, Long?> {
    fun findAllByPublicIsTrueAndStateIs(state: MapSession.State): List<MapSession>


    @Query(
        "SELECT ms.id, created_at, group_chat_url, public, start_date, state, creator_id, road_map_id, user_id " +
                "FROM map_sessions ms JOIN map_sessions_users mu ON ms.id = mu.map_session_id JOIN map_states mstates " +
                "ON mu.users_id = mstates.id WHERE user_id = ?1 ORDER BY state DESC",
        nativeQuery = true
    )
    fun findAllSessionsByUser(id: Long): List<MapSession>

    @Query(
        "SELECT ms.id, created_at, group_chat_url, public, start_date, state, creator_id, road_map_id, user_id " +
                "FROM map_sessions ms JOIN map_sessions_users mu ON ms.id = mu.map_session_id JOIN map_states mstates " +
                "ON mu.users_id = mstates.id WHERE user_id = ?1 AND state != 'FINISHED' ORDER BY created_at DESC",
        nativeQuery = true
    )
    fun findAllActiveSessionsByUser(id: Long): List<MapSession>


}
