package me.onvo.onvo.domain.usecase

//import dev.zacsweers.metro.Inject
import me.onvo.onvo.domain.model.Source
import me.onvo.onvo.domain.repository.ItemsRepository

//@Inject
class GetSourcesUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(): Result<List<Source>> {
        return try {
            val sources = repository.getItems()
            Result.success(sources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}