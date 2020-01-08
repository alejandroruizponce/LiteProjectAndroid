package com.chekinlite.app.OldVersionFiles

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.reginald.editspinner.EditSpinner
import org.json.JSONObject
import android.content.Intent
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.view.Window
import android.widget.*
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R
import com.yinglan.keyboard.HideUtil
import java.util.HashMap


class AddProperty : AppCompatActivity() {

    lateinit var titleBar: TextView

    lateinit var nameAdProView: RelativeLayout
    lateinit var NIFAdProView: RelativeLayout
    lateinit var countryAdProView: RelativeLayout
    lateinit var provinceAdProView: RelativeLayout
    lateinit var cityAdProView: RelativeLayout
    lateinit var addressAdProView: RelativeLayout
    lateinit var typePoliceAdProView: RelativeLayout
    lateinit var userPoliceAdProView: RelativeLayout
    lateinit var passwordPoliceAdProView: RelativeLayout
    lateinit var thirdFieldPoliceAdProView: RelativeLayout
    lateinit var portalIstatView: RelativeLayout
    lateinit var userIstatView: RelativeLayout
    lateinit var passIstatView: RelativeLayout
    lateinit var nRoomsIstatView: RelativeLayout


    lateinit var automaticPoliceView: RelativeLayout
    lateinit var automaticIstatView: RelativeLayout
    lateinit var policeFormAdProView: ConstraintLayout
    lateinit var istatFormAdProView: ConstraintLayout



    lateinit var policeSwitcher: Switch
    lateinit var istatSwitcher: Switch
    lateinit var hospederyPoliceSwitcher: CheckBox
    lateinit var hospederyIstatSwitcher: CheckBox

    lateinit var morethan1Police: Switch
    lateinit var morethan1Istat: Switch
    lateinit var morethan1PoliceView: RelativeLayout
    lateinit var morethan1IstatView: RelativeLayout
    lateinit var hospederyPoliceView: RelativeLayout
    lateinit var hospederyPoliceEditText: EditSpinner
    lateinit var hospederyIstatView: RelativeLayout
    lateinit var hospederyIstatEditText: EditSpinner

    lateinit var nameAdProEditText: EditText
    lateinit var NIFAdProEditText: EditText
    lateinit var countryAdProEditText: EditSpinner
    lateinit var provinceAdProEditText: EditSpinner
    lateinit var cityAdProEditText: EditText
    lateinit var addressAdProEditText: EditText
    lateinit var typePoliceAdProEditText: EditSpinner
    lateinit var userPoliceAdProEditText: EditText
    lateinit var passwordPoliceAdProEditText: EditText
    lateinit var thirdFieldPoliceAdProEditText: EditText
    lateinit var portalIstatEditText: EditSpinner
    lateinit var userIstatEditText: EditText
    lateinit var passIstatEditText: EditText
    lateinit var nroomsIstatEditText: EditText

    lateinit var thirdFieldTitle: TextView

    lateinit var buttonAddProperty: Button
    lateinit var buttonBack: Button

    lateinit var MyDialog: Dialog

    lateinit var propertyInfo: JSONObject
    var guestID = ""
    var countryIndex = 0
    var policeIndex = 0
    var istatIndex = 0
    var provinceIndex = 0
    var hospederyPoliceIndex = 0
    var hospederyIstatIndex = 0
    var policeEdited = false
    var policeType = ""
    var otherRegionIstat = false
    var portalIstatID = arrayOf("MTWEB","TURISTAT3","RICESTAT","ITER", "ITLO", "RADAR","ASTAT","EPT","STU","RIMOVCLI","SPOT","SARDEGNA","SICILIA","MARCHE","CALABRIA","PIEMONTE","ITAB","WEBTUR","UMBRIA","SIST","EPT","OTHER")
    var portalIstatNames = arrayOf("Veneto - MTWeb", "Toscana - Turistat3", "Toscana - Ricestat", "E. Romagna - Turismo5", "Lombardia - Turismo5", "Lazio - RADAR", "Bolzano - ASTAT Tic-WEB", "Campania - EPT", "Trento - STU", "Liguria - RIMOVCLI", "Puglia - SPOT", "Sardegna - SiRED", "Sicilia - OCR", "Marche - STM", "Calabria - SIRDAD", "Piemonte - TUAP", "Abruzzo - Turismo5", "Friuli Venezia Giula - WebTur", "Umbria - TOLM", "Basilicata - SIST", "Molise - EPT Molise", "Other region")
    var namePoliceHospedery: ArrayList<String> = ArrayList()
    var codePoliceHospedery: ArrayList<String> = ArrayList()
    var listCodeHostelry: ArrayList<JSONObject> = ArrayList()
    var nameIstatHospedery: ArrayList<String> = ArrayList()
    var codeIstatHospedery: ArrayList<String> = ArrayList()

