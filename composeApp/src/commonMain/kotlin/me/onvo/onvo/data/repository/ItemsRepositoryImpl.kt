package me.onvo.onvo.data.repository

//import dev.zacsweers.metro.AppScope
//import dev.zacsweers.metro.ContributesBinding
//import dev.zacsweers.metro.Inject
//import dev.zacsweers.metro.SingleIn
//import kotlinx.serialization.json.Json
import me.onvo.onvo.data.network.ApiService
import me.onvo.onvo.domain.model.Source
import me.onvo.onvo.domain.repository.ItemsRepository


//@Inject
//@SingleIn(AppScope::class)
//@ContributesBinding(AppScope::class)
class ItemsRepositoryImpl(
    private val api: ApiService,

) : ItemsRepository {
    override suspend fun getItems(): List<Source> = api.fetchItems()
}
