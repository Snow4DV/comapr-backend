package ru.snowadv.comaprbackend.entity.roadmap

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "challenges")
class Challenge(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 200_000)
    var description: String,

    @ElementCollection
    var answers: List<String>,

    var rightAnswer: String?,
)
