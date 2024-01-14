package ru.snowadv.comaprbackend.exception

class DuplicateException(entityName: String): Exception("Such entity \"$entityName\" already exists!") {
}