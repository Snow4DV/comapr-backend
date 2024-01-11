package ru.snowadv.comaprbackend.entity.roadmap

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL


@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Size(max = 100)
    var name: String,


    @JsonIgnore
    @OneToMany
    val maps: MutableList<RoadMap> = mutableListOf()
)