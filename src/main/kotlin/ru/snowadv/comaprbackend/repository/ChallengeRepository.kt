package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Challenge
import ru.snowadv.comaprbackend.entity.roadmap.Node


@Repository
interface ChallengeRepository : JpaRepository<Challenge?, Long?>
