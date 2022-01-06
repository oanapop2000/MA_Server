package com.example.lab2.players.service

import com.example.lab2.players.domain.Player
import retrofit2.http.*
import retrofit2.http.Body

import retrofit2.http.HTTP




interface PlayerService {

    @GET("/players")
    suspend fun getPlayers(): List<Player>

    @POST("/player")
    suspend fun addPlayer(@Body e: Player): Player

    @PUT("/modifyPlayer")
    suspend fun modifyPlayer(@Body e: Player): Player

    @HTTP(method = "DELETE", path = "/deletePlayer", hasBody = true)
    suspend fun deletePlayer(@Body e: Player): Player

    companion object {
        const val SERVICE_ENDPOINT = "http://10.0.2.2:3000"
    }
}