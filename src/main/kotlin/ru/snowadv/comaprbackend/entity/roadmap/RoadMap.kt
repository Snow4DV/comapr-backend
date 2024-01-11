package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User


@Entity
@Table(name = "maps")
class RoadMap( // stores nodes that store tasks
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Size(max = 100)
    val name: String,

    @Size(max = 200)
    val description: String, // markdown

    @ManyToOne
    val creator: User,

    @Enumerated(EnumType.STRING)
    var status: VerificationStatus,

    @ManyToOne
    var category: Category,

    @OneToMany(cascade = [CascadeType.PERSIST])
    @OrderBy("id")
    var nodes: MutableList<Node> = mutableListOf()
)