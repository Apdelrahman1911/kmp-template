package me.onvo.onvo.data.network

import me.onvo.onvo.domain.model.CheckInputResponse
import me.onvo.onvo.domain.model.LoginRequest
import me.onvo.onvo.domain.model.LoginResponse
import me.onvo.onvo.domain.model.TokenRequest
import me.onvo.onvo.domain.model.TokenResponse


interface AuthApiService {
    suspend fun getToken(request: TokenRequest): TokenResponse
    suspend fun checkInput(input: String, token: String): CheckInputResponse
    suspend fun login(request: LoginRequest, token: String): LoginResponse
    suspend fun logout(token: String)
}