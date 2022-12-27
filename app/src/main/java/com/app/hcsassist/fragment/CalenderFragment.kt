package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.calender_event.Event
import com.app.hcsassist.apimodel.calender_event.Holiday
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.databinding.CalendarDayBinding
import com.app.hcsassist.databinding.CalendarHeaderBinding
import com.app.hcsassist.databinding.FragmentCalenderBinding
import com.app.hcsassist.modelfactory.CalenderEventModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.CalenderEventViewModel
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


data class EventCalender(val id: String, val text: String, val date: LocalDate)
@RequiresApi(Build.VERSION_CODES.O)
class CalenderFragment : Fragment() {

    lateinit var fragmentCalenderBinding: FragmentCalenderBinding
    lateinit var mainActivity: MainActivity
    val c: Calendar = Calendar.getInstance()
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private var responseDate = ArrayList<LocalDate?>()


    var eventList: ArrayList<Event>? = ArrayList()
    var holidayList: ArrayList<Holiday>? = ArrayList()
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<EventCalender>>()
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: CalenderEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCalenderBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_calender,container,false)
        val root = fragmentCalenderBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: CalenderEventViewModel by viewModels {
            CalenderEventModelFactory(ApiHelper(ApiClient.apiService))
        }

        c.set(Calendar.DAY_OF_MONTH, 1);
        viewModel = vm

        /*val start = Calendar.getInstance()
        val today = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 2)
        start.add(Calendar.YEAR, -2)*/

        with(fragmentCalenderBinding){
            // .setDatesTypeface(typeface) //set font for dates
            //.setWeekHeaderTypeface(typeface) //set font for week names
            //.setMonthTitleTypeface(typeface) //set font for title of the calendar
            //.addEvent(c) //set events on the EventsCalendar [c: Calendar]
            // .disableDate(dc) //disable a specific day on the EventsCalendar [c: Calendar]
            //.setMonthRange(today, end)//set starting month [start: Calendar] and ending month [end: Calendar]
            //.setCurrentSelectedDate(today) //set current date and scrolls the calendar to the corresponding month of the selected date [today: Calendar]
//            .setDateTextFontSize(16f) //set font size for dates
//            .setMonthTitleFontSize(16f) //set font size for title of the calendar
//            .setWeekHeaderFontSize(16f) //set font size for week names
            //.setMonthRange(start, end)
//            //.setToday(today) //set today's date [today: Calendar]
//            eventsCalendar.setSelectionMode(eventsCalendar.SINGLE_SELECTION) //set mode of Calendar
//
//                .setToday(today)
//                .setWeekStartDay(Calendar.SUNDAY, false) //set start day of the week as you wish [startday: Int, doReset: Boolean]
//                .setMonthRange(start, end)
//                .setIsBoldTextOnSelectionEnabled(true)
//                .setCurrentSelectedDate(start) //set current date and scrolls the calendar to the corresponding month of the selected date [today: Calendar]
//
//                //.setSelectionColor(R.color.green)*/
//                .setCallback(this@CalenderFragment) //set the callback for EventsCalendar
//               // .disableDaysInWeek(Calendar.SATURDAY, Calendar.SUNDAY) //disable days in a week on the whole EventsCalendar [varargs days: Int]
//                .build()

        }

        with(fragmentCalenderBinding){

            exThreeCalendar.monthScrollListener = {
                yearText.text = if (it.yearMonth.year == today.year) {
                    titleSameYearFormatter.format(it.yearMonth)
                } else {
                    titleFormatter.format(it.yearMonth)
                }

                getCalenderEventData(it.yearMonth.atDay(1).toString())
                // Select the first day of the visible month.
             //   selectDate(it.yearMonth.atDay(1))
            }




            val daysOfWeek = daysOfWeek()
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(50)
            val endMonth = currentMonth.plusMonths(50)
            configureBinders(daysOfWeek)
            exThreeCalendar.apply {
                setup(startMonth, endMonth, daysOfWeek.first())
                //getCalenderEventData(daysOfWeek.first())
                scrollToMonth(currentMonth)
            }

            if (savedInstanceState == null) {
                // Show today's events initially.

                println(today.withDayOfMonth(1))
                //getCalenderEventData(today.withDayOfMonth(1).toString())

               // exThreeCalendar.post { selectDate(today) }

            }

        }

        fragmentCalenderBinding.btnBack.setOnClickListener {
            mainActivity.onBackPressedDispatcher.onBackPressed()
        }

        return root
    }
    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { fragmentCalenderBinding.exThreeCalendar.notifyDateChanged(it) }
            fragmentCalenderBinding.exThreeCalendar.notifyDateChanged(date)
            //updateAdapterForDate(date)
            fragmentCalenderBinding.exThreeSelectedDateText.text = selectionFormatter.format(date)
        }
    }

    private fun dateInString(cal:Calendar) : String{
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    private fun getLocalDate(stringDate:String):LocalDate{
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        formatter =
            formatter.withLocale(Locale.getDefault()) // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
        return LocalDate.parse(stringDate, formatter)
    }



    private fun getCalenderEventData(date: String){

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {
            viewModel.getCalenderEvent(authtoken ="Bearer "+sessionManager?.getToken()!!,
                CalenderEventRequest(date = date)
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){

                                try {
                                    eventList?.clear()
                                    resource.data.data?.events?.forEach {itEvent->

                                        eventList?.add(itEvent!!)
                                    }
                                    holidayList?.clear()
                                    resource.data.data?.holidays?.forEach {itHoliday->

                                        holidayList?.add(itHoliday!!)
                                    }
                                }finally {
                                    with(fragmentCalenderBinding){

                                        llEventContainer.removeAllViews()
                                        val title=TextView(mainActivity)
                                        title.setText("Events")
                                        responseDate.clear()
                                        if (eventList!!.size>0)
                                            llEventContainer.addView(title)
                                        for (i in 0 until eventList!!.size){
                                            val startDate = getLocalDate(eventList!![i].startDate!!)
                                            val endDate = getLocalDate(eventList!![i].endDate!!)
                                            responseDate.add(startDate)
                                            responseDate.add(endDate)

                                            val itemView: View = layoutInflater.inflate(R.layout.calender_list_event_adapter, null, false)

                                            val ivEventColor = itemView.findViewById<ImageView>(R.id.ivEventColor)
                                            val tvEventName = itemView.findViewById<TextView>(R.id.tvEventName)
                                            val tvEventTime = itemView.findViewById<TextView>(R.id.tvEventTime)
                                            val btnCalenderdetails = itemView.findViewById<ConstraintLayout>(R.id.btnCalenderdetails)
                                            tvEventName.text= eventList!![i].event
                                            tvEventTime.text= eventList!![i].startDate+" to "+eventList!![i].endDate
                                            ivEventColor.setColorFilter(
                                                ContextCompat.getColor(
                                                    mainActivity,
                                                    R.color.red
                                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                                            )

                                            btnCalenderdetails.setOnClickListener {

                                                detailspopup(tvEventName.text.toString(), tvEventTime.text.toString())

                                            }

                                            llEventContainer.addView(itemView)

                                        }
                                        val titleHoliday=TextView(mainActivity)
                                        titleHoliday.setText("Holiday")
                                        if (holidayList!!.size>0)
                                            llEventContainer.addView(titleHoliday)
                                        for (i in 0 until holidayList!!.size){

                                            val startDate = getLocalDate(holidayList!![i].startDate!!)
                                            val endDate = getLocalDate(holidayList!![i].endDate!!)

                                            responseDate.add(startDate)
                                            responseDate.add(endDate)

                                            val itemView: View = layoutInflater.inflate(R.layout.calender_list_event_adapter, null, false)

                                            val ivEventColor = itemView.findViewById<ImageView>(R.id.ivEventColor)
                                            val tvEventName = itemView.findViewById<TextView>(R.id.tvEventName)
                                            val tvEventTime = itemView.findViewById<TextView>(R.id.tvEventTime)
                                            val btnCalenderdetails = itemView.findViewById<ConstraintLayout>(R.id.btnCalenderdetails)


                                            tvEventName.text= holidayList!![i].holiday
                                            tvEventTime.text= holidayList!![i].startDate+" to "+holidayList!![i].endDate
                                            ivEventColor.setColorFilter(
                                                ContextCompat.getColor(
                                                    mainActivity,
                                                    R.color.yellow
                                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                                            )

                                            btnCalenderdetails.setOnClickListener {

                                                detailspopup(tvEventName.text.toString(), tvEventTime.text.toString())

                                            }

                                            llEventContainer.addView(itemView)

                                        }

                                        val titleEmpty=TextView(mainActivity)
                                        titleEmpty.setText("No Events available")
                                        if (eventList!!.isEmpty() && holidayList!!.isEmpty())
                                            llEventContainer.addView(titleEmpty)

                                        responseDate.forEach {itLocalDate->

                                            fragmentCalenderBinding.exThreeCalendar.notifyDateChanged(itLocalDate!!)
                                        }
                                    }


                                }
                                // attandanceAdapter.updateList(resource.data.data)

                            }else{

                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setMessage(resource.data?.message)
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->

                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()

                            }


                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            Toast.makeText(mainActivity, it.message, Toast.LENGTH_SHORT).show()

                        }

                        Status.LOADING -> {
                            showProgressDialog()
                        }

                    }

                }
            }
        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }

    }

    private fun detailspopup(eventname:String, eventtime:String){
        val btnPopupclose: TextView
        val tveventName: TextView
        val tvEventTime: TextView
        val dialog = Dialog(mainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val params = WindowManager.LayoutParams()
        dialog.setContentView(R.layout.layout_eventdetails)
        params.copyFrom(dialog.getWindow()?.getAttributes())
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
        dialog.getWindow()?.setAttributes(params)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        btnPopupclose = dialog.findViewById(R.id.btnPopupclose)
        tveventName = dialog.findViewById(R.id.tveventName)
        tvEventTime = dialog.findViewById(R.id.tvEventTime)

        tveventName.text = eventname
        tvEventTime.text = eventtime

        btnPopupclose.setOnClickListener {

            dialog.dismiss()
        }

    }


    var mProgressDialog: ProgressDialog? = null
    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(mainActivity)
            mProgressDialog!!.setMessage("Loading...")
            mProgressDialog!!.isIndeterminate = true
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }
    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                     //   selectDate(day.date)
                    }
                }
            }
        }
        fragmentCalenderBinding.exThreeCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.visibility=View.VISIBLE
                    when (data.date) {
                        today -> {
                            // textView.setTextColorRes(R.color.example_3_white)
                          // textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.visibility=View.INVISIBLE
                            textView.background = null
                        }
                        selectedDate -> {
                            // textView.setTextColorRes(R.color.example_3_blue)
                           // textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.visibility=View.INVISIBLE
                            textView.background = null
                        }
                        else -> {
                            // textView.setTextColor(R.color.black)
                            dotView.visibility=View.INVISIBLE
                            if (responseDate.contains(data.date)){
                                textView.setTextColor(
                                    resources.getColor(
                                        R.color.white,
                                        resources.newTheme()
                                    )
                                )
                                textView.setBackgroundResource(R.drawable.button_red_bg)
                            }else{
                                textView.background = null
                            }
                        }
                    }
                } else {
                    textView.visibility=View.INVISIBLE
                    dotView.visibility=View.INVISIBLE
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        fragmentCalenderBinding.exThreeCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        /* container.legendLayout.children.map { it as TextView }
                             .forEachIndexed { index, tv ->
                                 tv.text = daysOfWeek[index].name.first().toString()
                                 tv.setTextColorRes(R.color.black)
                             }*/
                    }
                }
            }
    }


}