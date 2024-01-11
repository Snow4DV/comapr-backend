package ru.snowadv.comaprbackend.entity.cooperation

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User

@Entity
@Table(name = "map_sessions")
class MapSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val creator: User,

    @ManyToMany
    val joinedUsers: List<User>
)