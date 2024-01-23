package ru.snowadv.comaprbackend.entity.cooperation

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import ru.snowadv.comaprbackend.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "session_messages")
class SessionChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val creator: User,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val text: String
)