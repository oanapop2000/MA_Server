package com.example.lab2.players.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lab2.players.domain.Player
import com.example.lab2.players.repo.PlayersRepository
import com.example.lab2.players.room.PlayerRoomDatabase
import com.example.lab2.players.service.PlayerService
import com.example.lab2.players.service.ServiceFactory
import com.example.lab2.players.utils.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class PlayersViewModel(application: Application) : AndroidViewModel(application) {

    val repository: PlayersRepository
    val service: PlayerService
    private val mutablePlayers = MutableLiveData<List<Player>>().apply { value = emptyList() }
    private val offlinePlayers: ArrayList<Player> = arrayListOf()
    val allPlayers: LiveData<List<Player>> = mutablePlayers

    init {
        val playersDao = PlayerRoomDatabase.getDatabase(application).playerDao()
        service = ServiceFactory
            .createRetrofitService(PlayerService::class.java, PlayerService.SERVICE_ENDPOINT)
        repository = PlayersRepository(playersDao)
    }

    fun insert(username: String, nume: String, email: String, data: String, grad: String, nrMeciuri: String) {
        val nrMeciuriBun = Integer.parseInt(nrMeciuri)
        val player = Player(0,username, nume, email, data, grad, nrMeciuriBun)
        var player1 = Player(0,username, nume, email, data, grad, nrMeciuriBun)
        viewModelScope.launch {
            try {
                player1 = service.addPlayer(player)
                launch(Dispatchers.IO) {
                    repository.addPlayer(player1)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
        logd("insert player: $player1")
    }

    fun insertOnlyInDB(username: String, nume: String, email: String, data: String, grad: String, nrMeciuri: String) = viewModelScope.launch(Dispatchers.IO){
        val nrMeciuriBun = Integer.parseInt(nrMeciuri)
        val player = Player(0,username, nume, email, data, grad, nrMeciuriBun)
        offlinePlayers.add(player)
        repository.addPlayer(player)
        logd("insert player only in DB: $player")
    }

    fun modify(id:Int, username: String, nume:String, email:String, data:String, grad:String, nrMeciuri: Int){
        val player = Player(id,username, nume, email, data, grad, nrMeciuri)

        viewModelScope.launch {
            try {
                service.modifyPlayer(player)
                launch(Dispatchers.IO) {
                    repository.modifyPlayer(player)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
        logd("modify player: $player")
    }

    fun delete(id: Int){
        viewModelScope.launch {
            try {
                val player=Player(id, " ", " ", " ", " ", " ", 0)
                service.deletePlayer(player)
                launch(Dispatchers.IO) {
                    repository.deletePlayer(id)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
        logd("delete player with id: $id")
    }

    fun fetchPlayersFromNetwork() {
        viewModelScope.launch {
            try {
                mutablePlayers.value = service.getPlayers()
                launch(Dispatchers.IO) {
                    repository.deletePlayers()
                    repository.addPlayers(allPlayers.value!!)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}
