package com.example.lab2.players.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lab2.players.domain.Player
import com.example.lab2.players.room.PlayerDao
import com.example.lab2.players.service.PlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlayersRepository(private val playerDao: PlayerDao) {

   val allPlayers: LiveData<List<Player>> = playerDao.getPlayers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPlayer(player: Player){
        playerDao.insert(player)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun modifyPlayer(player: Player){
        playerDao.modify(player)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deletePlayer(id: Int){
        playerDao.delete(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deletePlayers(){
        playerDao.deletePlayers()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPlayers(players: List<Player>){
        playerDao.addPlayers(players)
    }
}