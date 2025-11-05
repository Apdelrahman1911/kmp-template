package me.onvo.onvo.domain.repository

import me.onvo.onvo.domain.model.Source

interface ItemsRepository {
    suspend fun getItems(): List<Source>
}