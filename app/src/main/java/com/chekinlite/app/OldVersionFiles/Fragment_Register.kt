package com.chekinlite.app.OldVersionFiles

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.FragmentActivity
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.text.TextUtils
import android.view.Window
import android.widget.*
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R


class Fragment_Register : Fragment() {


    lateinit var myContext: FragmentActivity
    lateinit var userRegisterView: RelativeLayout
    lateinit var nameRegisterView: RelativeLayout
    lateinit var phoneRegisterView: RelativeLayout
    lateinit var passRegisterView: RelativeLayout
    lateinit var repeatpassRegisterView: RelativeLayout

    lateinit var userRegisterEditText: EditText
    lateinit var nameRegisterEditext: EditText
    lateinit var phoneRegisterEditext: EditText
    lateinit var passRegisterEditext: EditText
    lateinit var repeatpassRegisterEditext: EditText

    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonError: Button

    lateinit var TyCSwitcher: CheckBox
    lateinit var PrivacySwitcher: CheckBox

    lateinit var buttonRegister: Button
    lateinit var buttonTyC: Button
    lateinit var buttonPrivacy: Button
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "lite-variables"


    override fun onAttach(activity: Activity?) {
        myContext = activity as FragmentActivity
        super.onAttach(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_register, container, false)
        val sharedPref: SharedPreferences = activity!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        userRegisterView = view.findViewById(R.id.typeSuscriptionView) as RelativeLayout
        userRegisterEditText = view.findViewById(R.id.typeSuscriptionEditText) as EditText
        userRegisterEditText.addTextChangedListener(CustomTextWatcher(userRegisterEditText))
        userRegisterEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        nameRegisterView = view.findViewById(R.id.nameRegisterView) as RelativeLayout
        nameRegisterEditext = view.findViewById<View>(R.id.nameRegisterEditext) as EditText
        nameRegisterEditext.addTextChangedListener(CustomTextWatcher(nameRegisterEditext))
        nameRegisterEditext.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        phoneRegisterView = view.findViewById(R.id.phoneRegisterView) as RelativeLayout
        phoneRegisterEditext = view.findViewById<View>(R.id.phoneRegisterEditext) as EditText
        phoneRegisterEditext.addTextChangedListener(CustomTextWatcher(phoneRegisterEditext))
        phoneRegisterEditext.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        passRegisterView = view.findViewById(R.id.passRegisterView) as RelativeLayout
        passRegisterEditext = view.findViewById<View>(R.id.passRegisterEditext) as EditText
        passRegisterEditext.addTextChangedListener(CustomTextWatcher(passRegisterEditext))
        passRegisterEditext.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        repeatpassRegisterView = view.findViewById(R.id.repeatpassRegisterView) as RelativeLayout
        repeatpassRegisterEditext = view.findViewById<View>(R.id.repeatpassRegisterEditext) as EditText
        repeatpassRegisterEditext.addTextChangedListener(CustomTextWatcher(repeatpassRegisterEditext))
        repeatpassRegisterEditext.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


        buttonRegister = view.findViewById(R.id.buttonRegister) as Button
        buttonRegister.isClickable = false
        buttonRegister.getBackground().setAlpha(128);
        buttonRegister.setOnClickListener {
            if (isValidEmail(userRegisterEditText.text)) {
                if (isValidPhone(phoneRegisterEditext.text)) {
                    if (isValidPassword(passRegisterEditext.text)) {
                        val pass1 = passRegisterEditext.text
                        val pass2 = repeatpassRegisterEditext.text
                        println("$pass1 - $pass2")
                        if (pass1.toString() == pass2.toString()) {
                            activity?.let{ it1 -> ChekinAPI.registerUser(it1, userRegisterEditText.text.toString(), nameRegisterEditext.text.toString(), phoneRegisterEditext.text.toString(), passRegisterEditext.text.toString()) {
                                result, status ->
                                if (status) {
                                    (result?.get("token") as? String).let { token ->
                                        if (token != null) {
                                            UserProfile.token = token as String
                                            println("El token ha sido guardado ${UserProfile.token}")
                                            val editor = sharedPref.edit()
                                            editor.putString("token", token)
                                            editor.commit()
                                            val intent = Intent(activity, SuscriptionActivity::class.java)
                                            // start your next activity
                                            startActivity(intent)

                                        }
                                    }
                                } else {
                                    showDialogError(4)
                                }
                            } }
                        } else {
                            showDialogError(3)
                        }
                    } else {
                        showDialogError(2)
                    }
                } else {
                    showDialogError(1)
                }
            } else {
                showDialogError(0)
            }
        }

        TyCSwitcher = view.findViewById(R.id.TyCSwitcher)
        TyCSwitcher.setOnClickListener {
            checkFields()
        }

        PrivacySwitcher = view.findViewById(R.id.PrivacySwitcher)
        PrivacySwitcher.setOnClickListener {
            checkFields()
        }

        buttonTyC = view.findViewById(R.id.buttonTyC)
        buttonTyC.setOnClickListener {
            val intent = Intent(activity, TermsAndConditionsActivity::class.java)
            intent.putExtra("typeTerms", 0)
            // To pass any data to next activity
            startActivityForResult(intent, 1000)

        }

        buttonPrivacy = view.findViewById(R.id.buttonPolicyPrivacy)
        buttonPrivacy.setOnClickListener {
            val intent = Intent(activity, TermsAndConditionsActivity::class.java)
            intent.putExtra("typeTerms", 1)
            // To pass any data to next activity
            startActivityForResult(intent, 1000)

        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 200) {
            TyCSwitcher.isChecked = true
            checkFields()
        } else if (resultCode == 201) {
            PrivacySwitcher.isChecked = true
            checkFields()
        }
    }

    fun checkFields() {
        val user = userRegisterEditText.text.toString();
        val name = nameRegisterEditext.text.toString()
        val phone = phoneRegisterEditext.text.toString();
        val pass = passRegisterEditext.text.toString()
        val repPass = repeatpassRegisterEditext.text.toString()
        if (user.isNotBlank() && pass.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && pass.isNotBlank() && repPass.isNotBlank() && TyCSwitcher.isChecked && PrivacySwitcher.isChecked) {
            buttonRegister.isClickable = true
            buttonRegister.getBackground().setAlpha(255);
        } else {
            buttonRegister.isClickable = false
            buttonRegister.getBackground().setAlpha(128);
        }
    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == userRegisterEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                userRegisterView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                userRegisterView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == nameRegisterEditext) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nameRegisterView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nameRegisterView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == phoneRegisterEditext) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                phoneRegisterView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                phoneRegisterView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == passRegisterEditext) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                passRegisterView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                passRegisterView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == repeatpassRegisterEditext) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                repeatpassRegisterView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                repeatpassRegisterView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            checkFields()
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun showDialogError(typeError: Int) {

        MyDialog = Dialog(activity)
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
        } else if (typeError == 2) {
            errorMessage.setText(getString(R.string.passwordMin))
        } else if (typeError == 3) {
            errorMessage.setText(getString(R.string.passwordsRepeat))
        } else if (typeError == 4) {
            errorMessage.setText(getString(R.string.userAlreadyRegistered))
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

    fun isValidPhone(phone: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phone).matches()
        }
    }

    fun isValidPassword(password: CharSequence) : Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }





}
