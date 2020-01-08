package com.chekinlite.app.OldVersionFiles

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chekinlite.app.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class GuestDocumentViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val mTextView: TextView
    private val buttonPDFGuest: Button
    lateinit var id: String
    lateinit var  context: Context

    init {
        mTextView = itemView.findViewById(R.id.guestNameCellGuestDocument)
        buttonPDFGuest = itemView.findViewById(R.id.buttonPDFCellGuestDocument)


        buttonPDFGuest.setOnClickListener {
                ChekinAPI.getPartGuest(context, id) {
                    result, status ->
                        if (status) {
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


    }

    fun bind(product: GuestDocument) {
        mTextView.text = product.name
        id = product.id
        context = product.context
    }
}


