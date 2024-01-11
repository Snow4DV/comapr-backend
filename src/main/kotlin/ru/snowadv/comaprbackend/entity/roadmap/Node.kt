package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL


@Entity
@Table(name = "nodes")
class Node( // stores a bunch of tasks + description for them
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Size(max = 100)
    var name: String,

    @Size(max = 200)
    var description: String?,

    @OneToMany(cascade = [CascadeType.PERSIST])
    @OrderBy("id")
    val tasks: MutableList<Task> = mutableListOf()
)