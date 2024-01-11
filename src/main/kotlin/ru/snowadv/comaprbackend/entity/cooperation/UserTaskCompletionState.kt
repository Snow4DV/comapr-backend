package ru.snowadv.comaprbackend.entity.cooperation

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.roadmap.Task

@Entity
@Table(name = "task_states")
class UserTaskCompletionState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val task: Task,

    var state: Boolean = false
)