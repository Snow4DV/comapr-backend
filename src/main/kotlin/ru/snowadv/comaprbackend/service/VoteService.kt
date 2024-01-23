package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.roadmap.*
import ru.snowadv.comaprbackend.repository.RoadMapRepository
import ru.snowadv.comaprbackend.repository.VoteRepository
import ru.snowadv.comaprbackend.security.service.UserService


@Service
class VoteService(private val voteRepo: VoteRepository, private val mapRepo: RoadMapRepository, private val userService: UserService) {

    fun changeVoteToRoadMap(like: Boolean?, roadMapId: Long, voterId: Long) {
        like?.let {
            val map = mapRepo.findByIdOrNull(roadMapId) ?: error("no_such_roadmap")
            val vote = voteRepo.findVoteByVoterIdAndRoadmapId(voterId, roadMapId)?.apply { liked = like }
                ?: Vote(null, map, userService.getUserById(voterId) ?: error("no_such_user"), like)
            voteRepo.save(vote)
        } ?: run {
            removeVote(roadMapId, voterId)
        }
    }

    fun removeVote(roadMapId: Long, voterId: Long) {
        voteRepo.removeVoteByVoterIdAndRoadmapId(voterId, roadMapId)
    }

    fun getVotesForRoadMap(roadMapId: Long): List<Vote> {
        return voteRepo.findAllByRoadmapId(roadMapId)
    }
}