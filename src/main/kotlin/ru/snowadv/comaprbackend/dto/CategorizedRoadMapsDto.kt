package ru.snowadv.comaprbackend.dto

data class CategorizedRoadMapsDto(
    val categoryName: String,
    val categoryId: Long,
    val roadMaps: MutableList<RoadMapDto> = mutableListOf()
)