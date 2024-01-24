package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Immutable
import org.springframework.data.annotation.CreatedDate
import ru.snowadv.comaprbackend.entity.User
import java.time.LocalDateTime


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
    @Immutable
    val creator: User,

    @Enumerated(EnumType.STRING)
    var status: VerificationStatus,

    @ManyToOne
    var category: Category,

    @OneToMany(cascade = [CascadeType.ALL])
    @OrderBy("id")
    var nodes: MutableList<Node> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at")
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    enum class VerificationStatus(val id: Int) {
        HIDDEN(0), UNVERIFIED(1), COMMUNITY_CHOICE(2), VERIFIED(3);
        companion object {
            fun fromId(id: Int): VerificationStatus {
                return entries.find { it.id == id } ?: error("no status with id $id")
            }
        }
    }


    fun deepCheckCreator(id: Long): Boolean {
        if(this.creator.id != id) {
            return false
        }

        for(node in nodes) {
            if(node.creator.id != id) return false
            for(task in node.tasks) {
                if(task.creator.id != id) return false
            }
        }

        return true
    }
}