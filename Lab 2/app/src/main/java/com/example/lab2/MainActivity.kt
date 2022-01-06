package com.example.lab2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.players.adapter.PlayersListAdapter
import com.example.lab2.players.domain.Player
import com.example.lab2.players.viewmodel.PlayersViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var playersViewModel: PlayersViewModel
    private lateinit var manager: Manager


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PlayersListAdapter(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        playersViewModel = ViewModelProvider(this).get(PlayersViewModel::class.java)
        manager = Manager()

        playersViewModel.repository.allPlayers.observe(this, { players ->
            players?.let { adapter.setPlayers(it) }
        })
       // observeModel(adapter)
        playersViewModel.fetchPlayersFromNetwork()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewPlayerActivity::class.java)
            newPlayerActivityLauncher.launch(intent)
        }

        adapter.onItemClick = {
            println("pst")
            val intent = Intent(this@MainActivity, PlayerDetailsActivity::class.java)
            intent.putExtra("id", it.id.toString())
            intent.putExtra("username", it.username)
            intent.putExtra("nume", it.nume)
            intent.putExtra("email", it.email)
            intent.putExtra("dataNasterii", it.dataNasterii)
            intent.putExtra("grad", it.grad)
            intent.putExtra("nrMeciuri", it.nrMeciuriCastigate.toString())
            playerDetailsActivityLauncher.launch(intent)
        }
    }

//    private fun observeModel(adapter: PlayersListAdapter) {
//        playersViewModel.allPlayers.observe { displayPlayers(it ?: emptyList(), adapter) }
//    }

//    private fun displayPlayers(players: List<Player>, adapter: PlayersListAdapter) {
//        adapter?.setPlayers(players)
//    }

//    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
//        observe(this@MainActivity, { observe(it) })

    @RequiresApi(Build.VERSION_CODES.M)
    private val newPlayerActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (data != null) {
                if(!manager.networkConnectivity(this)){
                    println("Nu este net")
                    playersViewModel.insertOnlyInDB(data.getStringExtra("username")!!,data.getStringExtra("nume")!!,data.getStringExtra("email")!!,data.getStringExtra("dataNasterii")!!,data.getStringExtra("grad")!!,data.getStringExtra("nrMeciuri")!!)
                }else{
                    playersViewModel.insert(data.getStringExtra("username")!!,data.getStringExtra("nume")!!,data.getStringExtra("email")!!,data.getStringExtra("dataNasterii")!!,data.getStringExtra("grad")!!,data.getStringExtra("nrMeciuri")!!)
                }
            }

        }

    @RequiresApi(Build.VERSION_CODES.M)
    private val playerDetailsActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if(data!!.getStringExtra("modificare/stergere").equals("modificare")){
                if(!manager.networkConnectivity(this)){
                    println("Nu este net")
                }else{
                    playersViewModel.modify(Integer.parseInt(data.getStringExtra("id")!!), data.getStringExtra("username")!!,data.getStringExtra("nume")!!,data.getStringExtra("email")!!,data.getStringExtra("dataNasterii")!!,data.getStringExtra("grad")!!,Integer.parseInt(data.getStringExtra("nrMeciuri")!!))
                }
            }else if(data.getStringExtra("modificare/stergere").equals("stergere")){
                if(!manager.networkConnectivity(this)){
                    println("Nu este net")
                }else{
                    playersViewModel.delete(Integer.parseInt(data.getStringExtra("id")!!))                }
                }


        }
}
