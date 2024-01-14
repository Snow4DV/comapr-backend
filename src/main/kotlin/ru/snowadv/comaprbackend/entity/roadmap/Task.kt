package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.*
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Immutable
import org.hibernate.validator.constraints.URL
import ru.snowadv.comaprbackend.entity.User


@Entity
@Table(name = "tasks")
class Task( // stores a single task
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Size(max = 100)
    var name: String,

    @Size(max = 200)
    var description: String?,

    @ManyToOne
    @Immutable
    val creator: User,

    @URL
    @Size(max = 150)
    var url: String?
)