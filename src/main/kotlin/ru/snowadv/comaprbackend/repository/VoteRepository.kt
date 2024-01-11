package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.roadmap.Vote


@Repository
interface VoteRepository : JpaRepository<Vote?, Long?> {
    fun findAllByRoadmapId(roadMapId: Long): List<Vote>


    fun findVoteByVoterIdAndRoadmapId(voterId: Long, mapId: Long): Vote?
    fun removeVoteByVoterIdAndRoadmapId(voterId: Long, mapId: Long): Boolean
}
