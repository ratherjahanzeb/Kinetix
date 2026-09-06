package com.jahanzeb.kinetix

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class KinetixTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var settingsRepo: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        settingsRepo = SettingsRepository(this)
    }

    override fun onStartListening() {
        super.onStartListening()
        scope.launch {
            val isEnabled = settingsRepo.isEnabled.first()
            updateTileState(isEnabled)
        }
    }

    override fun onClick() {
        super.onClick()
        scope.launch {
            val current = settingsRepo.isEnabled.first()
            val newState = !current
            settingsRepo.setEnabled(newState)
            updateTileState(newState)
        }
    }

    private fun updateTileState(isEnabled: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
