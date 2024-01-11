package ru.snowadv.comaprbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node
import ru.snowadv.comaprbackend.entity.roadmap.Task


@Repository
interface TaskRepository : JpaRepository<Task?, Long?> {
}
