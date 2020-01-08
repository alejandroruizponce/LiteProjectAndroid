package com.chekinlite.app.OldVersionFiles

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
import com.chekinlite.app.R
import com.yinglan.keyboard.HideUtil

class AddCollaborator : AppCompatActivity() {

    lateinit var emailView: RelativeLayout
    lateinit var phoneView: RelativeLayout
    lateinit var nameView: RelativeLayout

    lateinit var emailEditText: EditText
    lateinit var phoneEditText: EditText
    lateinit var nameEditText: EditText

    lateinit var buttonInviteCol: Button
    lateinit var buttonBack: Button

    lateinit var buttonError: Button
    lateinit var MyDialog: Dialog
    lateinit var errorMessage: TextView

    var idUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collaborator)

        HideUtil.init(this);

        ChekinAPI.getUserProfile(this) { result, status ->
            if (status) {
                if (result != null) {
                    idUser = result.getString("id")
                }
            }
        }

        emailView = findViewById(R.id.emailNewColView)
        emailEditText = findViewById(R.id.emailNewColEditText)
        emailEditText.addTextChangedListener(CustomTextWatcher(emailEditText))
        emailEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        phoneView = findViewById(R.id.phoneNewColView)
        phoneEditText = findViewById(R.id.phoneNewColEditText)
        phoneEditText.addTextChangedListener(CustomTextWatcher(phoneEditText))
        phoneEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        nameView = findViewById(R.id.nameNewColView)
        nameEditText = findViewById(R.id.nameNewColEditText)
        nameEditText.addTextChangedListener(CustomTextWatcher(nameEditText))
        nameEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        buttonBack = findViewById(R.id.buttonBackNewCol)
        buttonBack.setOnClickListener {
            finish()
        }

        buttonInviteCol = findViewById(R.id.buttonInviteColaborator)
        buttonInviteCol.isClickable = false
        buttonInviteCol.background.alpha = 128
        buttonInviteCol.setOnClickListener {
            if (checkErrors()) {
                ChekinAPI.addCollaborator(
                    this,
                    idUser,
                    emailEditText.text.toString(),
                    nameEditText.text.toString(),
                    phoneEditText.text.toString()
                ) { result, status ->
                    if (status) {
                        val intent = Intent()
                        setResult(100, intent);
                        finish()
                    }
                }
            }
        }



    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == emailEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                emailView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                emailView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == phoneEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                phoneView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                phoneView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == nameEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nameView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            checkFields()
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun checkFields(): Boolean {
        if (emailEditText.text.isNullOrBlank() || phoneEditText.text.isNullOrBlank() || nameEditText.text.isNullOrBlank()) {
            buttonInviteCol.isClickable = false
            buttonInviteCol.background.alpha = 128
            return false
        } else {
            buttonInviteCol.isClickable = true
            buttonInviteCol.background.alpha = 255

        }
        return true
    }

    fun checkErrors(): Boolean {
        if (!isValidEmail(emailEditText.text.toString())) {
            showDialogError(0)
            return false
        } else {
            if (!isValidPhone(phoneEditText.text.toString())) {
                showDialogError(1)
                return false
            }
        }
        return true
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun isValidPhone(phone: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phone).matches()
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
            errorMessage.setText(getString(R.string.errorEmail))
        } else if (typeError == 1) {
            errorMessage.setText(getString(R.string.errorPhone))
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }

}
