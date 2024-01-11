package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.RoadMap


@Repository
interface RoadMapRepository : JpaRepository<RoadMap?, Long?> {
    fun findAllByCategoryIdOrderByName(id: Long): List<RoadMap>
    fun findAllByStatusIsOrderByName(status: RoadMap.VerificationStatus): List<RoadMap>

    fun findAllByStatusIsAndCategoryIdOrderByName(status: RoadMap.VerificationStatus, categoryId: Long): List<RoadMap>
}
