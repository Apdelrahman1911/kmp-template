package me.onvo.onvo.data.repository


import me.onvo.onvo.data.network.ApiService
import me.onvo.onvo.domain.model.Source
import me.onvo.onvo.domain.repository.ItemsRepository


class ItemsRepositoryImpl(
    private val api: ApiService,

) : ItemsRepository {
    override suspend fun getItems(): List<Source> = api.fetchItems()
}
