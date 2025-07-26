package com.example.helloschedule

import android.app.ActivityOptions
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import java.util.Calendar
import android.content.ContentUris

private const val CALENDAR_PERMISSION_REQUEST_CODE = 1001

class TransparentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make background transparent
        window.setBackgroundDrawableResource(android.R.color.transparent)

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
        } else {
            showScheduleDrawer()
        }

    }
    private fun showScheduleDrawer() {
        val events = ArrayList(getEventsForNextThreeDays(this))
        Log.d("TransparentActivity", "Events retrieved: ${events.size}")
        val bottomSheet = ScheduleBottomSheet.newInstance(events)
        bottomSheet.show(supportFragmentManager, "ScheduleBottomSheet")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showScheduleDrawer()
            } else {
                Toast.makeText(this, "Calendar permission is required to view events.", Toast.LENGTH_SHORT).show()
                finish() // Exit if permission not granted
            }
        }
    }

    @SuppressLint("Range")
    fun getEventsForNextThreeDays(context: Context): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.ALL_DAY
        )

        val startMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)
        val uri = builder.build()

        val selection = "${CalendarContract.Instances.VISIBLE} = 1"
        val sortOrder = "${CalendarContract.Instances.BEGIN} ASC"

        val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndex(CalendarContract.Instances.EVENT_ID))
                val title = it.getString(it.getColumnIndex(CalendarContract.Instances.TITLE)) ?: "(No Title)"
                val start = it.getLong(it.getColumnIndex(CalendarContract.Instances.BEGIN))
                val end = it.getLong(it.getColumnIndex(CalendarContract.Instances.END))
                val location = it.getString(it.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)) ?: ""
                val allDay = it.getInt(it.getColumnIndex(CalendarContract.Instances.ALL_DAY)) == 1

                events.add(CalendarEvent(id, title, start, end, location, allDay))
            }
        }

        return events
    }

}