package me.onvo.onvo.data.network

import me.onvo.onvo.domain.model.Source


// commonMain
interface ApiService {
    suspend fun fetchItems(): List<Source>
}