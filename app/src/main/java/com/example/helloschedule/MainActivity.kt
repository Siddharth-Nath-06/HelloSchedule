package com.example.helloschedule

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val CALENDAR_PERMISSION_REQUEST_CODE = 1001

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkCalendarPermission()

        val intentAction = intent?.action
        if (intentAction == Intent.ACTION_VIEW && intent.hasExtra("fromShortcut")) {
            val bottomSheet = ScheduleBottomSheet()
            bottomSheet.show(supportFragmentManager, "ScheduleBottomSheet")
        }

        val addShortcutButton = findViewById<Button>(R.id.addShortcutButton)

        addShortcutButton.setOnClickListener {
                val shortcutManager = getSystemService(ShortcutManager::class.java)
                if (shortcutManager?.isRequestPinShortcutSupported == true) {
                    val shortcut = ShortcutInfo.Builder(this, "schedule_shortcut")
                        .setShortLabel("Schedule")
                        .setLongLabel("View My Schedule")
                        .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                        .setIntent(Intent(this, TransparentActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                            putExtra("fromShortcut", true)
                        })
                        .build()

                    val pinnedShortcutCallbackIntent = PendingIntent.getActivity(
                        this, 0,
                        Intent(this, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    ).intentSender

                    shortcutManager.requestPinShortcut(shortcut, pinnedShortcutCallbackIntent)
                } else {
                    Toast.makeText(this, "Shortcuts not supported on your device", Toast.LENGTH_SHORT).show()
                }
        }

    }
    private fun checkCalendarPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALENDAR
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CALENDAR),
                CALENDAR_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Calendar permission is required to view events.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}