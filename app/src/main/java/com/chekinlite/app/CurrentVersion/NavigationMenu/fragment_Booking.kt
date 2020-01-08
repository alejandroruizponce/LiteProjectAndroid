package com.chekinlite.app.CurrentVersion.NavigationMenu

import android.animation.LayoutTransition
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.widget.*
import com.reginald.editspinner.EditSpinner
import org.json.JSONArray
import org.json.JSONObject
import android.support.design.internal.BottomNavigationMenuView
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Activities.MainActivity
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.Models.TableBookingsController
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Booking (ng: Int, nCG: Int, book: JSONObject, listG: JSONArray, err: Boolean) {

    var nGuests: Int = 0
    var nCheckedGuests: Int = 0
    var booking: JSONObject = JSONObject()
    var listGuests: JSONArray = JSONArray()
    var hasError: Boolean = false

    init {
        nGuests = ng
        nCheckedGuests = nCG
        booking = book
        listGuests = listG
        hasError = err
    }

}


class fragment_Booking  : Fragment(),
    TableBookingsController.ItemClickListener,
    DatePickerDialog.OnDateSetListener {


    var adapter: TableBookingsController? = null
    var listBookings: ArrayList<JSONObject> = ArrayList()
    var listGuestBooking: ArrayList<Booking> = ArrayList()
    var listProperties: ArrayList<JSONObject> = ArrayList()
    var nameProperties: ArrayList<String>? = ArrayList()
    var idProperties: ArrayList<String>? = ArrayList()
    var isCalendarOn: Boolean = false
    var isPropertyOn: Boolean = false
    var bookingsLoaded: Boolean = false
    var propertyRow = 0

    lateinit var dateFromDate: Date
    var dateFromSelected = ""
    var dateToSelected = ""


    lateinit var MyDialog: Dialog
    lateinit var loadingDialog: Dialog
    lateinit var buttonFilterBooking: Button
    lateinit var buttonAllBookings: TextView
    lateinit var buttonPendingBookings: TextView
    lateinit var buttonCompletedBookings: TextView
    lateinit var buttonErrorBookings: TextView
    lateinit var buttonNewBookings: TextView

    lateinit var buttonFilterByDate: ImageButton

    lateinit var progressBar: ProgressBar

    lateinit var buttonComplete: Button
    lateinit var circleStatusMain: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var filterPropertySpinner: EditSpinner
    lateinit var progressBarLoading: ProgressBar

    lateinit var dialogFilterDate: Dialog
    lateinit var titleYearDateFilter: TextView
    lateinit var titleDateFilter: TextView
    lateinit var dateFromView: RelativeLayout
    lateinit var dateToView: RelativeLayout
    lateinit var dateFromEditText: EditText
    lateinit var dateToEditText: EditText
    lateinit var cancelButtonFilterDate: Button
    lateinit var filterButtonFilterDate: Button
    lateinit var removeFilterButtonFilterDate: Button
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var progressBarCellBooking: ProgressBar

    var loading = true;
    var i = 0;
    var pastVisiblesItems = 0
    var visibleItemCount = 0
    var totalItemCount = 0


    lateinit var noBookingsView: ConstraintLayout
    lateinit var buttonNoBooking: Button
    val myCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_booking, container, false)
        // Inflate the layout for this fragment

        UserProfile.tabIsDisplayed = 0

        var sharedPreference = activity?.getSharedPreferences("LOGIN_CREDENTIALS", Context.MODE_PRIVATE)
        if(sharedPreference?.getString("token", "") != "") {
            UserProfile.token = sharedPreference?.getString("token", "").toString()
            println("El token del usuario logueado es: ${UserProfile.token}")
        }

        progressBar = view.findViewById(R.id.progressBarBookings)
        noBookingsView = view.findViewById(R.id.noBookingsView)
        noBookingsView.visibility = View.INVISIBLE
        buttonNoBooking = view.findViewById(R.id.buttonNoBooking)
        buttonNoBooking.setOnClickListener {
            (UserProfile.navigationBar.getChildAt(0) as BottomNavigationMenuView).getChildAt(2)
                .callOnClick()
        }


        progressBarCellBooking = view.findViewById(R.id.progressBarCellBooking)
        progressBarLoading = view.findViewById(R.id.progressBarBookings)






        buttonFilterByDate = view.findViewById(R.id.buttonFilterByDate)
        buttonFilterByDate.setOnClickListener {
            showDialogFilterDate()
        }


        // set up the RecyclerView
        val recView = view?.findViewById<RecyclerView>(R.id.tableBookings)
        if (recView != null) {
            recyclerView = recView
        }
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.setLayoutManager(mLayoutManager)



        adapter = this.context?.let { listBookings?.let { it1 -> listGuestBooking?.let { it2 ->
            TableBookingsController(
                it,
                it1,
                it2
            )
        } } }
        adapter?.setClickListener(this)
        recyclerView?.setAdapter(adapter)

        setScrollListenerInRecyclerView()

        filterPropertySpinner = view.findViewById(R.id.filterByProperty)

        buttonFilterBooking = view.findViewById(R.id.filterByBooking)
        buttonFilterBooking.setOnClickListener {
            showDialogFilterBooking()
        }

        circleStatusMain = view.findViewById(R.id.circleMainFilter)







        activity?.let {
            ChekinNewAPI.getAccommodations(it) { result, status ->
                if (status) {
                    if (result is JSONArray && result != null) {
                        for (num in 0..result?.length() - 1) {
                            listProperties?.add(result[num] as JSONObject)
                        }
                        UserProfile.listProperties =
                            listProperties
                        getPropertiesNames()
                    }

                } else {
                    println("ENTRAMOS EN EL ELSE DE GETPROPERTIES")

                    Toast.makeText(
                        activity,
                        getString(R.string.expiredSession),
                        Toast.LENGTH_SHORT
                    ).show();
                    var sharedPreference =
                        activity?.getSharedPreferences("LOGIN_CREDENTIALS", Context.MODE_PRIVATE)
                    var editor = sharedPreference?.edit()
                    editor?.remove("Logged")
                    editor?.commit()

                    val intent = Intent(
                        activity,
                        MainActivity::class.java
                    )
                    // start your next activity
                    startActivity(intent)
                }
            }
        }

        if(UserProfile.needBookingReload) {
            UserProfile.needBookingReload = false
            resetBookings()
            loadBookings()
        } else {

            progressBar.visibility = View.INVISIBLE
            println("NO HACE FALTA CARGAR LAS RESERVAS")
        }




        return view
    }

    override fun onResume() {
        super.onResume()

        print("VOLVEMOS AL FRAGMENT BOOKING DESDE BOOKING DETAILS")

    }



    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun resetBookings() {
        listBookings.clear()
        listGuestBooking.clear()
        UserProfile.nPage = 1
        i = 0
    }

    fun setScrollListenerInRecyclerView() {
        loading = true
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy > 0) {//check for scroll down{
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            println("SE VISUALIZA EL ULTIMO ELEMENTO DE LA LISTA DE RESERVAS")
                            UserProfile.nPage++
                            progressBarCellBooking.visibility = View.VISIBLE
                            loadBookings()
                        }
                    }
                }
            }
        })

    }

    fun loadBookings() {


        var URLParams = StringBuilder()

        URLParams.append("reservations/?ordering=-check_in_date&page=${UserProfile.nPage}")

        if (!isCalendarOn) {
            if (!isPropertyOn) {


            } else {
               URLParams.append("&housing=${idProperties?.get(propertyRow)}")
            }
        } else {
            URLParams.append("&check_in_date_from=${dateFromSelected}&check_in_date_until=$dateToSelected")
            if (!isPropertyOn) {


            } else {
                URLParams.append("&check_in_date_from=${dateFromSelected}&check_in_date_until=${dateToSelected}&housing=${idProperties?.get(propertyRow)}")
            }
        }


        if (UserProfile.filterMode == 1){
            URLParams.append("&aggregated_status=NEW")
        } else if (UserProfile.filterMode == 2) {
            URLParams.append("&aggregated_status=IN_PROGRESS")
        } else if (UserProfile.filterMode == 3) {
            URLParams.append("&aggregated_status=COMPLETE")
        } else if (UserProfile.filterMode == 4) {
            URLParams.append("&aggregated_status=ERROR")
        }

        println("Los parametros para recuperar las reservas son: ${URLParams.toString()}")



        activity?.let {
            ChekinNewAPI.getCheckinsByPage(
                it,
                URLParams.toString(),
                UserProfile.nPage
            ) { result, status ->
                if (status) {
                    if (result != null) {
                        result.getJSONArray("results").let { bookingP ->
                            if (bookingP.length() > 0) {
                                for (num in 0..bookingP.length() - 1) {
                                    println("Se añade la reserva a la lista; index $num de ${result.length() - 1}")
                                    listBookings?.add(bookingP[num] as JSONObject)
                                }
                            }
                        }


                    }
                    checkGuestsInBookings(
                        listBookings,
                        this.i,
                        UserProfile.filterMode
                    )

                } else {
                    UserProfile.nPage--
                    progressBarCellBooking.visibility = View.INVISIBLE
                    setScrollListenerInRecyclerView()

                }
            }

        }
    }



    fun checkGuestsInBookings(bookings: ArrayList<JSONObject>, index: Int, modeFilter: Int) {
        println("SE BUSCAN HUESPEDES EN LA LISTA DE RESERVAS")
        print("EL index es: $index")
            if (UserProfile.bookingNeedReload) {
                i = index
                if (bookings.count() != 0) {
                    recyclerView.visibility = View.VISIBLE
                    noBookingsView.visibility = View.INVISIBLE
                    bookingsLoaded = true
                    bookings[i].let { booking ->
                        booking.getString("id").let { id ->
                            println("Su id es: $id")
                            booking.getJSONObject("guest_group").let { gGroup ->
                                var nGuests = gGroup.optInt("number_of_guests", -1)
                                if (nGuests >= 0) {
                                    println("HUESPEDES TOTALES EN $id es: $nGuests")
                                    gGroup.getJSONArray("members").let { arrayGuests ->
                                        if (arrayGuests.length() >= 0) {
                                            var nGuestsChecked =
                                                (arrayGuests?.length() as Int)
                                            println("HUESPEDES CHEQUEADOS en $id es: $nGuests")

                                            booking.getString("aggregated_status")
                                                .let { aggStatus ->
                                                    if (aggStatus == "ERROR") {
                                                        listGuestBooking?.add(
                                                            Booking(
                                                                nGuests,
                                                                nGuestsChecked,
                                                                booking,
                                                                arrayGuests,
                                                                true
                                                            )
                                                        )
                                                    } else {
                                                        listGuestBooking?.add(
                                                            Booking(
                                                                nGuests,
                                                                nGuestsChecked,
                                                                booking,
                                                                arrayGuests,
                                                                false
                                                            )
                                                        )
                                                    }
                                                }

                                        }


                                        println("Hay $nGuests huespedes en total")
                                        if (index == (listBookings?.count() as Int) - 1) {
                                            this.i = index + 1
                                            println("Se termina el chequeo de reservas en BOOKINGS")
                                            bookingsLoaded = true
                                            progressBar.visibility = View.INVISIBLE


                                            println("Los filtros coinciden: $UserProfile.filterMode es igual a  $modeFilter")
                                            adapter?.listBookings = listBookings
                                            adapter?.listGuestBookings =
                                                listGuestBooking
                                            adapter?.notifyDataSetChanged()
                                            progressBarCellBooking.visibility =
                                                View.GONE
                                            setScrollListenerInRecyclerView()

                                            UserProfile.listGuestBookings =
                                                listGuestBooking
                                            UserProfile.listBookings = listBookings

                                        } else {
                                            //println("El index incrementado es: ${i++}")
                                            checkGuestsInBookings(
                                                bookings,
                                                i+ 1,
                                                modeFilter
                                            )


                                        }
                                    }

                                } else {
                                    println("Hay $nGuests huespedes en total")
                                    if (index == (listBookings?.count() as Int) - 1) {
                                        this.i = index + 1
                                        println("Se termina el chequeo de reservas en BOOKINGS")
                                        bookingsLoaded = true
                                        progressBar.visibility = View.INVISIBLE


                                        println("Los filtros coinciden: $UserProfile.filterMode es igual a  $modeFilter")
                                        adapter?.listBookings = listBookings
                                        adapter?.listGuestBookings =
                                            listGuestBooking
                                        adapter?.notifyDataSetChanged()
                                        progressBarCellBooking.visibility =
                                            View.GONE
                                        setScrollListenerInRecyclerView()

                                        UserProfile.listGuestBookings =
                                            listGuestBooking
                                        UserProfile.listBookings = listBookings

                                    } else {
                                        listBookings.removeAt(index)
                                        checkGuestsInBookings(bookings, i, modeFilter)
                                    }

                                }

                            }
                        }

                    }
                } else {

                    noBookingsView.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    adapter?.notifyDataSetChanged()
                }
            }
    }



    fun getPropertiesNames() {
        nameProperties?.add("Todas")
        idProperties?.add("")
        println("La cantidad de propiedades es: ${listProperties.count()}")
        for(i in 0..listProperties.count()-1) {
            val property = listProperties.get(i)
            property.getString("name").let {
                name ->
                //println("Se añade nombre de propiedad a nameProperties en index $i")
                nameProperties?.add(name)
            }

            property.getString("id").let {
                    id ->
                //println("Se añade id de propiedad a idProperties en index $i")
                idProperties?.add(id)
            }

        }
        println("nameProperties es: $nameProperties")

        val adapter = ArrayAdapter(
            activity, R.layout.item_dropdown,
            nameProperties
        )
        filterPropertySpinner.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        filterPropertySpinner.setAdapter(adapter)

        // triggered when one item in the list is clicked
        filterPropertySpinner.setOnItemClickListener { parent, view, position, id ->
            propertyRow = position
            if (propertyRow == 0) {
                isPropertyOn = false
            } else {
                isPropertyOn = true
            }
            resetBookings()
            loadBookings()

        }
    }


    fun showDialogFilterDate() {



        dialogFilterDate = Dialog(context)
        dialogFilterDate.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogFilterDate.setContentView(R.layout.custom_dialog_datefilter)
        dialogFilterDate.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        dialogFilterDate.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        filterButtonFilterDate = dialogFilterDate.findViewById(R.id.buttonFilterFilterDate) as Button
        filterButtonFilterDate.isClickable = false
        filterButtonFilterDate.alpha = 0.5f
        cancelButtonFilterDate = dialogFilterDate.findViewById(R.id.buttonCancelFilterDate)
        titleYearDateFilter = dialogFilterDate.findViewById(R.id.yearFilterDatePicker)
        titleYearDateFilter.setText(Calendar.getInstance().get(Calendar.YEAR).toString())
        titleDateFilter = dialogFilterDate.findViewById(R.id.titleDateFilter)
        val sdf = SimpleDateFormat("EEE");
        val d =  Date();
        val dayOfTheWeek = sdf.format(d);

        println("${Calendar.getInstance().toString()}")

        val cal=Calendar.getInstance();
        val month_date = SimpleDateFormat("MMMM");
        val month_name = month_date.format(cal.getTime());
        titleDateFilter.setText("${dayOfTheWeek}, $month_name ${Calendar.DAY_OF_MONTH.toString()} ")

        dateFromEditText = dialogFilterDate.findViewById(R.id.dateFromFilterEditText)

        dateFromView = dialogFilterDate.findViewById(R.id.dateFromFilterView)

        val dateFrom = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateFromDate()
                if(!dateToEditText.text.isNullOrBlank()) {
                    dateToEditText.setText("")
                    filterButtonFilterDate.isClickable = false
                    filterButtonFilterDate.alpha = 0.5f
                }
                dateToEditText.isEnabled = true
            }

        }

        dateFromEditText.setOnClickListener{

            // TODO Auto-generated method stub
            DatePickerDialog(
                activity,
                R.style.datePickerTheme, dateFrom, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dateToEditText = dialogFilterDate.findViewById(R.id.dateToFilterEditText)
        //dateToEditText.isEnabled = false

        dateToView = dialogFilterDate.findViewById(R.id.dateToFilterView)

        val dateTo = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateToDate()
            }

        }

        dateToEditText.setOnClickListener{

            // TODO Auto-generated method stub
            val datepPicker = DatePickerDialog(
                activity,
                R.style.datePickerTheme, dateTo, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepPicker.datePicker.minDate = dateFromDate.time
            datepPicker.show()
        }


        dialogFilterDate.show()
        cancelButtonFilterDate.setOnClickListener(View.OnClickListener {
            dialogFilterDate.cancel()

        })

        removeFilterButtonFilterDate = dialogFilterDate.findViewById(R.id.buttonRemoveFilterDate)
        removeFilterButtonFilterDate.setOnClickListener {
            isCalendarOn = false
            dialogFilterDate.dismiss()
            resetBookings()
            loadBookings()
        }

        filterButtonFilterDate.setOnClickListener{
            isCalendarOn = true
            dialogFilterDate.dismiss()
            resetBookings()
            loadBookings()
        }

        if(isCalendarOn) {
            removeFilterButtonFilterDate.visibility = View.VISIBLE
            dateFromEditText.setText(dateFromSelected)
            dateFromView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            dateFromView.setBackgroundColor(Color.parseColor("#ffffff"))
            dateFromEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            dateToEditText.setText(dateToSelected)
            dateToView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            dateToView.setBackgroundColor(Color.parseColor("#ffffff"))
            dateToEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        } else {
            removeFilterButtonFilterDate.visibility = View.GONE
        }

    }

    private fun updateFromDate() {
        val myFormat = "yyyy/MM/dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)


        dateFromDate = myCalendar.time
        dateFromEditText.setText((sdf.format(myCalendar.time)).replace("/", "-"))
        dateFromSelected = (sdf.format(myCalendar.time)).replace("/", "-")
        dateFromView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        dateFromView.setBackgroundColor(Color.parseColor("#ffffff"))
        dateFromEditText.setBackgroundResource(R.drawable.edittext_bottom_line)




        if(!dateFromEditText.text.isNullOrBlank() && !dateToEditText.text.isNullOrBlank()) {
            filterButtonFilterDate.isClickable = true
            filterButtonFilterDate.alpha = 1.0f

        } else {
            filterButtonFilterDate.isClickable = false
            filterButtonFilterDate.alpha = 0.5f

        }
    }

    fun updateToDate() {
        val myFormat = "yyyy/MM/dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)


        dateToEditText.setText((sdf.format(myCalendar.time)).replace("/", "-"))
        dateToSelected = (sdf.format(myCalendar.time)).replace("/", "-")
        dateToView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        dateToView.setBackgroundColor(Color.parseColor("#ffffff"))
        dateToEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

        if(!dateFromEditText.text.isNullOrBlank() && !dateToEditText.text.isNullOrBlank()) {
            filterButtonFilterDate.isClickable = true
            filterButtonFilterDate.alpha = 1.0f

        } else {
            filterButtonFilterDate.isClickable = false
            filterButtonFilterDate.alpha = 0.5f

        }
    }







    fun showDialogFilterBooking() {

        MyDialog = Dialog(activity)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.bookingfilter_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonAllBookings = MyDialog.findViewById(R.id.filterAllBookings) as TextView
        buttonPendingBookings = MyDialog.findViewById(R.id.filterPending) as TextView
        buttonCompletedBookings = MyDialog.findViewById(R.id.filterCompleted) as TextView
        buttonErrorBookings = MyDialog.findViewById(R.id.filterError) as TextView
        buttonNewBookings = MyDialog.findViewById(R.id.filterNew) as TextView

        MyDialog.show()
        buttonAllBookings.setOnClickListener(View.OnClickListener {
            UserProfile.filterMode = 0
            resetBookings()
            loadBookings()
            adapter?.notifyDataSetChanged()
            buttonFilterBooking.setText(R.string.todas_las_reservas)
            circleStatusMain.visibility = View.INVISIBLE
            progressBarLoading.visibility = View.VISIBLE
            MyDialog.cancel()

        })

        MyDialog.show()
        buttonNewBookings.setOnClickListener(View.OnClickListener {
            UserProfile.filterMode = 1
            resetBookings()
            loadBookings()
            adapter?.notifyDataSetChanged()
            buttonFilterBooking.setText(R.string.Nuevo)
            progressBarLoading.visibility = View.VISIBLE
            circleStatusMain.visibility = View.INVISIBLE
            MyDialog.cancel()

        })

        buttonPendingBookings.setOnClickListener(View.OnClickListener {
            UserProfile.filterMode = 2
            buttonFilterBooking.setText(R.string.pendientes)
            resetBookings()
            loadBookings()
            adapter?.notifyDataSetChanged()
            circleStatusMain.visibility = View.VISIBLE
            progressBarLoading.visibility = View.VISIBLE
            circleStatusMain.setImageResource(R.drawable.yellowcircle)
            MyDialog.cancel()

        })

        buttonCompletedBookings.setOnClickListener(View.OnClickListener {
            UserProfile.filterMode = 3
            buttonFilterBooking.setText(R.string.completadas)
            resetBookings()
            loadBookings()
            adapter?.notifyDataSetChanged()
            circleStatusMain.visibility = View.VISIBLE
            progressBarLoading.visibility = View.VISIBLE
            circleStatusMain.setImageResource(R.drawable.greencircle)
            MyDialog.cancel()

        })

        buttonErrorBookings.setOnClickListener(View.OnClickListener {
            UserProfile.filterMode = 4
            resetBookings()
            loadBookings()
            adapter?.notifyDataSetChanged()
            buttonFilterBooking.setText(R.string.erroneas)
            circleStatusMain.visibility = View.VISIBLE
            progressBarLoading.visibility = View.VISIBLE
            circleStatusMain.setImageResource(R.drawable.redcircle)
            MyDialog.cancel()

        })

    }

    fun showDialogLoading() {

        loadingDialog = Dialog(activity)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setContentView(R.layout.loading_dialog)
        loadingDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        loadingDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        progressBarLoading = loadingDialog.findViewById(R.id.progressBarLoading) as ProgressBar
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)



        loadingDialog.show()

    }



    override fun onItemClick(view: View, position: Int) {
        //Toast.makeText(activity, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


}
