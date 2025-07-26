package com.example.helloschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.TextView
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.provider.CalendarContract
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.content.ContentUris
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.widget.Space
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import java.time.*
import java.time.format.DateTimeFormatter


class ScheduleBottomSheet : BottomSheetDialogFragment(){
    private lateinit var events: List<CalendarEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ScheduleBottomSheet", "Arguments in onCreate: ${arguments?.keySet()}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = (getScreenHeight() * 0.7).toInt()
        bottomSheet?.requestLayout()

        val eventContainer = view.findViewById<LinearLayout>(R.id.eventsListContainer)
        val raw = arguments?.getSerializable("events")
        Log.d("ScheduleBottomSheet", "Raw events object: $raw")

        val events = arguments?.getSerializable("events", ArrayList::class.java) as? ArrayList<CalendarEvent>
        if (events == null) {
            Log.w("ScheduleBottomSheet", "Casting failed or events null!")
            return
        }
        populateEventsGroupedByDay(events, eventContainer)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        // Finish the activity if it's TransparentActivity
        (activity as? TransparentActivity)?.finish()
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun populateEventsGroupedByDay(events: List<CalendarEvent>, container: LinearLayout) {
        Log.d("populateEvents", "Total events passed: ${events.size}")
        val today = LocalDate.now()
        val grouped = events
            .filter { !it.isAllDay && isWithinThreeDays(it.startTime) }
            .groupBy { Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate() }

        for (i in 0..2) {
            val date = today.plusDays(i.toLong())
            val dayEvents = grouped[date].orEmpty().sortedBy { it.startTime }

            val dayTitle = TextView(requireContext()).apply {
                text = when (i) {
                    0 -> "Today"
                    1 -> "Tomorrow"
                    2 -> "Day After"
                    else -> date.toString()
                }
                textSize = 24f
                setPadding(8, 24, 8, 4)
                setTypeface(typeface, Typeface.BOLD)
            }

            val fullDateLabel = TextView(requireContext()).apply {
                text = date.format(DateTimeFormatter.ofPattern("EEEE,  dd-MM-yyyy"))
                setPadding(8, 4, 8, 12)
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
                textSize = 14f
            }

            container.addView(dayTitle)
            container.addView(fullDateLabel)

            if (dayEvents.isEmpty()) {
                val emptyView = TextView(requireContext()).apply {
                    text = "No events on this day"
                    setTextColor(resources.getColor(android.R.color.secondary_text_dark, null))
                    setPadding(16, 0, 16, 16)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (8 * resources.displayMetrics.density).toInt()
                    }
                }
                container.addView(emptyView)
            } else {
                for (event in dayEvents) {
                    val eventView = layoutInflater.inflate(R.layout.item_event, container, false)

                    eventView.findViewById<TextView>(R.id.eventTitle).text = event.title
                    eventView.findViewById<TextView>(R.id.eventTime).text = formatEventTime(event.startTime, event.endTime)
                    eventView.findViewById<TextView>(R.id.eventLocation).text = event.location

                    eventView.setOnClickListener {
                        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id)
                        val intent = Intent(Intent.ACTION_VIEW).setData(uri)
                        startActivity(intent)
                    }
                    container.addView(eventView)
                }
            }

            if (i == 0) {
                val themedColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, Color.BLACK)

                val newEventButton = Button(requireContext()).apply {
                    id = View.generateViewId()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER_VERTICAL
                    text = getString(R.string.new_event_button)

                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                    compoundDrawablePadding = (8 * resources.displayMetrics.density).toInt()

                    setTextColor(themedColor)
                    compoundDrawableTintList = ColorStateList.valueOf(themedColor)

                    background = ContextCompat.getDrawable(context, android.R.drawable.list_selector_background)
                    setPadding(16, 16, 16, 16)
                    isAllCaps = false

                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                        startActivity(intent)
                    }
                }

                container.addView(newEventButton)
            }
            if (i == 1 || i == 0) {
                val divider = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (1 * resources.displayMetrics.density).toInt()
                    ).apply {
                        topMargin = (16 * resources.displayMetrics.density).toInt()
                    }
                    setBackgroundColor(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorOutline, Color.LTGRAY))
                }
                container.addView(divider)
            }
            container.addView(Space(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (16 * resources.displayMetrics.density).toInt()
                )
            })
        }
        val openCalendarButton = ImageButton(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setImageResource(R.drawable.ic_arrow_up)
            background = null
            contentDescription = getString(R.string.calendar_button_desc)

            setOnClickListener {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR)
                startActivity(intent)
            }

            val arrowColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, Color.GRAY)
            imageTintList = ColorStateList.valueOf(arrowColor)
        }

        container.addView(openCalendarButton)
    }

    private fun isWithinThreeDays(startTime: Long): Boolean {
        val eventDate = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        return !eventDate.isBefore(today) && !eventDate.isAfter(today.plusDays(2))
    }

    private fun formatEventTime(startMillis: Long, endMillis: Long): String {
        val now = System.currentTimeMillis()
        val startInstant = Instant.ofEpochMilli(startMillis)
        val endInstant = Instant.ofEpochMilli(endMillis)

        val start = startInstant.atZone(ZoneId.systemDefault()).toLocalTime()
        val end = endInstant.atZone(ZoneId.systemDefault()).toLocalTime()

        return when {
            now in startMillis..endMillis -> "Ongoing • ${Duration.between(startInstant, Instant.ofEpochMilli(now)).toMinutes()}m"
            startMillis - now <= TimeUnit.MINUTES.toMillis(10) && startMillis > now -> "in ${Duration.between(Instant.ofEpochMilli(now), startInstant).toMinutes()}m"
            else -> "${start.format(DateTimeFormatter.ofPattern("h:mm a"))} – ${end.format(DateTimeFormatter.ofPattern("h:mm a"))}"
        }
    }




    private fun getDayCategory(eventTimeMillis: Long): String {
        val now = Calendar.getInstance()
        val eventCal = Calendar.getInstance().apply { timeInMillis = eventTimeMillis }

        val diffDays = TimeUnit.MILLISECONDS.toDays(
            eventCal.timeInMillis - now.run {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                timeInMillis
            }
        ).toInt()

        return when (diffDays) {
            0 -> "Today"
            1 -> "Tomorrow"
            2 -> "Day After"
            else -> "Other"
        }
    }

    companion object {
        fun newInstance(events: ArrayList<CalendarEvent>): ScheduleBottomSheet {
            val fragment = ScheduleBottomSheet()
            val bundle = Bundle()
            bundle.putSerializable("events", events)
            fragment.arguments = bundle
            return fragment
        }
    }

}