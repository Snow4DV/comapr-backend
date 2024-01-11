package ru.snowadv.comaprbackend.entity.userdata

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Task

class RoomUserStats( // wip
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @ManyToOne
    val user: User,

    @ManyToMany
    val finishedTasks: List<Task>

    // should containt room


)