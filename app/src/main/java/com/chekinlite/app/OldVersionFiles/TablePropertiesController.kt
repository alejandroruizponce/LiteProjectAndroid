package com.chekinlite.app.OldVersionFiles


import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import org.json.JSONObject
import android.content.Intent
import android.view.*
import android.widget.*
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R


class TablePropertiesController// data is passed into the constructor
internal constructor(context: Context,  var listProperties: ArrayList<JSONObject>) :
    RecyclerView.Adapter<TablePropertiesController.ViewHolder>() {
    private val mInflater: LayoutInflater
    private val context: Context= context
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
        val view = mInflater.inflate(R.layout.propertiecell, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        println("La posicion de la lista es: $position")
        holder.propertyName.text = listProperties[position].getString("tradename")
        holder.propertyAddress.text = listProperties[position].getString("address")
        holder.propertyLocation.text = listProperties[position].getString("municipality")

        holder.deleteButton.setOnClickListener {
            showDialogDelete(position)
        }

    }

    // total number of rows
    override fun getItemCount(): Int {
        return listProperties.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var propertyName: TextView
        internal var propertyLocation: TextView
        internal var propertyAddress: TextView
        internal var deleteButton: ImageButton
        
        init {
            propertyName = itemView.findViewById(R.id.namePropertyCell)
            propertyLocation = itemView.findViewById(R.id.locationPropertyCell)
            propertyAddress = itemView.findViewById(R.id.addressPropertyCell)
            deleteButton = itemView.findViewById(R.id.deletePropertyButtonCell)


            // Handle item click and set the selection
            itemView.setOnClickListener {
                println("CELDA ${layoutPosition} PULSADA")

                var intent = Intent(context, AddProperty::class.java)
                intent.putExtra("propertyInfo", listProperties[layoutPosition].toString())
                intent.putExtra("EDIT_PROPERTY", true)
                context.startActivity(intent)


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

        deleteMessage.setText(listProperties[index].getString("tradename"))
        deleteTitle .setText(context.getString(R.string.deleteProperty))


        MyDialog.show()
        buttonCancelDialog.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonDelete.setOnClickListener{
            deleteProperty(index)
        }

    }

    fun deleteProperty(index: Int) {
        ChekinAPI.deleteProperty(context, listProperties[index].getInt("id").toString())

        var intent = Intent(context, NavigationAcitivity::class.java)
        intent.putExtra("selectedTab", 1)

        context.startActivity(intent)
        UserProfile.needBookingReload = true


    }




}