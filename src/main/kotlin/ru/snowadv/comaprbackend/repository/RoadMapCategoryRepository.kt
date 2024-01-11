package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.roadmap.Category


@Repository
interface RoadMapCategoryRepository : JpaRepository<Category?, Long?> {
    fun findAllByOrderByName(): List<Category>
}
