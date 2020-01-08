package com.chekinlite.app.CurrentVersion.Activities

import android.os.Bundle
import android.widget.*
import android.support.v7.app.AppCompatActivity
import com.chekinlite.app.R


class MainActivity : AppCompatActivity() {

    lateinit var buttonGoTo: Button
    lateinit var titleBotton: TextView
    lateinit var titleBar: TextView
    var screenDisplayed = "LOGIN"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*
        titleBar = findViewById(R.id.titleBarMain) as TextView
        titleBotton = findViewById(R.id.titleBottom) as TextView


        buttonGoTo = findViewById<View>(R.id.buttonGoToRegister) as Button
        buttonGoTo.setOnClickListener {
            println("ACTIVAMOS EL BOTON DE REGISTRO")
                if (screenDisplayed == "LOGIN") {
                titleBar.setText(getString(R.string.registro))
                titleBotton.setText(getString(R.string.alreadyAccount))
                buttonGoTo.setText(getString(R.string.Entrar))

                val fragmentRegister = Fragment_Register()
                val ft = supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.fadein,
                    R.anim.fadeout, R.anim.fadein, R.anim.fadeout
                )
                ft.replace(R.id.fragment_frame_layout, fragmentRegister,  "FRAGMENT_REGISTER")
                ft.addToBackStack("loginFrag")
                ft.commit()
                screenDisplayed = "REGISTER"

            } else {
                println("ACTIVAMOS EL BOTON DE LOGIN")

                titleBar.setText(R.string.Entrar)
                titleBotton.setText(R.string.no_tienes_cuenta)
                buttonGoTo.setText(R.string.registrate)

                val fragmentLogin = Fragment_Login()
                val ft = supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.fadein,
                    R.anim.fadeout, R.anim.fadein, R.anim.fadeout
                )
                ft.replace(R.id.fragment_frame_layout, fragmentLogin, "FRAGMENT_LOGIN")
                ft.addToBackStack("loginFrag")
                ft.commit()
                screenDisplayed = "LOGIN"
            }




        };*/







    }





}
