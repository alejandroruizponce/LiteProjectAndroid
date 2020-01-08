package com.chekinlite.app.CurrentVersion.Activities

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.text.TextUtils
import android.view.Window
import android.widget.*
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.R


class Forgot_Password : AppCompatActivity() {

    lateinit var emailForgotEditText: EditText;
    lateinit var emailForgotView: RelativeLayout
    lateinit var buttonRecovery: Button
    lateinit var buttonError: Button
    lateinit var buttonBack: Button
    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog



    lateinit var loadingDialog: Dialog
    lateinit var progressBarLoading: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        emailForgotEditText = findViewById(R.id.typeSuscriptionEditText)
        emailForgotEditText.addTextChangedListener(CustomTextWatcher(emailForgotEditText))
        emailForgotView = findViewById(R.id.typeSuscriptionView)
        buttonRecovery = findViewById(R.id.buttonRecoveryPass)
        buttonRecovery.isClickable = false
        buttonRecovery.getBackground().setAlpha(128)


        buttonBack = findViewById(R.id.buttonBackAG)
        buttonBack.setOnClickListener {
            finish()
        }


        buttonRecovery.setOnClickListener {
            if (isValidEmail(emailForgotEditText.text)) {
                ChekinNewAPI.resetPassword(
                    this,
                    emailForgotEditText.text.toString()
                )
                buttonRecovery.setText("HECHO")
                buttonRecovery.isClickable = false
                buttonRecovery.getBackground().setAlpha(128)
            } else {
                showDialogError(0)
            }
        }

    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == emailForgotEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                emailForgotView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                emailForgotView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            checkFields()
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun checkFields() {
        val email = emailForgotEditText.text.toString();

        if (email.isNotBlank()) {
            buttonRecovery.isClickable = true
            buttonRecovery.getBackground().setAlpha(255);
        } else {
            buttonRecovery.isClickable = false
            buttonRecovery.getBackground().setAlpha(128);
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun showDialogLoading() {

        loadingDialog = Dialog(this)
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

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
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
            errorMessage.setText("El email introducido no es valido.")
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }
}
