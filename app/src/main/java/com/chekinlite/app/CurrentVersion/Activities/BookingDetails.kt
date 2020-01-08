package com.chekinlite.app.CurrentVersion.Activities

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.widget.*
import org.json.JSONObject
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.CurrentVersion.Models.TableGuestsController
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingDetails : AppCompatActivity(),
    TableGuestsController.ItemClickListener {
    override fun onItemClick(view: View, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var recyclerView: RecyclerView
    var adapter: TableGuestsController? = null
    var listGuests  = ArrayList<JSONObject>()
    lateinit var bookingAux: JSONObject
    lateinit var idBook: String
    var noCheckedGuests: Int = 0
    lateinit var checkinDate: String
    var statusBooking: Int = 0
    var typeBooking = ""
    var isNationalityProperty = ""
    lateinit var bookingInfo: JSONObject

    lateinit var titleBarText: TextView
    lateinit var statusBarText: TextView
    lateinit var circleStatusBar: ImageView
    lateinit var propertyName: TextView
    lateinit var bookingID: TextView
    lateinit var chekinOnlineLink: TextView
    lateinit var sendToPoliceLabel: TextView

    lateinit var buttonAddGuestExtra: ImageButton
    lateinit var buttonBack: Button
    lateinit var buttonSendChekin: ImageButton
    lateinit var buttonDeleteBooking: ImageButton

    lateinit var progressBar: ProgressBar

    lateinit var deleteMessage: TextView
    lateinit var deleteTitle: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonDelete: Button
    lateinit var buttonSendNowToPolice: Button
    lateinit var buttonCancelDialog: Button
    lateinit var buttonCopyLink: Button
    lateinit var buttonEditBooking: ImageButton


    lateinit var errorMessage: TextView
    lateinit var errorDialog: Dialog
    lateinit var buttonError: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        UserProfile.tabIsDisplayed = -1

        progressBar = findViewById(R.id.progressBarBookingDetails)

        sendToPoliceLabel = findViewById(R.id.sendtopoliceLabel)
        buttonSendNowToPolice = findViewById(R.id.sendNowToPoliceButton)
        buttonSendNowToPolice.setOnClickListener {
            ChekinNewAPI.sendToPoliceManually(
                this,
                idBook
            ) { result, status ->
                if (status) {
                    buttonSendNowToPolice.setText(getString(R.string.sent))
                    buttonSendNowToPolice.alpha = 0.5f
                    buttonSendNowToPolice.isClickable = false


                    UserProfile.needBookingReload =
                        true
                    UserProfile.needBookingDetailsReload =
                        true
                }
            }
        }

        buttonBack = findViewById(R.id.buttonBackBD)
        buttonBack.setOnClickListener {
            finish()
        }

        buttonDeleteBooking = findViewById(R.id.buttonDeleteBooking)
        buttonDeleteBooking.setOnClickListener {
            showDialogDelete()
        }

        buttonEditBooking = findViewById(R.id.editBookingButton)
        buttonEditBooking.setOnClickListener {
            var intent = Intent(this, EditChekin::class.java)
            intent.putExtra("bookingInfo", bookingAux.toString())
            startActivity(intent);
        }


        buttonAddGuestExtra = findViewById(R.id.addExtraGuestButton)
        buttonAddGuestExtra.setOnClickListener {
            if (noCheckedGuests == 0) {
                showDialogMaximumGuests()
            } else {
                var intent = Intent(this, AddGuest::class.java)
                intent.putExtra("isNationalityProperty", isNationalityProperty)
                intent.putExtra("noCheckedGuests", noCheckedGuests)
                intent.putExtra("checkinDate", checkinDate)
                intent.putExtra("typeBooking", typeBooking)
                intent.putExtra("idBooking", idBook)
                intent.putExtra("bookingInfo", bookingAux.toString())
                startActivity(intent);
            }
        }

        bookingAux = JSONObject(intent.getStringExtra("booking"))
        idBook = intent.getStringExtra("idBook")
        noCheckedGuests = intent.getIntExtra("noCheckedGuests", 0)
        checkinDate = intent.getStringExtra("checkinDate")
        statusBooking = intent.getIntExtra("statusBooking", 0)
        isNationalityProperty = intent.getStringExtra("isNationalityProperty")
        bookingInfo = JSONObject(intent.getStringExtra("bookingInfo"))


        buttonSendChekin = findViewById(R.id.buttonSendChekinOnline)
        buttonSendChekin.setOnClickListener {
            var intent = Intent(this, SendChekinOnline::class.java)
            intent.putExtra("idBooking", idBook)
            startActivity(intent)
        }


        titleBarText = findViewById(R.id.titleGuestBD)

        bookingAux.getJSONObject("housing").let { housing ->
            titleBarText.setText(housing.getString("name"))
            statusBarText = findViewById(R.id.statusBookingTitleBD)
            circleStatusBar = findViewById(R.id.circleStatusBookingTitleBD)
            bookingID = findViewById(R.id.bookingIDtextBD)
            bookingID.setText(idBook)
        }

        chekinOnlineLink = findViewById(R.id.chekinOnlineLinkLabel)
        bookingAux.getString("signup_form_link").let {
                signUp->
            chekinOnlineLink.setText(signUp)
        }

        buttonCopyLink = findViewById(R.id.buttonCopyLink)
        buttonCopyLink.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("signup_form_link", chekinOnlineLink.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.linkCopied),
                Toast.LENGTH_SHORT).show();
        }


        bookingAux.getString("aggregated_status").let {aggStatus ->
            if(aggStatus == "COMPLETE") {
                statusBarText.setText(getString(R.string.comBooking))
                circleStatusBar.setImageResource(R.drawable.greencircle)
                buttonSendNowToPolice.visibility = View.GONE
                sendToPoliceLabel.visibility = View.GONE
            } else if (aggStatus == "NEW") {
                if (!isDatePreviousToToday(bookingAux.optString("check_in_date").substring(0, 10))) {
                    buttonSendNowToPolice.visibility = View.GONE
                    sendToPoliceLabel.visibility = View.GONE
                }
                statusBarText.setText(getString(R.string.newBooking))
                circleStatusBar.setImageResource(R.drawable.path_19041)
            } else if (aggStatus == "ERROR") {
                statusBarText.setText(getString(R.string.errBooking))
                circleStatusBar.setImageResource(R.drawable.redcircle)
            }else {
                buttonSendNowToPolice.visibility = View.GONE
                sendToPoliceLabel.visibility = View.GONE
                statusBarText.setText(getString(R.string.incomBooking))
                circleStatusBar.setImageResource(R.drawable.yellowcircle)
            }
        }


        bookingAux.getJSONObject("guest_group").let {gGroup ->
            gGroup.getJSONArray("members").let {members ->
                for (i in 0..members.length() - 1) {
                    listGuests?.add(members[i] as JSONObject)
                }
                adapter?.notifyDataSetChanged()
                progressBar.visibility = View.INVISIBLE
            }
        }


            // set up the RecyclerView
            val recView = findViewById<RecyclerView>(R.id.tableGuestBookingDetails)
            if (recView != null) {
                recyclerView = recView
            }
            recyclerView?.setLayoutManager(LinearLayoutManager(this))

            adapter = this.let { listGuests?.let { it1 ->
                TableGuestsController(
                    it,
                    it1,
                    bookingAux,
                    this
                )
            } }
            adapter?.setClickListener(this)
            recyclerView?.setAdapter(adapter)


    }

    override fun onResume() {
        super.onResume()

        if (UserProfile.needBookingDetailsReload) {
            progressBar.visibility = View.VISIBLE
            listGuests.clear()
            ChekinNewAPI.getOneBooking(
                this,
                UserProfile.bookingDetailsUpdatedID
            ) { result, status ->
                if (status) {
                    if (result != null) {
                        result.getJSONObject("guest_group").let { guestG ->
                            guestG.getJSONArray("members").let { members ->
                                for (i in 0..members.length() - 1) {
                                    listGuests?.add(members[i] as JSONObject)
                                }
                                adapter?.notifyDataSetChanged()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    fun deleteBooking() {
        ChekinNewAPI.deleteBooking(
            this,
            idBook
        )

        var intent = Intent(this, NavigationAcitivity::class.java)
        intent.putExtra("selectedTab", 0)
        startActivity(intent)
        UserProfile.needBookingReload = true


    }

    fun isDatePreviousToToday (dateIn: String): Boolean {
        if (SimpleDateFormat("yyyy-MM-dd").parse(dateIn).before(Date())) {
            bookingInfo.getJSONObject("housing").let { housing ->
                housing.getJSONObject("location").let { location ->
                    location.getJSONObject("country").let { country ->
                        country.getString("code").let { code ->
                            if (code != "AE") {
                                return true
                            } else {
                                return false
                            }
                        }
                    }
                }
            }
        } else {
            return false
        }
        return true
    }

    fun showDialogDelete() {

        MyDialog = Dialog(this)
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

        bookingAux.getJSONObject("housing").let { housing ->
            deleteMessage.setText(housing.getString("name"))
            deleteTitle.setText(getString(R.string.deleteBooking))
        }


        MyDialog.show()
        buttonCancelDialog.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonDelete.setOnClickListener{
            deleteBooking()
        }


    }

    fun showDialogMaximumGuests() {

        MyDialog = Dialog(this)
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
        buttonDelete.setText(getString(R.string.anadir))

        deleteMessage.setText(R.string.maxGuests)

        deleteTitle .setText(getString(R.string.sure))


        MyDialog.show()
        buttonCancelDialog.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonDelete.setOnClickListener{
            var intent = Intent(this, AddGuest::class.java)
            intent.putExtra("isNationalityProperty", isNationalityProperty)
            intent.putExtra("noCheckedGuests", noCheckedGuests + 1)
            intent.putExtra("checkinDate", checkinDate)
            intent.putExtra("typeBooking", typeBooking)
            intent.putExtra("idBooking", idBook)
            intent.putExtra("isGuestExtra", true)
            intent.putExtra("bookingInfo", bookingInfo.toString())
            startActivity(intent);
        }

    }

}
