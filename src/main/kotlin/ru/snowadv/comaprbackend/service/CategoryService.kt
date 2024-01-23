package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.roadmap.*
import ru.snowadv.comaprbackend.exception.DuplicateException
import ru.snowadv.comaprbackend.exception.NoSuchEntityException
import ru.snowadv.comaprbackend.repository.CategoryRepository


@Service
class CategoryService(val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<Category> {
        return categoryRepository.findAllBy()
    }
    fun createNewCategory(name: String): Boolean {
        if (categoryRepository.findByName(name) != null) {
            return false
        }

        categoryRepository.save(Category(name = name))
        return true
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