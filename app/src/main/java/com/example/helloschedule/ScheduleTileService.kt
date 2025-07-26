package com.example.helloschedule

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.TileService
import android.service.quicksettings.Tile
import android.util.Log

class ScheduleTileService : TileService() {

    override fun onClick() {
        super.onClick()

        Log.d("ScheduleTileService", "Tile tapped!")

        val intent = Intent(this, TransparentActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("fromQuickTile", true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pendingIntent)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile?.apply {
            icon = Icon.createWithResource(this@ScheduleTileService, R.drawable.ic_schedule_tile)
            label = "Schedule"
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }
    override fun onStartListening() {
        super.onStartListening()
        super.onStartListening()
        val tile = qsTile ?: return

        tile.icon = Icon.createWithResource(this, R.drawable.ic_schedule_tile)
        tile.label = "Schedule"
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()

    }

}
