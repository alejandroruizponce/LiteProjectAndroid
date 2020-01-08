package com.chekinlite.app.CurrentVersion.Models


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.chekinlite.app.CurrentVersion.Activities.AddGuest
import com.chekinlite.app.CurrentVersion.Activities.BookingDetails
import com.chekinlite.app.CurrentVersion.NavigationMenu.Booking
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R
import org.json.JSONObject


class TableBookingsController// data is passed into the constructor
internal constructor(context: Context,  var listBookings: ArrayList<JSONObject>,  var listGuestBookings: ArrayList<Booking>) :
    RecyclerView.Adapter<TableBookingsController.ViewHolder>() {
    private val mInflater: LayoutInflater
    private val context: Context= context
    private var mClickListener: ItemClickListener? = null
    lateinit var MyDialog: Dialog
    lateinit var progressBarLoading: ProgressBar

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.bookingcell, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        println("La posicion de la lista es: $position")
        listBookings[position].getJSONObject("housing").let { housing ->
            holder.bookingName.text = housing.getString("name")
        }

        holder.bookingDate.text = revertDate(listBookings[position].getString("check_in_date").subSequence(0, 10).toString())
        holder.bookingNights.text = listBookings[position].getString("nights_of_stay") + " " + context.getString(
            R.string.noches
        )
        holder.bookingHouse.text = ""

        holder.buttonCheckin.tag = position

        holder.buttonCheckin.setOnClickListener(View.OnClickListener {
            showDialogLoading()
            println("BOTON PULSADO EN CELDA $position")
            var propertyIndex = 0
            var intent = Intent(context, AddGuest::class.java)
            var bookingIndex = position
            listBookings[bookingIndex].getString("id").let {
                    idB ->
                intent.putExtra("bookingInfo", listBookings[bookingIndex].toString())
                intent.putExtra("idBooking", idB)
                print("La ID de la reserva a completar es: $idB")
                listBookings[position].getJSONObject("housing").let { housing ->
                    housing.getString("id").let { housing ->
                        var i = 0
                        for (property in UserProfile.listProperties) {
                            if (property.getString("id") == housing) {
                                propertyIndex = i
                            }
                            i++
                        }

                        UserProfile.listProperties[propertyIndex].getJSONObject("location").let { location ->
                            location.getJSONObject("country").let { country ->
                                country.getString("code").let { code ->
                                    intent.putExtra("isNationalityProperty", code)
                                }

                            }

                            val booking = listGuestBookings[bookingIndex]
                            intent.putExtra("noCheckedGuests", booking.nGuests - booking.nCheckedGuests)

                            listBookings[bookingIndex].getString("check_in_date").let {
                                    checkin ->
                                intent.putExtra("checkinDate", checkin)
                            }

                        }
                    }
                }

                context.startActivity(intent)
            }
        })


        var guestsText = context.getString(R.string.guestsText)
        var guestCompleted = context.getString(R.string.guestsCompleted)
        var guestPending = context.getString(R.string.guestsPending)

        println("EL FILTER MODE PARA ACTUALIZAR LAS RESERVAS ES: $UserProfile.filterMode")

        if (UserProfile.filterMode == 0) {

            if(position < listGuestBookings.count()  && listGuestBookings[position] != null) {
                listGuestBookings[position].let { bookGuests ->
                    if (!bookGuests.hasError) {
                        println("Esta reserva no tiene errores")
                        if (allGuestsCOM(bookGuests.booking)) {
                            holder.circleStatus.setImageResource(R.drawable.greencircle)
                            holder.statusBooking = 0
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.darkBlue
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.darkBlue
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${bookGuests.nCheckedGuests} $guestCompleted"
                            holder.buttonCheckin.visibility = View.INVISIBLE
                        } else if (bookGuests.nGuests == bookGuests.nCheckedGuests) {
                            if (isNewBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (isInProgressBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.darkBlue
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.darkBlue
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${bookGuests.nCheckedGuests} $guestCompleted"
                            holder.buttonCheckin.visibility = View.INVISIBLE
                        } else if (bookGuests.nCheckedGuests == 0) {
                            if (isNewBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (isInProgressBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text =
                                "${bookGuests.nGuests - bookGuests.nCheckedGuests} $guestPending"
                            holder.buttonCheckin.tag = position
                        } else {
                            if (isNewBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (isInProgressBooking(bookGuests.booking)) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text =
                                "${bookGuests.nGuests - bookGuests.nCheckedGuests} $guestPending"
                            holder.buttonCheckin.tag = position
                        }
                    } else {
                        println("ESTA RESERVA TIENE ERRORES")
                        if (bookGuests.nGuests == bookGuests.nCheckedGuests) {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.INVISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${bookGuests.nCheckedGuests} $guestCompleted"
                        } else if (bookGuests.nCheckedGuests == 0) {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text =
                                "${bookGuests.nGuests - bookGuests.nCheckedGuests} $guestPending"
                            holder.buttonCheckin.tag = position
                        } else {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${bookGuests.nGuests} $guestsText"
                            holder.pendingGuestsText.text =
                                "${bookGuests.nGuests - bookGuests.nCheckedGuests} $guestPending"
                            holder.buttonCheckin.tag = position
                        }
                    }
                }
            }
        } else if (UserProfile.filterMode == 1 ||  UserProfile.filterMode == 2) {
            listGuestBookings[position].booking.let {
                booking ->

                listGuestBookings[position].nCheckedGuests.let {
                    nCheckedGuests ->
                    listGuestBookings[position].nGuests.let {
                        nGuests ->
                        if (nCheckedGuests == 0) {
                            if (UserProfile.filterMode == 1) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (UserProfile.filterMode == 2) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nGuests - nCheckedGuests} $guestPending"
                        } else if (nCheckedGuests == nGuests) {
                            if (UserProfile.filterMode == 1) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (UserProfile.filterMode == 2) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nCheckedGuests} $guestCompleted"
                        } else {
                            if (UserProfile.filterMode == 1) {
                                holder.circleStatus.setImageResource(R.drawable.path_19041)
                            } else if (UserProfile.filterMode == 2) {
                                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                            }
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nGuests - nCheckedGuests} $guestPending"
                        }
                        holder.buttonCheckin.tag = position
                    }
                }
            }
        } else if (UserProfile.filterMode == 3) {
            listGuestBookings[position].booking.let {
                booking ->

                listGuestBookings[position].listGuests.let {
                    listG ->
                    if (allGuestsCOM(booking)) {
                        holder.circleStatus.setImageResource(R.drawable.greencircle)
                        holder.statusBooking = 0
                        holder.buttonCheckin.visibility = View.INVISIBLE
                        holder.totalGuestsText.setTextColor(context.resources.getColor(
                            R.color.pink
                        ))
                        holder.pendingGuestsText.setTextColor(context.resources.getColor(
                            R.color.pink
                        ))
                        holder.totalGuestsText.text = ""
                        holder.pendingGuestsText.text = ""
                    }
                }
            }
        } else if (UserProfile.filterMode == 4) {
            listGuestBookings[position].booking.let {
                booking ->

                listGuestBookings[position].nCheckedGuests.let {
                    nCheckedGuests ->
                    listGuestBookings[position].nGuests.let {
                        nGuests ->
                        if(nCheckedGuests == 0) {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nGuests - nCheckedGuests} $guestPending"
                        } else if (nCheckedGuests == nGuests) {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 0
                            holder.buttonCheckin.visibility = View.INVISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nCheckedGuests} $guestCompleted"
                        } else {
                            holder.circleStatus.setImageResource(R.drawable.redcircle)
                            holder.statusBooking = 1
                            holder.buttonCheckin.visibility = View.VISIBLE
                            holder.totalGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.pendingGuestsText.setTextColor(context.resources.getColor(
                                R.color.pink
                            ))
                            holder.totalGuestsText.text = "${nGuests} $guestsText"
                            holder.pendingGuestsText.text = "${nGuests - nCheckedGuests} $guestPending"
                        }

                        holder.buttonCheckin.tag = position
                    }
                }
            }
        }


        if (position % 2 == 0) {
            holder.cellBackground.setBackgroundColor(context.getResources().getColor(
                R.color.cellSoftBlue
            ))

        } else {
            holder.cellBackground.setBackgroundColor(context.getResources().getColor(
                R.color.white
            ))

        }




    }

    // total number of rows
    override fun getItemCount(): Int {
        if (UserProfile.filterMode == 0) {
            return listBookings.size
        }
        return listGuestBookings.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var bookingName: TextView
        internal var bookingDate: TextView
        internal var bookingNights: TextView
        internal var bookingHouse: TextView
        internal var cellBackground: ConstraintLayout
        internal var circleStatus: ImageView
        internal var statusBooking: Int = 0
        internal var totalGuestsText: TextView
        internal var pendingGuestsText: TextView
        internal var buttonCheckin: Button

        init {
            bookingName = itemView.findViewById(R.id.mainGuestBooking)
            bookingDate = itemView.findViewById(R.id.dateBookingTextView)
            bookingNights = itemView.findViewById(R.id.nightsBooking)
            bookingHouse = itemView.findViewById(R.id.propertyBooking)
            cellBackground = itemView.findViewById(R.id.backgroundBookingCell) as ConstraintLayout
            circleStatus = itemView.findViewById(R.id.statusBookingCircle) as ImageView
            totalGuestsText = itemView.findViewById(R.id.totalGuests) as TextView
            pendingGuestsText = itemView.findViewById(R.id.pendingGuests) as TextView
            buttonCheckin = itemView.findViewById(R.id.buttonCompleteBooking) as Button


            // Handle item click and set the selection
            itemView.setOnClickListener {
                println("CELDA ${layoutPosition} PULSADA")


                var intent = Intent(context, BookingDetails::class.java)

                var propertyIndex = 0

                listBookings[layoutPosition].getJSONObject("housing").let { housing ->
                    housing.getString("id").let { id ->
                        var i = 0
                        for (property in UserProfile.listProperties) {
                            if (property.getString("id") == id) {
                                propertyIndex = i
                            }
                            i++
                        }
                        UserProfile.listProperties[propertyIndex].getJSONObject("location")
                            .let { location ->
                                location.getJSONObject("country").let { country ->
                                    country.getString("code").let { code ->
                                        intent.putExtra("isNationalityProperty", code)
                                    }
                                }
                            }
                    }

                    intent.putExtra("bookingInfo", listBookings[layoutPosition].toString())
                    intent.putExtra("booking", listBookings[layoutPosition].toString())
                    intent.putExtra("idBook", listBookings[layoutPosition].getString("id"))
                    println("La ID de la reserva seleccionada es: ${listBookings[layoutPosition].getString("id")}")
                    intent.putExtra(
                        "noCheckedGuests",
                        (listGuestBookings[layoutPosition].nGuests - listGuestBookings[layoutPosition].nCheckedGuests).toInt()
                    )
                    intent.putExtra(
                        "checkinDate",
                        listBookings[layoutPosition].getString("check_in_date")
                    )
                    intent.putExtra("statusBooking", this.statusBooking)
                    context.startActivity(intent)


                }
            }


        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    // convenience method for getting data at click position
    internal fun getItem(id: Int): String {
        return ""
    }

    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun revertDate(date: String): String {
        return date.substring(8,10) + "/" + date.substring(5,7) + "/" + date.substring(0,4)
    }

    fun allGuestsCOM(arrayGuests: JSONObject):Boolean {
            arrayGuests.getString("aggregated_status").let {
                    status ->
                if (status == "COMPLETE") {
                    return true
                }

            }
        return false
    }

    fun isNewBooking(arrayGuests: JSONObject):Boolean {
        arrayGuests.getString("aggregated_status").let {
                status ->
            if (status == "NEW") {
                return true
            }

        }
        return false
    }

    fun isInProgressBooking(arrayGuests: JSONObject):Boolean {
        arrayGuests.getString("aggregated_status").let {
                status ->
            if (status == "IN_PROGRESS") {
                return true
            }

        }
        return false
    }

    fun showDialogLoading() {

        MyDialog = Dialog(context)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.loading_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        progressBarLoading = MyDialog.findViewById(R.id.progressBarLoading) as ProgressBar
        MyDialog.setCancelable(false)
        MyDialog.setCanceledOnTouchOutside(false)



        MyDialog.show()

    }


}