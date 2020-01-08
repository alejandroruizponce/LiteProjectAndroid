package com.chekinlite.app.CurrentVersion.Models



import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import org.json.JSONObject
import android.content.Intent
import android.view.*
import android.widget.*
import com.chekinlite.app.CurrentVersion.Activities.AddGuest
import com.chekinlite.app.CurrentVersion.Activities.BookingDetails
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R


class TableGuestsController// data is passed into the constructor
internal constructor(context: Context,  var listGuests: ArrayList<JSONObject>, var bookingAux: JSONObject, var bookingDetailsActivity: BookingDetails) :
    RecyclerView.Adapter<TableGuestsController.ViewHolder>() {

    private val mInflater: LayoutInflater
    val context: Context= context
    private var mClickListener: ItemClickListener? = null
    private var focusedItem = 0

    lateinit var deleteMessage: TextView
    lateinit var deleteTitle: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonDelete: Button
    lateinit var buttonCancelDialog: Button

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.guestdetailcell, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        println("La posicion de la lista es: $position")

        listGuests[position].getJSONObject("statuses").let {
            statuses ->
            if (!statuses.isNull("police")) {
                val police = statuses.optJSONObject("police")
                police.getJSONObject("in").let {
                    inPolice ->
                    inPolice.getString("code").let {
                        status ->
                         if (status == "ERROR") {
                            holder.statusTitle.text = context.getString(R.string.erroneus)
                            holder.statusMessage.text = context.getString(R.string.errPolice)
                            holder.circleStatus.setImageResource(R.drawable.redcircle)

                             /*
                            holder.buttonCheckin.setText(context.getString(R.string.correct))
                            holder.buttonCheckin.setTextColor(Color.WHITE)
                            holder.buttonCheckin.setBackgroundResource(R.drawable.rounded_button)*/
                        } else if (status == "COMPLETE") {
                             holder.statusTitle.text = context.getString(R.string.completed)
                             holder.statusMessage.text = ""
                             holder.circleStatus.setImageResource(R.drawable.greencircle)
                         } else if (status == "NEW") {
                             holder.statusTitle.text = context.getString(R.string.newStatus)
                             holder.statusMessage.text = ""
                             holder.circleStatus.setImageResource(R.drawable.path_19041)
                         } else if (status == "IN PROGRESS") {
                             holder.statusTitle.text = context.getString(R.string.pending)
                             holder.statusMessage.text = context.getString(R.string.processing)
                             holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                         }
                    }

                }
            } else {
                holder.statusTitle.text = context.getString(R.string.pending)
                holder.statusMessage.text = context.getString(R.string.pendPolice)
                holder.circleStatus.setImageResource(R.drawable.yellowcircle)
                /*
                holder.buttonCheckin.setText(context.getString(R.string.correct))
                holder.buttonCheckin.setTextColor(Color.WHITE)
                holder.buttonCheckin.setBackgroundResource(R.drawable.rounded_button)*/
            }
        }

        listGuests[position].getString("name").let { name ->
            var nam = name.toLowerCase()
            listGuests[position].getString("surname").let { surn ->
                var surname = surn.split(" ")
                surname[0].let { surnFirst ->
                    var surnFinal = surnFirst.toLowerCase()
                    holder.guestName.setText("${capitalizeFirstLetter(nam)} ${capitalizeFirstLetter(surnFinal)}")
                }
            }
        }
    /*
        holder.buttonCheckin.setOnClickListener {
            // Handle item click and set the selection
                println("CELDA ${position} PULSADA")

            if(holder.buttonCheckin.text == "Corregir") {
                var i = -1
                for (property in UserProfile.listProperties) {
                    i++
                    if (bookingAux.getString("housing") == property.getString("id")) {
                        var intent = Intent(context, AddProperty::class.java)
                        intent.putExtra("propertyInfo", UserProfile.listProperties[i].toString())
                        intent.putExtra("EDIT_PROPERTY", true)
                        intent.putExtra("idGuest", listGuests[position].getString("legacy_id"))
                        context.startActivity(intent)
                    }
                }
            } else {
                ChekinAPI.getPartGuest(context, listGuests[position].getString("legacy_id")){
                    result, status ->
                    if(status) {
                        val pdfViewIntent = Intent(Intent.ACTION_VIEW)
                        pdfViewIntent.setDataAndType(Uri.parse(result?.getString("part_file_permalink")), "application/pdf")
                        pdfViewIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                        val intent = Intent.createChooser(pdfViewIntent, "Open File")
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // Instruct the user to install a PDF reader here, or something
                        }


                    }
                }
            }

        }*/


    }
    fun capitalizeFirstLetter(original: String?): String? {
        return if (original == null || original.length == 0) {
            original
        } else original.substring(0, 1).toUpperCase() + original.substring(1)
    }

    // total number of rows
    override fun getItemCount(): Int {
        return listGuests.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var guestName: TextView
        internal var statusTitle: TextView
        internal var statusMessage: TextView
        //internal var buttonCheckin: Button
        internal var circleStatus: ImageView
        internal var editGuestButton: ImageButton
        internal var removeGuestButton: ImageButton

        init {
            guestName = itemView.findViewById(R.id.guestNameCellBD)
            statusTitle = itemView.findViewById(R.id.statusTitleCellBD)
            statusMessage = itemView.findViewById(R.id.statusMessageCellBD)
            //buttonCheckin = itemView.findViewById(R.id.buttonStatusCellBD)
            circleStatus = itemView.findViewById(R.id.statusCircleCellBD)
            editGuestButton = itemView.findViewById(R.id.editGuestButtonCell)
            removeGuestButton = itemView.findViewById(R.id.removeGuestButtonCell)


            editGuestButton.setOnClickListener {
                var intent = Intent(context, AddGuest::class.java)




                bookingAux.getJSONObject("housing").let {
                    housing ->
                    housing.getJSONObject("location").let {
                        location ->
                        location.getJSONObject("country").let {
                            country ->
                            country.getString("code").let {
                                code ->
                                intent.putExtra("isNationalityProperty", code)
                            }
                        }
                    }
                }

                intent.putExtra("guestInfo", listGuests[layoutPosition].toString())
                bookingAux.getString("id").let {
                    idBook->
                    intent.putExtra("idBooking", idBook)
                }
                intent.putExtra("bookingInfo", bookingAux.toString())
                intent.putExtra("IS_EDIT_MODE", true)
                context.startActivity(intent);
            }

            removeGuestButton.setOnClickListener {
                showDialogDelete(layoutPosition)
            }


        }





        override fun onClick(view: View) {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(view, adapterPosition)

            }
        }
    }



    // convenience method for getting data at click position
    internal fun getItem(id: Int): String {
        print("Celda numero $id")
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

    fun showDialogDelete(index: Int) {

        MyDialog = Dialog(context)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.custom_delete_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonDelete = MyDialog.findViewById(R.id.buttonDeleteDialog) as Button
        buttonCancelDialog = MyDialog.findViewById(R.id.buttonCancelDeleteDialog)

        buttonDelete.setEnabled(true)
        deleteMessage = MyDialog.findViewById(R.id.messageDeleteDialog)
        deleteTitle = MyDialog.findViewById(R.id.titleDeleteDialog)

        deleteMessage.setText(listGuests[index].getString("name"))
        deleteTitle .setText(context.getString(R.string.deleteGuest))


        MyDialog.show()
        buttonCancelDialog.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonDelete.setOnClickListener{
            deleteGuest(index)
        }

    }

    fun deleteGuest(index: Int) {
        println("La ID del huesped a eliminar es: ${listGuests[index].getString("id")}")
        ChekinNewAPI.deleteGuest(
            context,
            listGuests[index].getString("id")
        )

        bookingDetailsActivity.listGuests.removeAt(index)
        bookingDetailsActivity.adapter?.notifyDataSetChanged()

        MyDialog.cancel()
        UserProfile.needBookingReload = true


    }




}