package ru.snowadv.comaprbackend.entity

import jakarta.persistence.*


@Entity
@Table(name = "roles")
class Role() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    lateinit var name: ERole

    constructor(name: ERole): this() {
        this.name = name
    }
}