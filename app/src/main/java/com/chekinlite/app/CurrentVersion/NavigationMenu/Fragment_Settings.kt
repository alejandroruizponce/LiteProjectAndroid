package com.chekinlite.app.CurrentVersion.NavigationMenu


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.chekinlite.app.CurrentVersion.Activities.MainActivity
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.OldVersionFiles.ChekinAPI
import com.chekinlite.app.R
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration




class Fragment_Settings : Fragment() {

    lateinit var closeSessionButton: Button


    lateinit var accountView: ConstraintLayout
    lateinit var collaboratorsView: ConstraintLayout
    lateinit var chatView: ConstraintLayout
    lateinit var helpCenterView: ConstraintLayout

    var emailUser = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        activity?.let {
            ChekinAPI.getUserProfile(it) { result, status ->
                if (status) {
                    if (result != null) {
                        emailUser = result.getString("email")
                    }
                }
            }
        }

        UserProfile.tabIsDisplayed = 4

        Intercom.initialize(activity?.application, "android_sdk-61d6b235c336aa716dc8637d1bd714181c4a7f04", "qngfmf4n");

        /*
        accountView = view.findViewById(R.id.accountView)
        accountView.setOnClickListener {
            var intent = Intent(activity, AccountDetails::class.java)
            startActivity(intent)
        }

        collaboratorsView = view.findViewById(R.id.collaboratorsView)
        collaboratorsView.setOnClickListener {
            var intent = Intent(activity, CollaboratorsActivity::class.java)
            intent.putExtra("LIST_COLLABORATORS", true)
            startActivity(intent)
        }*/

        closeSessionButton = view.findViewById(R.id.buttonCloseSession)
        closeSessionButton.setOnClickListener {
            var sharedPreference = activity?.getSharedPreferences("LOGIN_CREDENTIALS", Context.MODE_PRIVATE)
            var editor = sharedPreference?.edit()
            editor?.remove("Logged")
            editor?.commit()

            val intent = Intent(activity, MainActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        chatView = view.findViewById(R.id.chatView)
        chatView.setOnClickListener {
            val registration = Registration.create().withEmail(emailUser)
            Intercom.client().registerIdentifiedUser(registration)
            Intercom.client().displayMessenger()
        }

        helpCenterView = view.findViewById(R.id.helpCenterView)
        helpCenterView.setOnClickListener {
            Intercom.client().displayHelpCenter()
        }




        return view
    }


}
