package com.example.wakeupshakeup.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.wakeupshakeup.database.AlarmInfoDatabase
import com.example.wakeupshakeup.services.ShakeListener
import com.example.wakeupshakeup.services.ShakeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmViewModel(application: Application) : AndroidViewModel(application), ShakeListener {
    private val _songTitle = MutableLiveData<String>()
    val songTitle: LiveData<String> = _songTitle

    private val _songArtist = MutableLiveData<String>()
    val songArtist: LiveData<String> = _songArtist

    private var shakeService: ShakeService? = null
    private var isBound = false

    private val songTitleObserver = Observer<String> { title ->
        _songTitle.postValue(title)
    }

    private val songArtistObserver = Observer<String> { artist ->
        _songArtist.postValue(artist)
    }

    val alarmInfoDatabase = AlarmInfoDatabase(application)

    private val _totalShakeCount = MutableLiveData<Int>()
    val totalShakeCount: LiveData<Int> get() = _totalShakeCount

    private val _streakCount = MutableLiveData<Int>()
    val streakCount: LiveData<Int> = _streakCount

    private val _setTime = MutableLiveData<String>()
    val setTime: LiveData<String> get() = _setTime

    init {
        loadTotalShakeCount()
        loadStreakCount()
        loadSetTime()
    }

    fun loadSetTime() {
        viewModelScope.launch {
            val time = withContext(Dispatchers.IO) {
                alarmInfoDatabase.getSetTime()
            }
            _setTime.postValue(time)
        }
    }

    fun modifySetTime(newTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmInfoDatabase.modifySetTime(newTime)
            _setTime.postValue(newTime)
        }
    }

    fun loadTotalShakeCount() {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                alarmInfoDatabase.getTotalShakeCount()
            }
            _totalShakeCount.postValue(count)
        }
    }

    private fun loadStreakCount() {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                alarmInfoDatabase.getStreakCount()
            }
            _streakCount.postValue(count)
        }
    }

    fun resetShakeAndStreakCountsToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            alarmInfoDatabase.resetShakeAndStreakCount()
            // After resetting, load the updated counts
            loadTotalShakeCount()
            loadStreakCount()
        }
    }

    fun incrementStreakCount() {
        viewModelScope.launch(Dispatchers.IO) {
            alarmInfoDatabase.incrementStreakCount()
            loadStreakCount() // Reload the streak count after incrementing
        }
    }

    fun resetStreakCount() {
        viewModelScope.launch(Dispatchers.IO) {
            alarmInfoDatabase.resetStreakCount()
            _streakCount.postValue(0) // Update the streak count LiveData to zero
        }
    }

    override fun onShakeCountChanged(count: Int) {
        _totalShakeCount.postValue(count)
    }

    override fun onStreakCountChanged(streakCount: Int) {
        _streakCount.postValue(streakCount)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ShakeService.LocalBinder
            shakeService = binder.getService()
            isBound = true

            // Observe the LiveData from the service and update the ViewModel's LiveData
            shakeService?.currentSongTitle?.observeForever(songTitleObserver)
            shakeService?.currentSongArtist?.observeForever(songArtistObserver)

            shakeService?.shakeListener = this@AlarmViewModel
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            shakeService?.shakeListener = null
        }
    }

    fun bindToShakeService() {
        val intent = Intent(getApplication(), ShakeService::class.java)
        getApplication<Application>().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindFromShakeService() {
        if (isBound) {
            shakeService?.currentSongTitle?.removeObserver(songTitleObserver)
            shakeService?.currentSongArtist?.removeObserver(songArtistObserver)
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        unbindFromShakeService()
    }
}
