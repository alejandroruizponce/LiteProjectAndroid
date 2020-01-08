package com.chekinlite.app.CurrentVersion.Activities

import android.animation.LayoutTransition
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.OldVersionFiles.CollaboratorsActivity
import com.reginald.editspinner.EditSpinner
import com.yinglan.keyboard.HideUtil
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditChekin : AppCompatActivity() {

    lateinit var namePropertyView: RelativeLayout
    lateinit var responsibleView: RelativeLayout
    lateinit var checkInView: RelativeLayout
    lateinit var nightsView: RelativeLayout
    lateinit var guestsOlder: RelativeLayout
    lateinit var numberRoomView: RelativeLayout
    lateinit var typeBookingView: RelativeLayout

    lateinit var namePropertyEditText: EditSpinner
    lateinit var responsibleEditText: EditText
    lateinit var checkInDate: EditText
    lateinit var nightsEditText: EditText
    lateinit var guestsOlderEditText: EditText
    lateinit var numberRoomEditText: EditSpinner
    lateinit var typeBookingEditText: EditSpinner

    lateinit var buttonAddChekin: Button
    lateinit var buttonBack: Button
    lateinit var titleNewChekin: TextView

    lateinit var errorMessage: TextView
    lateinit var MyDialog: Dialog
    lateinit var buttonError: Button

    var propertyIndex = 0
    var nameProperties: ArrayList<String> = ArrayList()
    var listProperties: ArrayList<JSONObject> = ArrayList()
    val myCalendar = Calendar.getInstance()
    var roomsArray: ArrayList<String> = ArrayList()
    lateinit var bookingInfo: JSONObject
    var otherResponsible = false
    var roomNumber = 0
    var idCollaborator = ""
    var addGuestsLater = false
    var guestsTotal = 0
    var IS_EDIT_MODE = false

    lateinit var typeBookingNames: Array<String>
    lateinit var typeBookingIDs: Array<String>

    var typeBooking = ""

    var isItalianProperty = false
    var countryProperty = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_chekin)

        listProperties =
            UserProfile.listProperties
        bookingInfo = JSONObject(intent.getStringExtra("bookingInfo"))
        println("LA INFORMACION DE LA RESERVA A EDITAR ES: $bookingInfo")

        HideUtil.init(this);



        typeBookingNames = arrayOf(getString(R.string.individual), getString(
            R.string.grupo
        ), getString(R.string.familia))
        typeBookingIDs = arrayOf("S", "G", "F")

        UserProfile.tabIsDisplayed = 2

        this?.let {
            ChekinNewAPI.getAccommodations(it) { result, status ->
                if (status) {
                    if (result is JSONArray && result != null) {
                        for (num in 0..result.length() - 1) {
                            listProperties?.add(result[num] as JSONObject)
                        }
                        UserProfile.listProperties =
                            listProperties
                        val adapter = ArrayAdapter(
                            this, R.layout.item_dropdown,
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

                                listProperties[propertyIndex].getBoolean("is_stat_registration_enabled")
                                    .let { istatOn ->
                                        if (istatOn) {
                                            println("Se pone el numero de habitaciones visible")
                                            numberRoomView.visibility = View.VISIBLE

                                            listProperties[propertyIndex].getInt("rooms_quantity")
                                                .let { rooms ->
                                                    for (i in 0..rooms) {
                                                        var number = i + 1
                                                        roomsArray.add(number.toString())
                                                    }
                                                }


                                            val adapterAux = ArrayAdapter(
                                                this,
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
                                                numberRoomView.setBackgroundColor(Color.parseColor("#ffffff"))
                                                numberRoomEditText.setBackgroundResource(
                                                    R.drawable.edittext_bottom_line
                                                )


                                                if (checkFields()) {
                                                    buttonAddChekin.isClickable = true
                                                    buttonAddChekin.background.alpha = 255
                                                } else {
                                                    buttonAddChekin.isClickable = false
                                                    buttonAddChekin.background.alpha = 128
                                                }

                                            }
                                        } else {
                                            numberRoomView.visibility = View.GONE
                                        }
                                    }
                            } else if (countryProperty == "AE") {
                                typeBookingView.visibility = View.VISIBLE
                            } else {
                                typeBookingView.visibility = View.GONE
                            }

                            if (checkFields()) {
                                buttonAddChekin.isClickable = true
                                buttonAddChekin.background.alpha = 255
                            } else {
                                buttonAddChekin.isClickable = false
                                buttonAddChekin.background.alpha = 128
                            }

                        }
                    }
                }
            }
        }


        buttonAddChekin = findViewById(R.id.buttonAddGuestsEC)
        buttonAddChekin.setOnClickListener {
            addGuestsLater = false
            createNewChekin()
        }

        buttonBack = findViewById(R.id.buttonBackEC)
        buttonBack.setOnClickListener {
            finish()
        }

        buttonAddChekin.isClickable = false
        buttonAddChekin.background.alpha = 128

        numberRoomEditText = findViewById(R.id.numberRoomECEditText)
        numberRoomView = findViewById(R.id.numberRoomEC)
        numberRoomView.visibility = View.GONE





        namePropertyView = findViewById(R.id.propertyEC) as RelativeLayout
        namePropertyEditText = findViewById(R.id.propertyECEditText) as EditSpinner




        responsibleView = findViewById(R.id.responsibleEC) as RelativeLayout
        responsibleEditText = findViewById(R.id.responsibleECEditText) as EditText
        responsibleEditText.setOnClickListener {
            var intent = Intent(this, CollaboratorsActivity::class.java)

            startActivityForResult(intent, 100)
        }
        responsibleView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        responsibleView.setBackgroundColor(Color.parseColor("#ffffff"))
        responsibleEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        responsibleEditText.setText(getString(R.string.me))


        checkInView = findViewById(R.id.checkInDateEC)
        checkInDate = findViewById(R.id.checkInDateECEditText) as EditText
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
                this,
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

        nightsEditText = findViewById(R.id.nightsECEditText)
        nightsView = findViewById(R.id.nightsEC)
        nightsEditText.addTextChangedListener(CustomTextWatcher(nightsEditText))
        nightsEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        guestsOlderEditText = findViewById(R.id.guestsOlderECEditText)
        guestsOlder = findViewById(R.id.guestsOlderEC)
        guestsOlderEditText.addTextChangedListener(CustomTextWatcher(guestsOlderEditText))
        guestsOlderEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


        typeBookingEditText = findViewById(R.id.typeBookingEditTextEC)
        typeBookingView = findViewById(R.id.typeBookingEC)

        val adapterAux = ArrayAdapter(
            this, R.layout.item_dropdown,
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
            } else {
                buttonAddChekin.isClickable = false
                buttonAddChekin.background.alpha = 128
            }

        }

        setupEditMode()

    }

    fun setupEditMode() {

        bookingInfo.getJSONObject("housing").let {
                housing ->

            var i = 0
            for (property in listProperties) {
                if(property.getString("id") == housing.getString("id")) {
                    propertyIndex = i
                }
                i++
            }
            println("La informacion de la propiedad de la reserva a editar es: $housing")
            housing.getString("name").let {
                    name ->
                namePropertyEditText.setText(name)
            }
            housing.getJSONObject("location").let { location ->
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

                listProperties[propertyIndex].getBoolean("is_stat_registration_enabled")
                    .let { istatOn ->
                        if (istatOn) {
                            println("Se pone el numero de habitaciones visible")
                            numberRoomView.visibility = View.VISIBLE

                            listProperties[propertyIndex].getInt("rooms_quantity")
                                .let { rooms ->
                                    for (i in 0..rooms) {
                                        var number = i + 1
                                        roomsArray.add(number.toString())
                                    }
                                }

                            listProperties[propertyIndex].optInt("occupied_rooms_quantity", -1).let { rooms ->
                                numberRoomEditText.setText(rooms.toString())
                            }

                            val adapterAux = ArrayAdapter(
                                this, R.layout.item_dropdown,
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
                                numberRoomView.setBackgroundColor(Color.parseColor("#ffffff"))
                                numberRoomEditText.setBackgroundResource(R.drawable.edittext_bottom_line)


                                if (checkFields()) {
                                    buttonAddChekin.isClickable = true
                                    buttonAddChekin.background.alpha = 255
                                } else {
                                    buttonAddChekin.isClickable = false
                                    buttonAddChekin.background.alpha = 128
                                }

                            }
                        } else {
                            numberRoomView.visibility = View.GONE
                        }
                    }
            } else if (countryProperty == "DE") {
                println("Se pone el numero de habitaciones visible")
                listProperties[propertyIndex].getBoolean("is_stat_registration_enabled")
                    .let { istatOn ->
                        if (istatOn) {
                            numberRoomView.visibility = View.VISIBLE

                            listProperties[propertyIndex].getInt("rooms_quantity")
                                .let { rooms ->
                                    for (i in 0..rooms) {
                                        var number = i + 1
                                        roomsArray.add(number.toString())
                                    }
                                }

                            listProperties[propertyIndex].optInt("occupied_rooms_quantity", -1).let { rooms ->
                                numberRoomEditText.setText(rooms.toString())
                            }

                            val adapterAux = ArrayAdapter(
                                this, R.layout.item_dropdown,
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
                                numberRoomView.setBackgroundColor(Color.parseColor("#ffffff"))
                                numberRoomEditText.setBackgroundResource(R.drawable.edittext_bottom_line)


                                if (checkFields()) {
                                    buttonAddChekin.isClickable = true
                                    buttonAddChekin.background.alpha = 255
                                } else {
                                    buttonAddChekin.isClickable = false
                                    buttonAddChekin.background.alpha = 128
                                }

                            }
                        }
                    }
            } else if (countryProperty == "AE") {
                typeBookingView.visibility = View.VISIBLE
            } else {
                typeBookingView.visibility = View.GONE
            }

            if(checkFields()) {
                buttonAddChekin.isClickable = true
                buttonAddChekin.background.alpha = 255
            } else {
                buttonAddChekin.isClickable = false
                buttonAddChekin.background.alpha = 128
            }

        }

        bookingInfo.getString("check_in_date").let {
            checkD->
            checkInDate.setText(checkD.substring(0, 10))
            checkInView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            checkInView.setBackgroundColor(Color.parseColor("#ffffff"))
            checkInDate.setBackgroundResource(R.drawable.edittext_bottom_line)
        }

        bookingInfo.getInt("nights_of_stay").let {
            nights ->
            nightsEditText.setText(nights.toString())
        }

        bookingInfo.getJSONObject("guest_group").let {
            gGroup ->
            gGroup.getInt("number_of_guests").let {
                nGuests ->
                guestsOlderEditText.setText(nGuests.toString())
                guestsTotal = nGuests
            }
        }

        bookingInfo.getJSONObject("housing").let {
            housing ->
            housing.getBoolean("is_stat_registration_enabled").let {
                stat_enabled ->
                if (stat_enabled) {
                    housing.getInt("rooms_quantity").let {
                        nRooms ->
                        this.roomNumber = nRooms
                        numberRoomEditText.setText(nRooms.toString())
                        numberRoomView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                        numberRoomView.setBackgroundColor(Color.parseColor("#ffffff"))
                        numberRoomEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                        numberRoomView.visibility = View.VISIBLE
                    }
                }

            }
        }

        bookingInfo.getJSONObject("housing").let { housing ->
            housing.getJSONObject("location").let {
                location ->
                location.getJSONObject("country").let {
                    country ->
                    country.getString("code").let {
                        code ->
                        countryProperty = code

                        if (countryProperty == "IT" || countryProperty == "AE") {
                            bookingInfo.getJSONObject("guest_group").let {
                                gGroup ->
                                println("AVERIGUAMOS EL TYPE DE LA RESERVA...")
                                gGroup.getString("type").let {
                                    type ->
                                    typeBookingEditText.setText(typeBookingNames[typeBookingIDs.indexOf(type)])
                                    typeBooking = type
                                    typeBookingView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                                    typeBookingView.setBackgroundColor(Color.parseColor("#ffffff"))
                                    typeBookingEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                                    typeBookingView.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }

        if(checkFields()) {
            buttonAddChekin.isClickable = true
            buttonAddChekin.background.alpha = 255
        } else {
            buttonAddChekin.isClickable = false
            buttonAddChekin.background.alpha = 128
        }

    }


    override fun onResume() {
        super.onResume()





    }

    fun createNewChekin() {
        val params = HashMap<String, Any>()

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




        if(!otherResponsible) {
            if((listProperties[propertyIndex].getBoolean("is_stat_registration_enabled"))) {
                params["occupied_rooms_quantity"] = roomNumber
            }
        } else {
            if((listProperties[propertyIndex].getBoolean("is_stat_registration_enabled"))) {
                params["occupied_rooms_quantity"] = roomNumber
                params["assigned_user"] = idCollaborator
            } else {
                params["assigned_user"] = idCollaborator
            }

        }

        params["housing_id"] = listProperties[propertyIndex].getString("id")
        params["check_in_time"] = ("${rightNow.get(Calendar.HOUR_OF_DAY)}:${rightNow.get(Calendar.MINUTE)}")
        params["check_in_date"] = checkInDate.text.toString()
        params["check_out_date"] = checkOutDate.toString()
        val mapGuestGroup : MutableMap<String,Any> = mutableMapOf()

        if (countryProperty == "IT" || countryProperty == "AE") {
            mapGuestGroup.put("number_of_guests", guestsTotal)
            mapGuestGroup.put("type", typeBooking)
        } else {
            mapGuestGroup.put("number_of_guests", guestsTotal)
        }

        params["guest_group"] = mapGuestGroup


        this?.let { it1 ->
            ChekinNewAPI.updateChekin(
                it1,
                bookingInfo.getString("id"),
                params
            ) { result, status ->
                if (status) {
                    var intent = Intent(
                        this,
                        NavigationAcitivity::class.java
                    )
                    startActivity(intent)
                }
                UserProfile.needBookingReload =
                    true
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
        } else {
            buttonAddChekin.isClickable = false
            buttonAddChekin.background.alpha = 128
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
            return false
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
            } else {
                buttonAddChekin.isClickable = false
                buttonAddChekin.background.alpha = 128
            }
        }

        override fun afterTextChanged(s: Editable) {

        }


    }


    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }



}
