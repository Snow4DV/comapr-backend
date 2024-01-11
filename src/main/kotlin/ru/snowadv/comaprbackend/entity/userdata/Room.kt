package ru.snowadv.comaprbackend.entity.userdata

import jakarta.persistence.*
import ru.snowadv.comaprbackend.entity.User

class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    @ManyToMany
    val userStats: List<RoomUserStats>,
    val creator: User,

    )


