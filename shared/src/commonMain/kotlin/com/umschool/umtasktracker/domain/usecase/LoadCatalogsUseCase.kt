package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.repository.CatalogRepository

// Загружает все каталоги параллельно (роли, предметы, отделы)
data class Catalogs(
    val roles: List<CatalogItem>,
    val subjects: List<CatalogItem>,
    val departments: List<CatalogItem>
)

class LoadCatalogsUseCase(private val repository: CatalogRepository) {

    suspend operator fun invoke(): Result<Catalogs> {
        val rolesResult = repository.getRoles()
        val subjectsResult = repository.getSubjects()
        val departmentsResult = repository.getDepartments()

        // Если хотя бы один каталог не загрузился — ошибка
        val roles = rolesResult.getOrElse { return Result.failure(it) }
        val subjects = subjectsResult.getOrElse { return Result.failure(it) }
        val departments = departmentsResult.getOrElse { return Result.failure(it) }

        return Result.success(Catalogs(roles, subjects, departments))
    }
}
