package ru.snowadv.comaprbackend.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("username")), UniqueConstraint(columnNames = arrayOf("email"))]
)
class User() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotBlank
    @Size(max = 20)
    lateinit var username: String

    @NotBlank
    @Size(max = 50)
    @Email
    lateinit var email: String

    @NotBlank
    @JsonIgnore
    @Size(max = 120)
    lateinit var password: String

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = HashSet()


    constructor(username: String, email: String, password: String): this() {
        this.username = username
        this.email = email
        this.password = password
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}