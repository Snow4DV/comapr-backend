package ru.snowadv.comaprbackend.entity.cooperation

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL
import org.springframework.data.annotation.CreatedDate
import ru.snowadv.comaprbackend.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "map_sessions")
class MapSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val creator: User,

    @ManyToMany
    val users: List<UserMapCompletionState>,

    var public: Boolean = false,

    var startDate: LocalDateTime, // can be edited any time by creator. Is used just to show when creator intents to start the study session

    @Enumerated(EnumType.STRING)
    var state: State,

    var groupChatUrl: String?,

    @OneToMany
    val messages: MutableList<SessionChatMessage> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at")
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    enum class State {
        LOBBY, STARTED, FINISHED
    }
}