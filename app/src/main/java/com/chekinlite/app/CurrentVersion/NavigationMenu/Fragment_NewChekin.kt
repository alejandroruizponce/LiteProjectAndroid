package com.chekinlite.app.CurrentVersion.NavigationMenu


import android.animation.LayoutTransition
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reginald.editspinner.EditSpinner
import org.json.JSONObject
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.chekinlite.app.CurrentVersion.Activities.AddGuest
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.OldVersionFiles.CollaboratorsActivity
import com.chekinlite.app.R
import com.yinglan.keyboard.HideUtil
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat
import java.text.ParseException


class Fragment_NewChekin : Fragment() {

    lateinit var namePropertyView: RelativeLayout
    lateinit var responsibleView: RelativeLayout
    lateinit var checkInView: RelativeLayout
    lateinit var nightsView: RelativeLayout
    lateinit var guestsOlder: RelativeLayout
    lateinit var numberRoomView: RelativeLayout
    lateinit var typeBookingView: RelativeLayout
    lateinit var progressBarLoading: ProgressBar

    lateinit var namePropertyEditText: EditSpinner
    lateinit var responsibleEditText: EditText
    lateinit var checkInDate: EditText
    lateinit var nightsEditText: EditText
    lateinit var guestsOlderEditText: EditText
    lateinit var numberRoomEditText: EditSpinner
    lateinit var typeBookingEditText: EditSpinner

    lateinit var buttonAddChekinLater: Button
    lateinit var buttonAddChekin: Button
    lateinit var titleNewChekin: TextView

    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonError: Button

    var propertyIndex = 0
    var nameProperties: ArrayList<String> = ArrayList()
    var listProperties: ArrayList<JSONObject> = ArrayList()
    val myCalendar = Calendar.getInstance()
    var roomsArray: ArrayList<String> = ArrayList()
    var otherResponsible = false
    var roomNumber = 0
    var idCollaborator = ""
    var addGuestsLater = false
    var guestsTotal = 0
    var IS_EDIT_MODE = false



    var typeBooking = ""

    var isItalianProperty = false
    var countryProperty = ""

