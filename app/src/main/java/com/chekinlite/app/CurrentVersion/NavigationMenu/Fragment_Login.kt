package com.chekinlite.app.CurrentVersion.NavigationMenu


import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Activities.Forgot_Password
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.yinglan.keyboard.HideUtil


class Fragment_Login : Fragment() {

    lateinit var userLoginEditText: EditText;
    lateinit var passLoginEditext: EditText;
    lateinit var buttonLogin: Button
    lateinit var buttonForgot: Button
    lateinit var buttonPMS: Button
    lateinit var userLoginView: RelativeLayout;
    lateinit var passLoginView: RelativeLayout;
    lateinit var userEmail: String
    lateinit var password: String

    lateinit var bookingView: RelativeLayout;
    lateinit var guestyView: RelativeLayout;
    lateinit var cloudbedsView: RelativeLayout;

    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonError: Button

    lateinit var loadingDialog: Dialog
    lateinit var progressBarLoading: ProgressBar


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        HideUtil.init(activity);


        var sharedPreference = activity?.getSharedPreferences("LOGIN_CREDENTIALS", Context.MODE_PRIVATE)




        userLoginEditText = view.findViewById(R.id.userLoginEditext) as EditText
        userLoginEditText.addTextChangedListener(CustomTextWatcher(userLoginEditText))

        passLoginEditext = view.findViewById<View>(R.id.passLoginEditext) as EditText
        passLoginEditext.addTextChangedListener(CustomTextWatcher(passLoginEditext))



        buttonLogin = view.findViewById<View>(R.id.buttonLogin) as Button
        buttonLogin.setClickable(false)



        buttonLogin.setOnClickListener {
            showDialogLoading()
            activity?.let { it1 ->
                ChekinNewAPI.loginUser(
                    it1,
                    userEmail,
                    password
                ) { result, status ->
                    if (status) {
                        var editor = sharedPreference?.edit()
                        editor?.putString("username", userEmail)
                        editor?.putString("password", password)

                        editor?.putBoolean("Logged", true)

                        editor?.commit()
                        (result?.get("token") as? String).let { token ->
                            if (token != null) {
                                UserProfile.token =
                                    "JWT " + token
                                editor?.putString("token", "JWT " + token)
                                editor?.commit()
                                println("El token ha sido guardado ${UserProfile.token}")
                                UserProfile.needBookingReload =
                                    true
                                val intent = Intent(
                                    activity,
                                    NavigationAcitivity::class.java
                                )
                                // start your next activity
                                startActivity(intent)
                            }
                        }
                    } else {
                        showDialogError(0)
                        hideLoadingDialog()
                    }
                }
            }
        }

        buttonPMS = view.findViewById(R.id.buttonPMS) as Button
        buttonPMS.setOnClickListener {
            showDialogPMS()
        }




        buttonForgot = view.findViewById(R.id.buttonForgotPass)
        buttonForgot.setOnClickListener {
            val intent = Intent(activity, Forgot_Password::class.java)
            // To pass any data to next activity
            startActivity(intent)
        }


        userLoginEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        passLoginEditext.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        userLoginView = view.findViewById<View>(R.id.userLoginView) as RelativeLayout
        passLoginView = view.findViewById<View>(R.id.passLoginView) as RelativeLayout

        if (sharedPreference?.getString("username", "") != "" && sharedPreference?.getString("password", "") != "") {
            userLoginEditText.setText(sharedPreference?.getString("username", ""))
            userLoginEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            userLoginView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            userLoginView.setBackgroundColor(Color.parseColor("#ffffff"))

            passLoginEditext.setText(sharedPreference?.getString("password", ""))
            passLoginEditext.setBackgroundResource(R.drawable.edittext_bottom_line)
            passLoginView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            passLoginView.setBackgroundColor(Color.parseColor("#ffffff"))

            checkFields()
        }


        return view


    }





    fun checkFields() {
        val user = userLoginEditText.text.toString();
        val pass = passLoginEditext.text.toString()
        if (user.isNotBlank() && pass.isNotBlank()) {
            userEmail = user
            password = pass
            //buttonLogin.isClickable = true
            //buttonLogin.getBackground().setAlpha(255);
        } else {
            //buttonLogin.isClickable = false
            //buttonLogin.getBackground().setAlpha(128);
        }
    }

    fun showDialogLoading() {

        loadingDialog = Dialog(activity)
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

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == userLoginEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                userLoginView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                userLoginView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == passLoginEditext) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                passLoginView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                passLoginView.setBackgroundColor(Color.parseColor("#ffffff"))
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
            errorMessage.setText(getString(R.string.errorLogin))
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }

    fun showDialogPMS() {

        MyDialog = Dialog(activity)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.loginwith_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonError = MyDialog.findViewById(R.id.buttonCancelPMS) as Button


        MyDialog.show()

        bookingView = MyDialog.findViewById(R.id.pmsBooking)
        bookingView.setOnClickListener {
            Toast.makeText(getActivity(), getString(R.string.comingSoon),
                Toast.LENGTH_SHORT).show();
        }

        guestyView = MyDialog.findViewById(R.id.pmsGuesty)
        guestyView.setOnClickListener {
            Toast.makeText(getActivity(), getString(R.string.comingSoon),
                Toast.LENGTH_SHORT).show();
        }

        cloudbedsView = MyDialog.findViewById(R.id.pmsCloudbeds)
        cloudbedsView.setOnClickListener {
            Toast.makeText(getActivity(), getString(R.string.comingSoon),
                Toast.LENGTH_SHORT).show();
        }

        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }






}
