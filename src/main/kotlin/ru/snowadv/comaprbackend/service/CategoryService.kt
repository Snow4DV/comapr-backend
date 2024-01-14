package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.roadmap.*
import ru.snowadv.comaprbackend.exception.DuplicateException
import ru.snowadv.comaprbackend.exception.NoSuchEntityException
import ru.snowadv.comaprbackend.repository.CategoryRepository


@Service
class CategoryService(val categoryRepository: CategoryRepository) {

    fun createNewCategory(name: String) {
        if (categoryRepository.findByName(name) != null) {
            throw DuplicateException("category")
        }

        categoryRepository.save(Category(name = name))
    }

    fun updateCategory(id: Long, name: String) {
        val category = categoryRepository.findByIdOrNull(id) ?: throw NoSuchEntityException("category", id)
        category.name = name
        categoryRepository.save(category)
    }

    fun updateCategory(category: Category) {
        categoryRepository.findByIdOrNull(category.id ?: -1) ?: throw NoSuchEntityException(
            "category",
            category.id ?: -1L
        )
        categoryRepository.save(category)
    }

    fun removeCategory(id: Long) {
        categoryRepository.deleteById(id)
    }

    fun getCategoryById(id: Long): Category? {
        return categoryRepository.findByIdOrNull(id)
    }

}