    var isRoomsActivated = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_new_chekin, container, false)
        println("ENTRAMOS EN FRAGMENT NEW CHEKIN desde ONCREATE")
        listProperties =
            UserProfile.listProperties

        HideUtil.init(activity);

        var typeBookingNames = arrayOf(getString(R.string.individual), getString(
            R.string.grupo
        ), getString(R.string.familia))
        var typeBookingIDs = arrayOf("S", "G", "F")

        UserProfile.tabIsDisplayed = 2


        numberRoomEditText = view.findViewById(R.id.numberRoomNCEditText)
        numberRoomView = view.findViewById(R.id.numberRoomNC)
        numberRoomView.visibility = View.GONE


        activity?.let {
            ChekinNewAPI.getAccommodations(it) { result, status ->
                if (status) {
                    if (result is JSONArray && result != null) {
                        listProperties.clear()
                        for (num in 0..result.length() - 1) {
                            listProperties?.add(result[num] as JSONObject)
                        }
                        UserProfile.listProperties =
                            listProperties
                        val adapter = ArrayAdapter(
                            activity, R.layout.item_dropdown,
                            getPropertiesNames()
                        )
                        namePropertyEditText.dropDownBackground.setColorFilter(
                            Color.WHITE,
                            PorterDuff.Mode.SRC_ATOP
                        )
                        namePropertyEditText.setAdapter(adapter)

                        // triggered when one item in the list is clicked
                        namePropertyEditText.setOnItemClickListener { parent, view, position, id ->
                            println("Se pulsa una propiedad")
                            propertyIndex = position
                            listProperties[propertyIndex].getJSONObject("location")
                                .let { location ->
                                    location.getJSONObject("country").let { country ->
                                        country.getString("code").let { code ->
                                            countryProperty = code

                                        }
                                    }

                                }
                            namePropertyView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                            namePropertyView.setBackgroundColor(Color.parseColor("#ffffff"))
                            namePropertyEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                            if (countryProperty == "IT") {

                                typeBookingView.visibility = View.VISIBLE

                                println("La propiedad italiana seleccionada es: ${listProperties[propertyIndex]}")

                                listProperties[propertyIndex].getBoolean("is_stat_registration_enabled")
                                    .let { istatOn ->
                                        if (istatOn) {
                                            listProperties[propertyIndex].optJSONObject("stat_account")
                                                .let { stat_account ->
                                                    if (stat_account != null) {
                                                        stat_account.optString("type").let { type ->
                                                            if (type != "ITCA" && type != "ITVE") {
                                                                println("Se pone el numero de habitaciones visible")
                                                                isRoomsActivated = true
                                                                numberRoomView.visibility =
                                                                    View.VISIBLE



                                                                listProperties[propertyIndex].getInt(
                                                                    "rooms_quantity"
                                                                )
                                                                    .let { rooms ->
                                                                        roomsArray.clear()
                                                                        for (i in 0..rooms) {
                                                                            var number = i + 1
                                                                            roomsArray.add(number.toString())
                                                                        }
                                                                    }


                                                                val adapterAux = ArrayAdapter(
                                                                    activity,
                                                                    R.layout.item_dropdown,
                                                                    roomsArray
                                                                )
                                                                numberRoomEditText.dropDownBackground.setColorFilter(
                                                                    Color.WHITE,
                                                                    PorterDuff.Mode.SRC_ATOP
                                                                )
                                                                numberRoomEditText.setAdapter(
                                                                    adapterAux
                                                                )

                                                                // triggered when one item in the list is clicked
                                                                numberRoomEditText.setOnItemClickListener { parent, view, position, id ->
                                                                    roomNumber = position + 1
                                                                    numberRoomView.layoutTransition.enableTransitionType(
                                                                        LayoutTransition.CHANGING
                                                                    )
                                                                    numberRoomView.setBackgroundColor(
                                                                        Color.parseColor(
                                                                            "#ffffff"
                                                                        )
                                                                    )
                                                                    numberRoomEditText.setBackgroundResource(
                                                                        R.drawable.edittext_bottom_line
                                                                    )


                                                                    if (checkFields()) {
                                                                        buttonAddChekin.isClickable =
                                                                            true
                                                                        buttonAddChekin.background.alpha =
                                                                            255
                                                                        buttonAddChekinLater.isClickable =
                                                                            true
                                                                        buttonAddChekinLater.background.alpha =
                                                                            255
                                                                    } else {
                                                                        buttonAddChekin.isClickable =
                                                                            false
                                                                        buttonAddChekin.background.alpha =
                                                                            128
                                                                        buttonAddChekinLater.isClickable =
                                                                            false
                                                                        buttonAddChekinLater.background.alpha =
                                                                            128
                                                                    }

                                                                }
                                                            } else {
                                                                isRoomsActivated = false
                                                                numberRoomView.visibility =
                                                                    View.GONE
                                                            }
                                                        }
                                                    }
                                                }
                                        } else {
                                            numberRoomView.visibility = View.GONE
                                        }
                                    }
                            } else if (countryProperty == "AE") {
                                typeBookingView.visibility = View.VISIBLE
                                numberRoomView.visibility = View.GONE
                                isRoomsActivated = false
                            } else if (countryProperty == "DE") {
                                listProperties[propertyIndex].getBoolean("is_stat_registration_enabled")
                                    .let { istatOn ->
                                        if (istatOn) {
                                            isRoomsActivated = true
                                            println("Se pone el numero de habitaciones visible")
                                            numberRoomView.visibility = View.VISIBLE



                                            listProperties[propertyIndex].getInt("rooms_quantity")
                                                .let { rooms ->
                                                    roomsArray.clear()
                                                    for (i in 0..rooms) {
                                                        var number = i + 1
                                                        roomsArray.add(number.toString())
                                                    }
                                                }


                                            val adapterAux = ArrayAdapter(
                                                activity,
                                                R.layout.item_dropdown,
                                                roomsArray
                                            )
                                            numberRoomEditText.dropDownBackground.setColorFilter(
                                                Color.WHITE,
                                                PorterDuff.Mode.SRC_ATOP
                                            )
                                            numberRoomEditText.setAdapter(adapterAux)

                                            // triggered when one item in the list is clicked
                                            numberRoomEditText.setOnItemClickListener { parent, view, position, id ->
                                                roomNumber = position + 1
                                                numberRoomView.layoutTransition.enableTransitionType(
                                                    LayoutTransition.CHANGING
                                                )
                                                numberRoomView.setBackgroundColor(
                                                    Color.parseColor(
                                                        "#ffffff"
                                                    )
                                                )
                                                numberRoomEditText.setBackgroundResource(
                                                    R.drawable.edittext_bottom_line
                                                )

                                                if (checkFields()) {
                                                    buttonAddChekin.isClickable = true
                                                    buttonAddChekin.background.alpha = 255
                                                    buttonAddChekinLater.isClickable = true
                                                    buttonAddChekinLater.background.alpha = 255
                                                } else {
                                                    buttonAddChekin.isClickable = false
                                                    buttonAddChekin.background.alpha = 128
                                                    buttonAddChekinLater.isClickable = false
                                                    buttonAddChekinLater.background.alpha = 128
                                                }

                                            }
                                        }
                                    }
                            } else {
                                typeBookingView.visibility = View.GONE
                                numberRoomView.visibility = View.GONE
                            }

                            if (checkFields()) {
                                buttonAddChekin.isClickable = true
                                buttonAddChekin.background.alpha = 255
                                buttonAddChekinLater.isClickable = true
                                buttonAddChekinLater.background.alpha = 255
                            } else {
                                buttonAddChekin.isClickable = false
                                buttonAddChekin.background.alpha = 128
                                buttonAddChekinLater.isClickable = false
                                buttonAddChekinLater.background.alpha = 128
                            }

                        }
                    }
                }
            }
        }


        buttonAddChekin = view.findViewById(R.id.buttonAddGuestsNC)
        buttonAddChekin.setOnClickListener {
            addGuestsLater = false
            createNewChekin(buttonAddChekin)
        }
        buttonAddChekinLater = view.findViewById(R.id.buttonAddGuestsLaterNC)
        buttonAddChekinLater.setOnClickListener {
           addGuestsLater = true
            createNewChekin(buttonAddChekinLater)
        }
        buttonAddChekin.isClickable = false
        buttonAddChekin.background.alpha = 128
        buttonAddChekinLater.isClickable = false
        buttonAddChekinLater.background.alpha = 128





        namePropertyView = view.findViewById(R.id.propertyNC) as RelativeLayout
        namePropertyEditText = view.findViewById(R.id.propertyNCEditText) as EditSpinner



        responsibleView = view.findViewById(R.id.responsibleNC) as RelativeLayout
        responsibleEditText = view.findViewById(R.id.responsibleNCEditText) as EditText
        responsibleEditText.setOnClickListener {
            var intent = Intent(activity, CollaboratorsActivity::class.java)

            startActivityForResult(intent, 100)
        }
        responsibleView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        responsibleView.setBackgroundColor(Color.parseColor("#ffffff"))
        responsibleEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        responsibleEditText.setText(getString(R.string.me))


        checkInView = view.findViewById(R.id.checkInDateNC)
        checkInDate = view.findViewById(R.id.checkInDateNCEditText) as EditText
        val date = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }

        }

        checkInDate.setOnClickListener{


                // TODO Auto-generated method stub
            val datePicker = DatePickerDialog(
                activity,
                R.style.datePickerTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )

            println("Se va a  comprobar el country de la propiedad...")
            listProperties[propertyIndex].getJSONObject("location").let {
                location ->
                location.getJSONObject("country").let {
                    count ->
                    count.getString("code").let { country ->
                        println("Se comprueba el country de la propiedad...")
                        if (country == "IT") {
                            println("La propiedad para la nueva reserva es $country")
                            isItalianProperty = true
                        } else {
                            println("La propiedad para la nueva reserva es $country")
                            isItalianProperty = false
                        }
                    }
                }
            }


            if (isItalianProperty) {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_MONTH, -1)
                datePicker.datePicker.minDate = c.timeInMillis
                datePicker.show()
            } else {
                datePicker.show()
            }
        }

        nightsEditText = view.findViewById(R.id.nightsNCEditText)
        nightsView = view.findViewById(R.id.nightsNC)
        nightsEditText.addTextChangedListener(CustomTextWatcher(nightsEditText))
        nightsEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        guestsOlderEditText = view.findViewById(R.id.guestsOlderNCEditText)
        guestsOlder = view.findViewById(R.id.guestsOlderNC)
        guestsOlderEditText.addTextChangedListener(CustomTextWatcher(guestsOlderEditText))
        guestsOlderEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


        typeBookingEditText = view.findViewById(R.id.typeBookingEditTextNC)
        typeBookingView = view.findViewById(R.id.typeBookingNC)

        val adapterAux = ArrayAdapter(
            activity, R.layout.item_dropdown,
            typeBookingNames
        )
        typeBookingEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        typeBookingEditText.setAdapter(adapterAux)

        // triggered when one item in the list is clicked
        typeBookingEditText.setOnItemClickListener { parent, view, position, id ->
            typeBooking = typeBookingIDs[position]
            typeBookingView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            typeBookingView.setBackgroundColor(Color.parseColor("#ffffff"))
            typeBookingEditText.setBackgroundResource(R.drawable.edittext_bottom_line)


            if(checkFields()) {
                buttonAddChekin.isClickable = true
                buttonAddChekin.background.alpha = 255
                buttonAddChekinLater.isClickable = true
                buttonAddChekinLater.background.alpha = 255
            } else {
                buttonAddChekin.isClickable = false
                buttonAddChekin.background.alpha = 128
                buttonAddChekinLater.isClickable = false
                buttonAddChekinLater.background.alpha = 128
            }

        }



        return view
    }


    override fun onResume() {
        super.onResume()

        println("ENTRAMOS EN FRAGMENT NEW CHEKIN desde ONRESUME")

    }

    fun checkErrors(): Boolean {

        if (nightsEditText.text.toString().toInt() <= 1) {
            showDialogError(2)
            return false
        }

        if (guestsTotal >  1 && typeBooking == "S") {
            showDialogError(0)
            return false
        } else if (guestsTotal <= 1 && typeBooking != "S") {
            showDialogError(1)
            return false
        }
        return true
    }

    fun createNewChekin(buttonSelected: Button) {
        val params = HashMap<String, Any>()

        if (checkErrors()) {
            showDialogLoading()

            buttonSelected.isClickable = false

            val rightNow = Calendar.getInstance()
            val currentHourIn24Format =
                rightNow.get(Calendar.HOUR_OF_DAY) // return the hour in 24 hrs format (ranging from 0-23)

            val currentHourIn12Format = rightNow.get(Calendar.HOUR)

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val c = Calendar.getInstance()
            try {
                c.time = sdf.parse(checkInDate.text.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            c.add(
                Calendar.DATE,
                nightsEditText.text.toString().toInt()
            )  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
            val sdf1 = SimpleDateFormat("yyyy-MM-dd")
            val checkOutDate = sdf1.format(c.time)




            if (!otherResponsible) {
                if ((listProperties[propertyIndex].getBoolean("is_stat_registration_enabled"))) {
                    params["occupied_rooms_quantity"] = roomNumber
                }
            } else {
                if ((listProperties[propertyIndex].getBoolean("is_stat_registration_enabled"))) {
                    params["occupied_rooms_quantity"] = roomNumber
                    params["assigned_user"] = idCollaborator
                } else {
                    params["assigned_user"] = idCollaborator
                }

            }

            params["housing_id"] = listProperties[propertyIndex].getString("id")
            params["check_in_time"] =
                ("${rightNow.get(Calendar.HOUR_OF_DAY)}:${rightNow.get(Calendar.MINUTE)}")
            params["check_in_date"] = checkInDate.text.toString()
            params["check_out_date"] = checkOutDate.toString()
            val mapGuestGroup: MutableMap<String, Any> = mutableMapOf()

            if (countryProperty == "IT" || countryProperty == "AE") {
                mapGuestGroup.put("number_of_guests", guestsTotal)
                mapGuestGroup.put("type", typeBooking)
            } else {
                mapGuestGroup.put("number_of_guests", guestsTotal)
            }

            params["guest_group"] = mapGuestGroup


            activity?.let { it1 ->
                ChekinNewAPI.newChekin(
                    it1,
                    params
                ) { result, status ->
                    if (status) {
                        if (addGuestsLater) {
                            var intent = Intent(
                                activity,
                                NavigationAcitivity::class.java
                            )
                            intent.putExtra("selectedTab", 0)
                            startActivity(intent)
                        } else {

                            var intent = Intent(activity, AddGuest::class.java)
                            intent.putExtra("isNationalityProperty", countryProperty)
                            intent.putExtra("idBooking", result?.getString("id"))
                            intent.putExtra(
                                "noCheckedGuests",
                                guestsOlderEditText.text.toString().toInt()
                            )

                            intent.putExtra("checkinDate", checkInDate.text.toString())
                            if (result is JSONObject) {
                                intent.putExtra("bookingInfo", result.toString())
                                startActivity(intent)
                            }

                        }
                        UserProfile.needBookingReload =
                            true
                    } else {
                        buttonSelected.isClickable = true
                        hideLoadingDialog()
                    }
                }
            }
        }


    }

    private fun updateDate() {
        val myFormat = "yyyy/MM/dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        checkInDate.setText((sdf.format(myCalendar.time)).replace("/", "-"))
        checkInView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        checkInView.setBackgroundColor(Color.parseColor("#ffffff"))
        checkInDate.setBackgroundResource(R.drawable.edittext_bottom_line)

        if(checkFields()) {
            buttonAddChekin.isClickable = true
            buttonAddChekin.background.alpha = 255
            buttonAddChekinLater.isClickable = true
            buttonAddChekinLater.background.alpha = 255
        } else {
            buttonAddChekin.isClickable = false
            buttonAddChekin.background.alpha = 128
            buttonAddChekinLater.isClickable = false
            buttonAddChekinLater.background.alpha = 128
        }
    }

    fun getPropertiesNames(): ArrayList<String> {
        println("La cantidad de propiedades es: ${listProperties.count()}")
        for (i in 0..listProperties.count() - 1) {
            val property = listProperties.get(i)
            property.getString("name").let { name ->
                println("Se añade nombre de propiedad a nameProperties en index $i")
                nameProperties.add(name)
            }
        }
        println("nameProperties es: $nameProperties")
        return nameProperties
    }

    fun checkFields(): Boolean {

        if(namePropertyEditText.text.isBlank() || responsibleEditText.text.isBlank() || checkInDate.text.isBlank() || nightsEditText.text.isBlank() || guestsOlderEditText.text
                .isBlank()) {
            println("CHECKFIELDS - Algun campo principal esta vacio")
            return false
        }

        if (countryProperty == "IT" || countryProperty == "AE") {
            if (typeBookingEditText.text.isBlank()) {
                println("CHECKFIELDS - El type booking esta vacio")
                return false
            }
        }

        if (countryProperty == "IT" || countryProperty == "DE") {
            if (isRoomsActivated) {
                if (numberRoomEditText.text.isBlank()) {
                    println("CHECKFIELDS - El numero de habitaciones esta vacio")
                    return false
                }
            }
        }


         guestsTotal = guestsOlderEditText.text.toString().toInt()


        return true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        println("Entramos en onActivityResult")
        if (resultCode == 100) {
            println("El resultCode es 100 y añadimos el nuevo responsable")
            super.onActivityResult(requestCode, resultCode, intent)
            responsibleEditText.setText(intent?.getStringExtra("responsible"))
            otherResponsible = false
        } else if (resultCode == 101) {
            println("El resultCode es 101 y añadimos el nuevo responsable")
            otherResponsible = true
            responsibleEditText.setText(intent?.getStringExtra("responsible"))
            idCollaborator = intent?.getStringExtra("idResponsible").toString()
        }
    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == nightsEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nightsView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nightsView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == guestsOlderEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                guestsOlder.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                guestsOlder.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(checkFields()) {
                buttonAddChekin.isClickable = true
                buttonAddChekin.background.alpha = 255
                buttonAddChekinLater.isClickable = true
                buttonAddChekinLater.background.alpha = 255
            } else {
                buttonAddChekin.isClickable = false
                buttonAddChekin.background.alpha = 128
                buttonAddChekinLater.isClickable = false
                buttonAddChekinLater.background.alpha = 128
            }
        }

        override fun afterTextChanged(s: Editable) {

        }


    }

    fun showDialogLoading() {

        MyDialog = Dialog(activity)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.loading_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        progressBarLoading = MyDialog.findViewById(R.id.progressBarLoading) as ProgressBar
        MyDialog.setCancelable(false)
        MyDialog.setCanceledOnTouchOutside(false)



        MyDialog.show()

    }

    fun hideLoadingDialog() {
        MyDialog.dismiss()
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
            errorMessage.setText(getString(R.string.errorSingle))
        } else if (typeError == 1) {
            errorMessage.setText(getString(R.string.errorGroup))
        } else if (typeError == 2) {
            errorMessage.setText(getString(R.string.errorNights))
        }

        MyDialog.show()
        buttonError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

    }


    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }



}
