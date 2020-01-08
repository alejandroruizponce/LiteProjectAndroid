package com.chekinlite.app.CurrentVersion.Activities

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R

class SendChekinOnline : AppCompatActivity() {

    lateinit var emailChekinView: RelativeLayout

    lateinit var emailChekinEditText: EditText

    lateinit var buttonSendChekin: Button
    lateinit var buttonSendAndFinish: Button
    lateinit var buttonSendAndAddAnother: Button
    lateinit var buttonError: Button
    lateinit var MyDialog: Dialog
    lateinit var messageError: TextView
    lateinit var buttonBackSCO: Button


    var isNationalityProperty = ""
    var noCheckedGuests = 0
    var checkinDate = ""

    var idBooking = ""
    var fromAddGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_chekin_online)

        buttonSendChekin = findViewById(R.id.buttonSCO)
        buttonSendAndFinish = findViewById(R.id.buttonSendChekinAndFinishAG)
        buttonSendAndAddAnother = findViewById(R.id.buttonSendChekinAndAddAnotherAG)

        idBooking = intent.getStringExtra("idBooking")
        fromAddGuest = intent.getBooleanExtra("fromAddGuest", false)

        if (fromAddGuest) {
            isNationalityProperty = intent.getStringExtra("isNationalityProperty")
            checkinDate = intent.getStringExtra("checkinDate")
            noCheckedGuests = intent.getIntExtra("noCheckedGuests", 1)

            println("Quedan $noCheckedGuests huespedes por registrar")

            if (noCheckedGuests <= 1) {
                buttonSendAndAddAnother.visibility = View.GONE
            }
        }




        if (fromAddGuest) {
            buttonSendChekin.visibility = View.GONE
        } else {
            buttonSendAndFinish.visibility = View.GONE
            buttonSendAndAddAnother.visibility = View.GONE
        }


        buttonBackSCO = findViewById(R.id.buttonBackSCO)
        buttonBackSCO.setOnClickListener {
            finish()
        }

        buttonSendAndFinish.setOnClickListener {
            if(isValidEmail(emailChekinEditText.text)) {
                ChekinNewAPI.sendChekinOnline(
                    this,
                    idBooking,
                    emailChekinEditText.text.toString()
                ) { result, status ->
                    if (status) {
                        buttonSendAndFinish.setText(getString(R.string.sent))
                        buttonSendAndFinish.background.alpha = 128
                        buttonSendAndFinish.isClickable = false
                        hideKeyboard(this.currentFocus)
                        var intent =
                            Intent(
                                this,
                                NavigationAcitivity::class.java
                            )

                        startActivity(intent)
                        UserProfile.needBookingReload =
                            true
                    }
                }
            } else {
                showDialogError(0)
            }
        }

        buttonSendAndAddAnother.setOnClickListener {
            if(isValidEmail(emailChekinEditText.text)) {
                ChekinNewAPI.sendChekinOnline(
                    this,
                    idBooking,
                    emailChekinEditText.text.toString()
                ) { result, status ->
                    if (status) {
                        buttonSendAndAddAnother.setText(getString(R.string.sent))
                        buttonSendAndAddAnother.background.alpha = 128
                        buttonSendAndAddAnother.isClickable = false
                        hideKeyboard(this.currentFocus)
                        UserProfile.needBookingReload =
                            true
                        println("HUESPED AÑADIDO CON EXITO")

                        var intent = Intent(this, AddGuest::class.java)
                        intent.putExtra("isNationalityProperty", isNationalityProperty)
                        intent.putExtra("noCheckedGuests", noCheckedGuests - 1)
                        intent.putExtra("checkinDate", checkinDate)
                        intent.putExtra("idBooking", idBooking)
                        finish()
                        startActivity(intent);
                    }
                }
            } else {
                showDialogError(0)
            }
        }

        emailChekinView = findViewById(R.id.emailSCOView)
        emailChekinEditText = findViewById(R.id.emailSCOEditText)
        emailChekinEditText.addTextChangedListener(CustomTextWatcher(emailChekinEditText))
        emailChekinEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })



        buttonSendChekin.isClickable = false
        buttonSendChekin.background.alpha = 128
        buttonSendChekin.setOnClickListener {
            if(isValidEmail(emailChekinEditText.text)) {
                ChekinNewAPI.sendChekinOnline(
                    this,
                    idBooking,
                    emailChekinEditText.text.toString()
                ) { result, status ->
                    if (status) {
                        buttonSendChekin.setText(getString(R.string.sent))
                        buttonSendChekin.background.alpha = 128
                        buttonSendChekin.isClickable = false
                        hideKeyboard(this.currentFocus)
                    }
                }
            } else {
                showDialogError(0)
            }
        }

    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == emailChekinEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                emailChekinView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                emailChekinView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            buttonSendChekin.isClickable = true
            buttonSendChekin.background.alpha = 255
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun showDialogError(typeError: Int) {

        MyDialog = Dialog(this)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.custom_error_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonError = MyDialog.findViewById(R.id.buttonErrorDialog) as Button
        buttonError.setEnabled(true)
        messageError = MyDialog.findViewById(R.id.messageErrorDialog)
        if (typeError == 0) {
            messageError.setText(getString(R.string.errorEmail))
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }

    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}
