package com.chekinlite.app.OldVersionFiles

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.chekinlite.app.R
import com.yinglan.keyboard.HideUtil
import org.json.JSONObject

class AccountDetails : AppCompatActivity() {

    lateinit var userProfile: JSONObject

    lateinit var emailView: RelativeLayout
    lateinit var phoneView: RelativeLayout
    lateinit var currentPassView: RelativeLayout
    lateinit var newPassView: RelativeLayout
    lateinit var repeatPassView: RelativeLayout

    lateinit var emailEditText: EditText
    lateinit var phoneEditText: EditText
    lateinit var currentPassEditText: EditText
    lateinit var newPassEditText: EditText
    lateinit var repeatPassEditText: EditText

    lateinit var buttonBack: Button
    lateinit var buttonSave: Button

    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonError: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)
        HideUtil.init(this);

        buttonBack = findViewById(R.id.buttonBackAccount)
        buttonBack.setOnClickListener {
            finish()
        }
        buttonSave = findViewById(R.id.buttonSaveChangesAccount)
        buttonSave.background.alpha = 128
        buttonSave.isClickable = false
        buttonSave.setOnClickListener{
            if (newPassEditText.text.toString() == repeatPassEditText.text.toString()) {
                ChekinAPI.changePassword(
                    this,
                    currentPassEditText.text.toString(),
                    newPassEditText.text.toString()
                ) { result, status ->
                    if (status) {
                        buttonSave.background.alpha = 128
                        buttonSave.isClickable = false
                        buttonSave.setText(getString(R.string.guardado))
                    }
                }

            } else {
                showDialogError(0)
            }
        }


        ChekinAPI.getUserProfile(this) { result, status ->
            if (status) {
                if (result != null) {
                    userProfile = result
                    loadProfile()
                }
            }
        }

        currentPassView = findViewById(R.id.currentPassAccountView)
        currentPassEditText = findViewById(R.id.currentPassAccountEditText)
        currentPassEditText.addTextChangedListener(CustomTextWatcher(currentPassEditText))
        currentPassEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        newPassView = findViewById(R.id.newPassAccountView)
        newPassEditText = findViewById(R.id.newPassAccountEditText)
        newPassEditText.addTextChangedListener(CustomTextWatcher(newPassEditText))
        newPassEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        repeatPassView = findViewById(R.id.repeatPassAccountView)
        repeatPassEditText = findViewById(R.id.repeatPassAccountEditText)
        repeatPassEditText.addTextChangedListener(CustomTextWatcher(repeatPassEditText))
        repeatPassEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })




    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == currentPassEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                currentPassView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                currentPassView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == newPassEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                newPassView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                newPassView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == repeatPassEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                repeatPassView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                repeatPassView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            checkFields()
        }

        override fun afterTextChanged(s: Editable) {

        }
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
        errorMessage = MyDialog.findViewById(R.id.messageErrorDialog)
        if (typeError == 0) {
            errorMessage.setText(getString(R.string.passwordsRepeat))
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }

    fun checkFields(): Boolean {
        if (newPassEditText.text.isNullOrBlank() || currentPassEditText.text.isNullOrBlank() || repeatPassEditText.text.isNullOrBlank()) {
            buttonSave.background.alpha = 128
            buttonSave.isClickable = false
            return false
        }
        buttonSave.background.alpha = 255
        buttonSave.isClickable = true
        return true
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun loadProfile() {
        emailView = findViewById(R.id.emailAccountView)
        emailEditText = findViewById(R.id.emailAccountEditText)
        emailEditText.setText(userProfile.getString("email"))
        emailEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        emailView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        emailView.setBackgroundColor(Color.parseColor("#ffffff"))

        phoneView = findViewById(R.id.phoneAccountView)
        phoneEditText = findViewById(R.id.phoneAccountEditText)
        phoneEditText.setText(userProfile.getString("phone"))
        phoneEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        phoneView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        phoneView.setBackgroundColor(Color.parseColor("#ffffff"))
    }
}
