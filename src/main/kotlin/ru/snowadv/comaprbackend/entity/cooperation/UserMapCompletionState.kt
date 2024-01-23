package ru.snowadv.comaprbackend.entity.cooperation

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User

@Entity
@Table(name = "map_states")
class UserMapCompletionState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    var user: User,

    @OneToMany(cascade = [CascadeType.ALL])
    val tasksStates: MutableSet<UserTaskCompletionState> = mutableSetOf()




)