    lateinit var loadingDialog: Dialog
    lateinit var progressBarLoading: ProgressBar


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)
        HideUtil.init(this);

        var EDIT_PROPERTY = intent.getBooleanExtra("EDIT_PROPERTY", false)
        if (EDIT_PROPERTY) {
            intent.removeExtra("EDIT_PROPERTY")
            propertyInfo = JSONObject(intent.getStringExtra("propertyInfo"))
            if(!intent.getStringExtra("idGuest").isNullOrBlank()) {
                guestID = intent.getStringExtra("idGuest")
            }

        }



        buttonBack = findViewById(R.id.buttonBackAP)
        buttonBack.setOnClickListener {
            finish()
        }

        nameAdProView = findViewById(R.id.nameAdProView) as RelativeLayout
        nameAdProEditText = findViewById(R.id.nameAdProEditText) as EditText
        nameAdProEditText.addTextChangedListener(CustomTextWatcher(nameAdProEditText))
        nameAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        NIFAdProView = findViewById(R.id.NIFAdPro) as RelativeLayout
        NIFAdProEditText = findViewById(R.id.NIFAdProEditText) as EditText
        NIFAdProEditText.addTextChangedListener(CustomTextWatcher(NIFAdProEditText))
        NIFAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        provinceAdProView = findViewById(R.id.provinceAdPro) as RelativeLayout
        provinceAdProEditText = findViewById(R.id.provinceAdProEditText) as EditSpinner
        provinceAdProEditText.isEnabled = false
        provinceAdProEditText.addTextChangedListener(CustomTextWatcher(provinceAdProEditText))
        provinceAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        countryAdProView = findViewById(R.id.countryAdPro) as RelativeLayout
        countryAdProEditText = findViewById(R.id.countryAdProEditText) as EditSpinner
        val adapter = ArrayAdapter(
            this, R.layout.item_dropdown,
            resources.getStringArray(R.array.countryList)

        )
        countryAdProEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        countryAdProEditText.setAdapter(adapter)

        // triggered when one item in the list is clicked
        countryAdProEditText.setOnItemClickListener { parent, view, position, id ->
            countryIndex = position
            countryAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            countryAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            countryAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            provinceAdProEditText.isEnabled = true

            setProvincesByCountry()

        }




        cityAdProView = findViewById(R.id.cityAdPro) as RelativeLayout
        cityAdProEditText = findViewById(R.id.cityAdProEditText) as EditText
        cityAdProEditText.addTextChangedListener(CustomTextWatcher(cityAdProEditText))
        cityAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        addressAdProView = findViewById(R.id.addressAdPro) as RelativeLayout
        addressAdProEditText = findViewById(R.id.addressAdProEditText) as EditText
        addressAdProEditText.addTextChangedListener(CustomTextWatcher(addressAdProEditText))
        addressAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


        typePoliceAdProEditText = findViewById(R.id.policeTypeAdProEditText) as EditSpinner


        userPoliceAdProView = findViewById(R.id.userPoliceAdPro) as RelativeLayout
        userPoliceAdProEditText = findViewById(R.id.userPoliceAdProEditText) as EditText
        userPoliceAdProEditText.addTextChangedListener(CustomTextWatcher(userPoliceAdProEditText))
        userPoliceAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        passwordPoliceAdProView = findViewById(R.id.passPoliceAdPro) as RelativeLayout
        passwordPoliceAdProEditText = findViewById(R.id.passPoliceAdProEditText) as EditText
        passwordPoliceAdProEditText.addTextChangedListener(CustomTextWatcher(passwordPoliceAdProEditText))
        passwordPoliceAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        thirdFieldPoliceAdProView = findViewById(R.id.thirdFieldPoliceAdPro) as RelativeLayout
        thirdFieldPoliceAdProEditText = findViewById(R.id.thirdFieldPoliceAdProEditText) as EditText
        thirdFieldPoliceAdProEditText.addTextChangedListener(CustomTextWatcher(thirdFieldPoliceAdProEditText))
        thirdFieldPoliceAdProEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })
        thirdFieldTitle = findViewById(R.id.thirdFieldTitleText)

        userIstatView = findViewById(R.id.userIstatAdPro) as RelativeLayout
        userIstatEditText = findViewById(R.id.userIstatProEditText) as EditText
        userIstatEditText.addTextChangedListener(CustomTextWatcher(userIstatEditText))
        userIstatEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        passIstatView = findViewById(R.id.passIstatAdPro) as RelativeLayout
        passIstatEditText = findViewById(R.id.passIstatProEditText) as EditText
        passIstatEditText.addTextChangedListener(CustomTextWatcher(passIstatEditText))
        passIstatEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        nRoomsIstatView = findViewById(R.id.nRoomsAdPro) as RelativeLayout
        nroomsIstatEditText = findViewById(R.id.nRoomsIstatProEditText) as EditText
        nroomsIstatEditText.addTextChangedListener(CustomTextWatcher(nroomsIstatEditText))
        nroomsIstatEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })



        morethan1PoliceView = findViewById(R.id.moreThan1AdProView)
        morethan1PoliceView.visibility = View.GONE

        policeSwitcher = findViewById(R.id.switchPolice)

        policeSwitcher.isEnabled = false
        policeSwitcher.setOnClickListener {
            if (policeSwitcher.isChecked) {
                policeFormAdProView.visibility = View.VISIBLE
            } else {
                policeFormAdProView.visibility = View.GONE
            }
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }



        istatSwitcher = findViewById(R.id.switchIstat)
        istatSwitcher.setOnClickListener {
            if (istatSwitcher.isChecked) {
                istatFormAdProView.visibility = View.VISIBLE
            } else {
                istatFormAdProView.visibility = View.GONE
            }
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }


        hospederyPoliceEditText = findViewById(R.id.hospederyPoliceAdProEditText)
        hospederyPoliceEditText.isEnabled = false
        hospederyPoliceView = findViewById(R.id.hospederyPoliceAdPro)
        hospederyPoliceView.visibility = View.GONE
        hospederyPoliceSwitcher = findViewById(R.id.checkerHospederyPolice)
        hospederyPoliceSwitcher.alpha = .5f
        hospederyPoliceSwitcher.isEnabled = false
        hospederyPoliceSwitcher.setOnClickListener {
            if (hospederyPoliceSwitcher.isChecked) {
                if(countryIndex == 0) {
                    ChekinAPI.getHostelryCodesSpain(
                        this,
                        userPoliceAdProEditText.text.toString(),
                        passwordPoliceAdProEditText.text.toString()
                    ) { result, status ->
                        if (status) {

                        }
                    }
                } else if (countryIndex == 1) {
                    ChekinAPI.getHostelryCodesPoliceItaly(
                        this,
                        userPoliceAdProEditText.text.toString(),
                        passwordPoliceAdProEditText.text.toString(),
                        thirdFieldPoliceAdProEditText.text.toString()
                    ) { result, status ->
                        if (result != null) {
                            for (num in 0..result.length() - 1) {
                                listCodeHostelry?.add(result[num] as JSONObject)
                            }

                            for (index in 0..listCodeHostelry.count()) {
                                listCodeHostelry.get(index).let { hospedery ->
                                    hospedery.getString("subhouse_name").let { name ->
                                        namePoliceHospedery.add(name)
                                    }

                                    hospedery.getString("subhouse_code").let { code ->
                                        codePoliceHospedery.add(code)
                                    }
                                }
                            }

                            hospederyPoliceEditText.isEnabled = true
                            hospederyPoliceEditText =
                                findViewById(R.id.hospederyPoliceAdProEditText) as EditSpinner
                            val adapter = ArrayAdapter(
                                this, R.layout.item_dropdown,
                                namePoliceHospedery

                            )
                            hospederyPoliceEditText.dropDownBackground.setColorFilter(
                                Color.WHITE,
                                PorterDuff.Mode.SRC_ATOP
                            )
                            hospederyPoliceEditText.setAdapter(adapter)

                            // triggered when one item in the list is clicked
                            hospederyPoliceEditText.setOnItemClickListener { parent, view, position, id ->
                                hospederyPoliceIndex = position
                                hospederyPoliceView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                hospederyPoliceView.setBackgroundColor(Color.parseColor("#ffffff"))
                                hospederyPoliceEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                            }


                        }
                    }
                }
                hospederyPoliceView.visibility = View.VISIBLE
            } else {
                hospederyPoliceView.visibility = View.GONE
            }
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }

        morethan1IstatView = findViewById(R.id.moreThan1IstatAdProView)
        hospederyIstatEditText = findViewById(R.id.hospederyIstatAdProEditText)
        hospederyIstatView = findViewById(R.id.hospederyIstatAdPro)
        hospederyIstatView.visibility = View.GONE
        hospederyIstatSwitcher = findViewById(R.id.checkIstatHospedery)
        hospederyIstatSwitcher.alpha = .5f
        hospederyIstatSwitcher.isEnabled = false
        hospederyIstatSwitcher.setOnClickListener {
            if (hospederyIstatSwitcher.isChecked) {
                if (countryIndex == 1) {
                    ChekinAPI.getHostelryCodesIstatItaly(
                        this,
                        userIstatEditText.text.toString(),
                        passIstatEditText.text.toString(),
                        portalIstatID[istatIndex]
                    ) { result, status ->
                        if (status) {

                        }
                    }
                }
                hospederyIstatView.visibility = View.VISIBLE
            } else {
                hospederyIstatView.visibility = View.GONE
            }
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }

        portalIstatView = findViewById(R.id.istatPortaleAdPro) as RelativeLayout
        portalIstatEditText = findViewById(R.id.istatPortaldProEditText) as EditSpinner
        val adapterPolice = ArrayAdapter(
            this, R.layout.item_dropdown,
            portalIstatNames

        )
        portalIstatEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        portalIstatEditText.setAdapter(adapterPolice)

        // triggered when one item in the list is clicked
        portalIstatEditText.setOnItemClickListener { parent, view, position, id ->
            istatIndex = position
            if (portalIstatNames[istatIndex] == portalIstatNames.last()) {
                otherRegionIstat = true
            } else {
                otherRegionIstat = false
            }
            portalIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            portalIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
            portalIstatEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }

        }


        policeFormAdProView = findViewById(R.id.policeFormView) as ConstraintLayout
        policeFormAdProView.visibility = View.GONE
        istatFormAdProView = findViewById(R.id.istatFormView) as ConstraintLayout
        istatFormAdProView.visibility = View.GONE
        automaticPoliceView = findViewById(R.id.automaticPoliceView) as RelativeLayout
        automaticIstatView = findViewById(R.id.automaticIstatView) as RelativeLayout
        automaticIstatView.visibility = View.GONE


        buttonAddProperty = findViewById(R.id.buttonAddPro)
        buttonAddProperty.background.alpha = 128
        buttonAddProperty.isClickable = false
        buttonAddProperty.setOnClickListener {
            val params = HashMap<String, Any>()

            if(countryIndex == 0) {
                if(policeSwitcher.isChecked) {
                    policeEdited = true
                    userPoliceAdProEditText.text.let {
                        user ->
                        passwordPoliceAdProEditText.text.let {
                            pass ->
                            if(policeIndex == 0 && hospederyPoliceSwitcher.isChecked) {
                                params["tradename"] = nameAdProEditText.text.toString()
                                params["legal_holder_name"] = "legal_holder_name"
                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                params["address"] = addressAdProEditText.text.toString()
                                params["country"] = "ESP"
                                params["municipality"] = cityAdProEditText.text.toString()
                                params["province"] = provinceIndex
                                params["police_type"] = policeType
                                params["police_user"] = user.toString()
                                params["police_password"] = pass.toString()
                                params["is_housing_group"] = true
                                params["is_conected_to_istat"] = false
                                params["police_hostelry_code"] = codePoliceHospedery[hospederyPoliceIndex]


                            } else {
                                params["tradename"] = nameAdProEditText.text.toString()
                                params["legal_holder_name"] = "legal_holder_name"
                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                params["address"] = addressAdProEditText.text.toString()
                                params["country"] = "ESP"
                                params["municipality"] = cityAdProEditText.text.toString()
                                params["province"] = provinceIndex
                                params["police_type"] = policeType
                                params["police_user"] = user.toString()
                                params["police_password"] = pass.toString()
                                params["is_housing_group"] = false
                                params["is_conected_to_istat"] = false
                            }
                        }
                    }
                } else {
                    params["tradename"] = nameAdProEditText.text.toString()
                    params["legal_holder_name"] = "legal_holder_name"
                    params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                    params["address"] = addressAdProEditText.text.toString()
                    params["country"] = "ESP"
                    params["municipality"] = cityAdProEditText.text.toString()
                    params["province"] = provinceIndex
                    params["police_type"] = "NOC"
                    params["is_housing_group"] = false
                    params["is_conected_to_istat"] = false
                }
            } else if (countryIndex == 1) {
                if(policeSwitcher.isChecked) {
                    policeEdited = true
                    userPoliceAdProEditText.text.let {
                            user ->
                        passwordPoliceAdProEditText.text.let {
                                pass ->
                                thirdFieldPoliceAdProEditText.text.let {
                                    certificate ->
                                    if (hospederyPoliceSwitcher.isChecked) {
                                        params["tradename"] = nameAdProEditText.text.toString()
                                        params["legal_holder_name"] = "legal_holder_name"
                                        params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                        params["address"] = addressAdProEditText.text.toString()
                                        params["country"] = "ITA"
                                        params["municipality"] = cityAdProEditText.text.toString()
                                        params["province"] = provinceIndex
                                        params["police_type"] = "ISP"
                                        params["police_user"] = user.toString()
                                        params["police_password"] = pass.toString()
                                        params["police_cert_password"] = certificate.toString()
                                        params["is_housing_group"] = true
                                        params["is_conected_to_istat"] = false
                                        params["police_hostelry_code"] = codePoliceHospedery[hospederyPoliceIndex]

                                    } else {
                                        println("SE USA LA POLICIA ITALIANA EN LA PROPIEDAD")
                                        params["tradename"] = nameAdProEditText.text.toString()
                                        params["legal_holder_name"] = "legal_holder_name"
                                        params["rta_number"] = "rta_number"
                                        params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                        params["address"] = addressAdProEditText.text.toString()
                                        params["country"] = "ITA"
                                        params["municipality"] = cityAdProEditText.text.toString()
                                        params["province"] = provinceIndex
                                        params["police_type"] = "ISP"
                                        params["police_user"] = user.toString()
                                        params["police_password"] = pass.toString()
                                        params["police_cert_password"] = certificate.toString()
                                        params["is_housing_group"] = false
                                        params["is_conected_to_istat"] = false
                                    }

                                    if (istatSwitcher.isChecked) {
                                        portalIstatEditText.text.let {
                                            portal ->
                                            userIstatEditText.text.let {
                                                userIs ->
                                                passIstatEditText.text.let {
                                                    passIs ->
                                                    nroomsIstatEditText.text.let {
                                                        nrooms ->
                                                        if (!otherRegionIstat) {
                                                            params["tradename"] = nameAdProEditText.text.toString()
                                                            params["legal_holder_name"] = "legal_holder_name"
                                                            params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                            params["address"] = addressAdProEditText.text.toString()
                                                            params["country"] = "ITA"
                                                            params["municipality"] = cityAdProEditText.text.toString()
                                                            params["province"] = provinceIndex
                                                            params["police_type"] = "ISP"
                                                            params["police_user"] = user.toString()
                                                            params["police_password"] = pass.toString()
                                                            params["police_cert_password"] = certificate.toString()
                                                            params["is_conected_to_istat"] = true
                                                            params["police_hostelry_code"] = ""
                                                            params["istat_web"] = portalIstatID[istatIndex]
                                                            params["istat_user"] = userIs.toString()
                                                            params["istat_password"] = passIs.toString()
                                                            params["rooms_qty"] = nrooms.toString().toInt()

                                                            if(hospederyIstatSwitcher.isChecked) {
                                                                params["tradename"] = nameAdProEditText.text.toString()
                                                                params["legal_holder_name"] = "legal_holder_name"
                                                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                                params["address"] = addressAdProEditText.text.toString()
                                                                params["country"] = "ITA"
                                                                params["municipality"] = cityAdProEditText.text.toString()
                                                                params["province"] = provinceIndex
                                                                params["police_type"] = "ISP"
                                                                params["police_user"] = user.toString()
                                                                params["police_password"] = pass.toString()
                                                                params["police_cert_password"] = certificate.toString()
                                                                params["is_conected_to_istat"] = true
                                                                params["police_hostelry_code"] = ""
                                                                params["istat_web"] = portalIstatID[istatIndex]
                                                                params["istat_user"] = userIs.toString()
                                                                params["istat_password"] = passIs.toString()
                                                                params["rooms_qty"] = nrooms.toString().toInt()
                                                            }
                                                        } else {
                                                            params["tradename"] = nameAdProEditText.text.toString()
                                                            params["legal_holder_name"] = "legal_holder_name"
                                                            params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                            params["address"] = addressAdProEditText.text.toString()
                                                            params["country"] = "ITA"
                                                            params["municipality"] = cityAdProEditText.text.toString()
                                                            params["province"] = provinceIndex
                                                            params["police_type"] = "ISP"
                                                            params["police_user"] = user.toString()
                                                            params["police_password"] = pass.toString()
                                                            params["police_cert_password"] = certificate.toString()
                                                            params["is_conected_to_istat"] = true
                                                            params["police_hostelry_code"] = ""
                                                            params["istat_web"] = portalIstatID[istatIndex]
                                                            params["istat_user"] = userIs
                                                            params["istat_other_web"] = portal.toString()
                                                            params["istat_password"] = passIs.toString()
                                                            params["rooms_qty"] = nrooms.toString().toInt()

                                                            if(hospederyIstatSwitcher.isChecked) {
                                                                params["tradename"] = nameAdProEditText.text.toString()
                                                                params["legal_holder_name"] = "legal_holder_name"
                                                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                                params["address"] = addressAdProEditText.text.toString()
                                                                params["country"] = "ITA"
                                                                params["municipality"] = cityAdProEditText.text.toString()
                                                                params["province"] = provinceIndex
                                                                params["police_type"] = "ISP"
                                                                params["police_user"] = user.toString()
                                                                params["police_password"] = pass.toString()
                                                                params["police_cert_password"] = certificate.toString()
                                                                params["is_conected_to_istat"] = true
                                                                params["istat_structure_code"] = codeIstatHospedery[hospederyIstatIndex]
                                                                params["police_hostelry_code"] = ""
                                                                params["istat_web"] = portalIstatID[istatIndex]
                                                                params["istat_user"] = userIs.toString()
                                                                params["istat_other_web"] = portal.toString()
                                                                params["istat_password"] = passIs.toString()
                                                                params["rooms_qty"] = nrooms.toString().toInt()
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                } else  {
                    if (istatSwitcher.isChecked) {
                        policeEdited = true
                        portalIstatEditText.text.let {
                                portal ->
                            userIstatEditText.text.let {
                                    userIs ->
                                passIstatEditText.text.let {
                                        passIs ->
                                    nroomsIstatEditText.text.let {
                                            nrooms ->
                                        if (!otherRegionIstat) {
                                            params["tradename"] = nameAdProEditText.text.toString()
                                            params["legal_holder_name"] = "legal_holder_name"
                                            params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                            params["address"] = addressAdProEditText.text.toString()
                                            params["country"] = "ITA"
                                            params["municipality"] = cityAdProEditText.text.toString()
                                            params["province"] = provinceIndex
                                            params["police_type"] = "NOC"
                                            params["is_conected_to_istat"] = true
                                            params["police_hostelry_code"] = ""
                                            params["istat_web"] = portalIstatID[istatIndex]
                                            params["istat_user"] = userIs.toString()
                                            params["istat_password"] = passIs.toString()
                                            params["rooms_qty"] = nrooms.toString().toInt()

                                            if(hospederyIstatSwitcher.isChecked) {
                                                params["tradename"] = nameAdProEditText.text.toString()
                                                params["legal_holder_name"] = "legal_holder_name"
                                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                params["address"] = addressAdProEditText.text.toString()
                                                params["country"] = "ITA"
                                                params["municipality"] = cityAdProEditText.text.toString()
                                                params["province"] = provinceIndex
                                                params["police_type"] = "NOC"
                                                params["is_conected_to_istat"] = true
                                                params["police_hostelry_code"] = ""
                                                params["istat_web"] = portalIstatID[istatIndex]
                                                params["istat_user"] = userIs.toString()
                                                params["istat_password"] = passIs.toString()
                                                params["rooms_qty"] = nrooms.toString().toInt()
                                            }
                                        } else {
                                            params["tradename"] = nameAdProEditText.text.toString()
                                            params["legal_holder_name"] = "legal_holder_name"
                                            params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                            params["address"] = addressAdProEditText.text.toString()
                                            params["country"] = "ITA"
                                            params["municipality"] = cityAdProEditText.text.toString()
                                            params["province"] = provinceIndex
                                            params["police_type"] = "NOC"
                                            params["is_conected_to_istat"] = true
                                            params["police_hostelry_code"] = ""
                                            params["istat_web"] = portalIstatID[istatIndex]
                                            params["istat_user"] = userIs.toString()
                                            params["istat_other_web"] = portal.toString()
                                            params["istat_password"] = passIs.toString()
                                            params["rooms_qty"] = nrooms.toString().toInt()

                                            if(hospederyIstatSwitcher.isChecked) {
                                                params["tradename"] = nameAdProEditText.text.toString()
                                                params["legal_holder_name"] = "legal_holder_name"
                                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                                params["address"] = addressAdProEditText.text.toString()
                                                params["country"] = "ITA"
                                                params["municipality"] = cityAdProEditText.text.toString()
                                                params["province"] = provinceIndex
                                                params["police_type"] = "NOC"
                                                params["is_conected_to_istat"] = true
                                                params["istat_structure_code"] = codeIstatHospedery[hospederyIstatIndex]
                                                params["police_hostelry_code"] = ""
                                                params["istat_web"] = portalIstatID[istatIndex]
                                                params["istat_user"] = userIs.toString()
                                                params["istat_other_web"] = portal.toString()
                                                params["istat_password"] = passIs.toString()
                                                params["rooms_qty"] = nrooms.toString().toInt()
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        params["tradename"] = nameAdProEditText.text.toString()
                        params["legal_holder_name"] = "legal_holder_name"
                        params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                        params["address"] = addressAdProEditText.text.toString()
                        params["country"] = "ITA"
                        params["municipality"] = cityAdProEditText.text.toString()
                        params["province"] = provinceIndex
                        params["police_type"] = "NOC"
                        params["is_conected_to_istat"] = false
                    }
                }
            } else if (countryIndex == 2) {
                if (policeSwitcher.isChecked) {
                    policeEdited = true
                    userPoliceAdProEditText.text.let { user ->
                        passwordPoliceAdProEditText.text.let { pass ->
                            thirdFieldPoliceAdProEditText.text.let { est ->
                                params["tradename"] = nameAdProEditText.text.toString()
                                params["legal_holder_name"] = "legal_holder_name"
                                params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                                params["address"] = addressAdProEditText.text.toString()
                                params["country"] = "PRT"
                                params["municipality"] = cityAdProEditText.text.toString()
                                params["province"] = provinceIndex
                                params["police_type"] = "SEF"
                                params["police_user"] = user.toString()
                                params["police_password"] = pass.toString()
                                params["establishment_num"] = est.toString()
                                params["is_housing_group"] = false
                                params["is_conected_to_istat"] = false
                            }
                        }
                    }

                } else {
                    params["tradename"] = nameAdProEditText.text.toString()
                    params["legal_holder_name"] = "legal_holder_name"
                    params["legal_holder_nif"] = NIFAdProEditText.text.toString()
                    params["address"] = addressAdProEditText.text.toString()
                    params["country"] = "PRT"
                    params["municipality"] = cityAdProEditText.text.toString()
                    params["province"] = provinceIndex
                    params["police_type"] = "NOC"
                    params["is_housing_group"] = false
                    params["is_conected_to_istat"] = false
                }


            }

            if(EDIT_PROPERTY) {
                println("SE EDITA LA PROPIEDAD")
                propertyInfo.getString("id").let {
                    id ->
                    println("Se actualiza la propiedad con id: $id")
                    showDialogLoading()
                    ChekinAPI.updateAccommodation(
                        this,
                        id,
                        params
                    ) { result, status ->
                        if (status) {
                            UserProfile.needBookingReload = true
                            UserProfile.needPropertyReload = true
                            intent.removeExtra("EDIT_PROPERTY")
                            hideLoadingDialog()
                            var intent = Intent(this, NavigationAcitivity::class.java)
                            intent.putExtra("selectedTab", 1)
                            startActivity(intent)

                            if (policeEdited) {
                                var arrayGuests = ArrayList<Int>()
                                if (!guestID.isNullOrBlank()) {
                                    arrayGuests.add(guestID.toInt())
                                    ChekinAPI.retriggerGuests(
                                        this,
                                        arrayGuests
                                    ) { result, status ->
                                        if (status) {

                                        }
                                    }
                                }

                            }
                        } else {
                        }
                    }
                }
            } else {
                println("SE CREA LA PROPIEDAD")
                showDialogLoading()
                ChekinAPI.addAccommodation(
                    this,
                    params
                ) { result, status ->
                    if (status) {
                        UserProfile.needPropertyReload = true
                        UserProfile.needDocumentReload = true
                        hideLoadingDialog()
                        finish()

                        /*
                        var intent = Intent(this, NavigationAcitivity::class.java)
                        intent.putExtra("selectedTab", 1)
                        startActivity(intent)*/
                    } else {
                    }
                }
            }




        }

        if (EDIT_PROPERTY) {
            println("MODO EDITAR PROPIEDAD ACTIVO")
            println("La info de la propiedad es $propertyInfo ")

            titleBar = findViewById(R.id.titleBarAdPro)
            titleBar.setText(getString(R.string.editProperty))

            buttonAddProperty.setText(getString(R.string.updateProperty))

            nameAdProEditText.setText(propertyInfo.getString("tradename"))
            nameAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            nameAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            nameAdProView.setBackgroundColor(Color.parseColor("#ffffff"))

            NIFAdProEditText.setText(propertyInfo.getString("legal_holder_nif"))
            NIFAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            NIFAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            NIFAdProView.setBackgroundColor(Color.parseColor("#ffffff"))

            countryAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            countryAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            countryAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            propertyInfo.getString("country").let {
                country ->
                if(country == "ESP") {
                    countryIndex = 0
                    countryAdProEditText.setText("España")
                } else if (country == "ITA") {
                    countryIndex = 1
                    countryAdProEditText.setText("Italia")

                    automaticIstatView.visibility = View.VISIBLE
                    thirdFieldPoliceAdProView.visibility = View.VISIBLE
                    morethan1PoliceView.visibility = View.VISIBLE
                } else if (country == "PRT") {
                    countryIndex = 2
                    countryAdProEditText.setText("Portugal")
                }
                policeSwitcher.isChecked = false
                istatSwitcher.isChecked = false
                policeFormAdProView.visibility = View.GONE
                policeSwitcher.isEnabled = true
                provinceAdProEditText.isEnabled = true
                setProvincesByCountry()
            }


            provinceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            provinceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            provinceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            propertyInfo.getInt("province").let {
                provin ->
                provinceIndex = provin
                if(countryIndex == 0) {
                    provinceAdProEditText.setText(UserProfile.provincesSpain[provinceIndex])
                } else if(countryIndex == 1) {
                    provinceAdProEditText.setText(UserProfile.provincesItaly[provinceIndex])
                } else if (countryIndex == 2) {
                    provinceAdProEditText.setText(UserProfile.provincesPortugal[provinceIndex])
                }
            }

            cityAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            cityAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            cityAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            propertyInfo.getString("municipality").let {
                    mun ->
                cityAdProEditText.setText(mun)
            }

            addressAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            addressAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            addressAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            propertyInfo.getString("address").let {
                    add ->
                addressAdProEditText.setText(add)
            }

            propertyInfo.getString("police_type").let {
                poltype ->
                if(poltype.isNotBlank()) {
                    policeType = poltype

                    userPoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                    userPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    userPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                    propertyInfo.getString("police_user").let { poluser ->
                        userPoliceAdProEditText.setText(poluser)
                    }

                    passwordPoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                    passwordPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    passwordPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                    propertyInfo.getString("police_password").let { poluser ->
                        passwordPoliceAdProEditText.setText(poluser)
                    }

                    typePoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                    typePoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    typePoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                    if(poltype == "POL") {
                        policeIndex = 0

                        typePoliceAdProEditText.setText("Policia Nacional")

                        propertyInfo.getBoolean("is_housing_group").let {
                            hGroup ->
                            if(hGroup) {
                                hospederyPoliceSwitcher.isChecked = true
                            }
                        }

                    } else if(poltype == "NAT") {
                        policeIndex = 1
                        typePoliceAdProEditText.setText("Guardia Civil")
                    } else if(poltype == "MOS") {
                        policeIndex = 2
                        typePoliceAdProEditText.setText("Mossos d'Esquadra")
                    } else if(poltype == "ERT") {
                        policeIndex = 3
                        typePoliceAdProEditText.setText("Ertzaintza")
                    } else if(poltype == "ISP") {
                        typePoliceAdProEditText.setText("Polizia di Stato Italiana")
                    } else if (poltype == "SEF") {
                        typePoliceAdProEditText.setText("Serviço de Estrangeiros e Fronteiras")
                    }

                    if(countryIndex == 1) {
                        thirdFieldPoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                        thirdFieldPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                        thirdFieldPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                        propertyInfo.getString("police_cert_password").let {
                            cert->
                            if(cert.isNotBlank()) {
                                thirdFieldTitle.setText("Certificado")
                                thirdFieldPoliceAdProEditText.setText(cert)
                            }
                        }
                    } else if (countryIndex == 2) {
                        thirdFieldPoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                        thirdFieldPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                        thirdFieldPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                        propertyInfo.getString("establishment_num").let {
                                est->
                            if(est.isNotBlank()) {
                                thirdFieldTitle.setText("Número de establecimiento")
                                thirdFieldPoliceAdProEditText.setText(est)
                            }
                        }
                    }
                }

                propertyInfo.getBoolean("is_conected_to_istat").let {
                    istatON ->
                    if (istatON) {
                        istatSwitcher.isChecked = true
                        istatFormAdProView.visibility = View.VISIBLE
                    }
                }

                portalIstatEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                portalIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                portalIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
                propertyInfo.getString("istat_web").let {
                    istatType ->
                    if(istatType.isNotBlank()) {
                        for(i in 0..portalIstatID.count()-1){
                            if (istatType == portalIstatID[i]) {
                                portalIstatEditText.setText(portalIstatNames[i])
                                istatIndex = i
                            }
                        }
                    }
                }

                userIstatEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                userIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                userIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
                propertyInfo.getString("istat_user").let {
                    userIs ->
                    userIstatEditText.setText(userIs)
                }

                passIstatEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                passIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                passIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
                propertyInfo.getString("istat_password").let {
                        passIs ->
                        passIstatEditText.setText(passIs)
                }

                nroomsIstatEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nRoomsIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nRoomsIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
                propertyInfo.getInt("rooms_qty").let {
                        nroomsIs ->
                        nroomsIstatEditText.setText(nroomsIs.toString())
                }

            }


            if(policeType != "NOC") {
                policeSwitcher.isChecked = true
                policeFormAdProView.visibility = View.VISIBLE
            }

            checkFields()
        }

    }

    fun setProvincesByCountry() {
        //ESPAÑA
        if (countryIndex == 0) {
            val adapter = ArrayAdapter(
                this, R.layout.item_dropdown,
                UserProfile.provincesSpain

            )
            provinceAdProEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            provinceAdProEditText.setAdapter(adapter)

            thirdFieldPoliceAdProView.visibility = View.GONE
            automaticIstatView.visibility = View.GONE
            istatFormAdProView.visibility = View.GONE

            typePoliceAdProView = findViewById(R.id.policeTypeAdPro) as RelativeLayout

            val adapterPolice = ArrayAdapter(
                this, R.layout.item_dropdown,
                resources.getStringArray(R.array.spanishPoliceType)

            )
            typePoliceAdProEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            typePoliceAdProEditText.setAdapter(adapterPolice)

            // triggered when one item in the list is clicked
            typePoliceAdProEditText.setOnItemClickListener { parent, view, position, id ->
                policeIndex = position
                typePoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                typePoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
                typePoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                if(checkFields()) {
                    buttonAddProperty.isClickable = true
                    buttonAddProperty.background.alpha = 255
                } else {
                    buttonAddProperty.isClickable = false
                    buttonAddProperty.background.alpha = 128
                }

                if (policeIndex == 0) {
                    policeType = "POL"
                    morethan1PoliceView.visibility = View.VISIBLE
                } else {
                    if (policeIndex == 1) {
                        policeType = "NAT"
                    } else if (policeIndex == 2) {
                        policeType = "MOS"
                    } else if (policeIndex == 3) {
                        policeType = "ERT"
                    }
                    morethan1PoliceView.visibility = View.GONE
                }
            }



            //ITALIA
        } else if (countryIndex == 1) {
            val adapter = ArrayAdapter(
                this, R.layout.item_dropdown,
                UserProfile.provincesItaly

            )
            provinceAdProEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            provinceAdProEditText.setAdapter(adapter)
            automaticIstatView.visibility = View.VISIBLE
            thirdFieldPoliceAdProView.visibility = View.VISIBLE

            typePoliceAdProView = findViewById(R.id.policeTypeAdPro) as RelativeLayout
            typePoliceAdProEditText = findViewById(R.id.policeTypeAdProEditText) as EditSpinner
            typePoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            typePoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            typePoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            typePoliceAdProEditText.setText("Polizia di Stato Italiana")

            thirdFieldTitle.setText(getString(R.string.certificate))

            morethan1PoliceView.visibility = View.VISIBLE

            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }


            //PORTUGAL
        } else if (countryIndex == 2) {
            val adapter = ArrayAdapter(
                this, R.layout.item_dropdown,
                UserProfile.provincesPortugal

            )
            provinceAdProEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            provinceAdProEditText.setAdapter(adapter)
            automaticIstatView.visibility = View.GONE
            thirdFieldPoliceAdProView.visibility = View.VISIBLE
            istatFormAdProView.visibility = View.GONE

            typePoliceAdProView = findViewById(R.id.policeTypeAdPro) as RelativeLayout
            typePoliceAdProEditText = findViewById(R.id.policeTypeAdProEditText) as EditSpinner
            typePoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            typePoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            typePoliceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            typePoliceAdProEditText.setText("Serviço de Estrangeiros e Fronteiras")

            thirdFieldTitle.setText(getString(R.string.establishmentNumber))

            morethan1PoliceView.visibility = View.GONE

            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }

        provinceAdProEditText.setOnItemClickListener { parent, view, position, id ->
            provinceIndex = position
            provinceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            provinceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            provinceAdProEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

        }
        policeSwitcher.isChecked = false
        istatSwitcher.isChecked = false
        policeFormAdProView.visibility = View.GONE
        policeSwitcher.isEnabled = true
        if(checkFields()) {
            buttonAddProperty.isClickable = true
            buttonAddProperty.background.alpha = 255
        } else {
            buttonAddProperty.isClickable = false
            buttonAddProperty.background.alpha = 128
        }
    }


    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == nameAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nameAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nameAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == NIFAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                NIFAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                NIFAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == countryAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                countryAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                countryAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == provinceAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                provinceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                provinceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == cityAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                cityAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                cityAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == addressAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                addressAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                addressAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == typePoliceAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                typePoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                typePoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == userPoliceAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                userPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                userPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == passwordPoliceAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                passwordPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                passwordPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == thirdFieldPoliceAdProEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                thirdFieldPoliceAdProView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                thirdFieldPoliceAdProView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == userIstatEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                userIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                userIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == passIstatEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                passIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                passIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == nroomsIstatEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nRoomsIstatView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nRoomsIstatView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(checkFields()) {
                buttonAddProperty.isClickable = true
                buttonAddProperty.background.alpha = 255
            } else {
                buttonAddProperty.isClickable = false
                buttonAddProperty.background.alpha = 128
            }
        }

        override fun afterTextChanged(s: Editable) {

        }


    }

    fun showDialogLoading() {

        MyDialog = Dialog(this)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.loading_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        MyDialog.setCancelable(false)
        MyDialog.setCanceledOnTouchOutside(false)



        MyDialog.show()

    }

    fun hideLoadingDialog() {
        MyDialog.dismiss()
    }




    fun checkFields(): Boolean {

        if(countryIndex == 0) {
            if (typePoliceAdProEditText.text.isNotBlank() && userPoliceAdProEditText.text.isNotBlank() && passwordPoliceAdProEditText.text.isNotBlank()) {
                hospederyPoliceSwitcher.isEnabled = true
                hospederyPoliceSwitcher.alpha = 1f
            }
        } else if (countryIndex == 1) {
            if(typePoliceAdProEditText.text.isNotBlank() && userPoliceAdProEditText.text.isNotBlank() && passwordPoliceAdProEditText.text.isNotBlank() && thirdFieldPoliceAdProEditText.text.isNotBlank()) {
                hospederyPoliceSwitcher.isEnabled = true
                hospederyPoliceSwitcher.alpha = 1f
            }

            if(portalIstatEditText.text.isNotBlank() && userIstatEditText.text.isNotBlank() && passIstatEditText.text.isNotBlank() && nroomsIstatEditText.text.isNotBlank()) {
                hospederyIstatSwitcher.isEnabled = true
                hospederyIstatSwitcher.alpha = 1f
            }
        }



        if (nameAdProEditText.text.isNotBlank() && NIFAdProEditText.text.isNotBlank() && countryAdProEditText.text.isNotBlank() &&
                provinceAdProEditText.text.isNotBlank() && cityAdProEditText.text.isNotBlank()  && addressAdProEditText.text.isNotBlank()) {
            if(countryIndex == 0) {
                if (policeSwitcher.isChecked) {
                        if(typePoliceAdProEditText.text.isNotBlank() && userPoliceAdProEditText.text.isNotBlank() && passwordPoliceAdProEditText.text.isNotBlank()) {
                            hospederyPoliceSwitcher.isEnabled = true
                            hospederyPoliceSwitcher.alpha = 1f
                            if (hospederyPoliceSwitcher.isChecked) {
                                if(!hospederyPoliceEditText.text.isNotBlank()) {
                                    return false
                                }
                            }
                        } else {
                            return false
                        }
                }
            } else if (countryIndex == 1) {
                if (policeSwitcher.isChecked) {
                    if(typePoliceAdProEditText.text.isNotBlank() && userPoliceAdProEditText.text.isNotBlank() && passwordPoliceAdProEditText.text.isNotBlank() && thirdFieldPoliceAdProEditText.text.isNotBlank()) {
                        hospederyPoliceSwitcher.isEnabled = true
                        hospederyPoliceSwitcher.alpha = 1f
                        if (hospederyPoliceSwitcher.isChecked) {
                            if (!hospederyPoliceEditText.text.isNotBlank()) {
                                return false
                            }
                        }
                    } else {
                        return false
                    }
                }

                if (istatSwitcher.isChecked) {
                    if(portalIstatEditText.text.isNotBlank() && userIstatEditText.text.isNotBlank() && passIstatEditText.text.isNotBlank() && nroomsIstatEditText.text.isNotBlank()) {
                        if (hospederyIstatSwitcher.isChecked) {
                            hospederyIstatSwitcher.isClickable = true
                            hospederyIstatSwitcher.background.alpha = 255
                            if(!hospederyIstatEditText.text.isNotBlank()) {
                                return false
                            }
                        }
                    } else {
                        return false
                    }
                }
            } else if (countryIndex == 2) {
                if (policeSwitcher.isChecked) {
                    if(typePoliceAdProEditText.text.isNotBlank() && userPoliceAdProEditText.text.isNotBlank() && passwordPoliceAdProEditText.text.isNotBlank() && thirdFieldPoliceAdProEditText.text.isNotBlank()) {

                    } else {
                        return false
                    }
                }
            }

        } else {
            return false
        }


        return true
    }


    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }
}


