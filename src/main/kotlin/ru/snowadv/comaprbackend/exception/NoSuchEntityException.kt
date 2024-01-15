package ru.snowadv.comaprbackend.exception

class NoSuchEntityException(entityName: String, id: Long?): Exception("Such entity \"$entityName\" with id $id not found") {
}