package com.chekinlite.app.CurrentVersion.Models


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import com.chekinlite.app.OldVersionFiles.ChekinAPI
import com.chekinlite.app.R
import org.json.JSONObject


class TableCollaboratorsController// data is passed into the constructor
internal constructor(context: Activity,  var listCollaborators: ArrayList<JSONObject>) :
    RecyclerView.Adapter<TableCollaboratorsController.ViewHolder>() {
    private val mInflater: LayoutInflater
    private val context: Activity= context
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
        val view = mInflater.inflate(R.layout.collaboratorcell, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        println("La posicion de la lista es: $position")
        holder.collaboratorName.text = listCollaborators[position].getString("name")

        holder.deleteButton.setOnClickListener {
           showDialogDelete(position)
        }


    }

    // total number of rows
    override fun getItemCount(): Int {
        return listCollaborators.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var collaboratorName: TextView
        internal var deleteButton: Button

        init {
            collaboratorName = itemView.findViewById(R.id.collaboratorName)
            deleteButton = itemView.findViewById(R.id.buttonRemoveCollaborator)



            // Handle item click and set the selection
            itemView.setOnClickListener {
                println("CELDA ${layoutPosition} PULSADA")

                val intent = Intent()
                intent.putExtra("responsible", listCollaborators[layoutPosition].getString("name"));
                context.setResult(101 , intent);
                context.finish()
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

        deleteMessage.setText(listCollaborators[index].getString("name"))
        deleteTitle .setText(context.getString(R.string.deleteCol))


        MyDialog.show()
        buttonCancelDialog.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonDelete.setOnClickListener{
            deleteCollaborator(index)
        }

    }

    fun deleteCollaborator(position: Int) {
        ChekinAPI.deleteColaborator(
            context as Activity,
            listCollaborators[position].getString("id")
        ) { result, status ->

            MyDialog.cancel()

            listCollaborators.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }




}