package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.*
import ru.snowadv.comaprbackend.entity.User


@Entity
@Table(name = "votes")
class Vote( // vote by user for roadmap
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val roadmap: RoadMap,

    @ManyToOne
    val voter: User,

    var like: Boolean
)