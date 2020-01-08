package com.chekinlite.app.CurrentVersion.Activities

import android.animation.LayoutTransition
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.reginald.editspinner.EditSpinner
import java.util.*
import kotlin.collections.ArrayList
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.CurrentVersion.Models.CountrySearchModel
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.CurrentVersion.Networking.ChekinNewAPI
import com.chekinlite.app.CurrentVersion.Helpers.ValidatorDNI
import com.chekinlite.app.OldVersionFiles.ChekinAPI
import com.justinnguyenme.base64image.Base64Image
import com.kyanogen.signatureview.SignatureView
import com.regula.documentreader.api.DocumentReader
import com.regula.documentreader.api.enums.*
import com.regula.documentreader.api.results.DocumentReaderResults
import com.squareup.picasso.Picasso
import com.yinglan.keyboard.HideUtil
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import kotlinx.android.synthetic.main.activity_add_guest.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat


class AddGuest : AppCompatActivity() {

    lateinit var buttonScan: Button
    lateinit var buttonBack: Button
    lateinit var ExtraButtonAddSign: Button
    lateinit var titleAddGuest: TextView

    lateinit var arrowResidenceCity: ImageView

    lateinit var nameView: ConstraintLayout
    lateinit var surnameView: RelativeLayout
    lateinit var secondSurnameView: RelativeLayout
    lateinit var sexView: RelativeLayout
    lateinit var nationalityView: RelativeLayout
    lateinit var birthDateView: RelativeLayout
    lateinit var documentTypeView: RelativeLayout
    lateinit var documentNumberView: RelativeLayout
    lateinit var expeditionDateView: RelativeLayout
    lateinit var expeditionCountryView: RelativeLayout
    lateinit var birthCountryView: RelativeLayout
    lateinit var residenceView: RelativeLayout
    lateinit var birthCityView: RelativeLayout
    lateinit var expeditionCityView: RelativeLayout
    lateinit var residenceCityView: RelativeLayout
    lateinit var residenceAddressView: RelativeLayout

    lateinit var nameEditText: EditText
    lateinit var surnameEditText: EditText
    lateinit var secondSurnameEditText: EditText
    lateinit var sexEditText: EditSpinner
    lateinit var birthDateEditText: EditText
    lateinit var nationalityEditText: EditText
    lateinit var documentTypeEditText: EditSpinner
    lateinit var documentNumberEditText: EditText
    lateinit var expeditionDateEditText: EditText
    lateinit var expeditionCountryEditText: EditText
    lateinit var birthCountryEditText: EditText
    lateinit var residenceCountryEditText: EditText
    lateinit var residenceAddressEditText: EditText
    lateinit var birthCityEditText: EditText
    lateinit var residenceCityEditText: EditText
    lateinit var expeditionCityEditText: EditText
    lateinit var signatureView: RelativeLayout
    lateinit var signatureField: SignatureView
    lateinit var addDocumentView: ConstraintLayout
    lateinit var MyDialog: Dialog
    lateinit var progressBarLoading: ProgressBar

    lateinit var buttonAddSignature: Button
    lateinit var buttonAddDocuments: Button
    lateinit var buttonCancelSignature: Button
    lateinit var buttonAddGuest: Button
    lateinit var buttonAddGuestLater: Button
    lateinit var buttonSendChekin: Button

    lateinit var errorMessage: TextView
    lateinit var errorTitle: TextView
    lateinit var errorDialog: Dialog
    lateinit var buttonCorrectError: Button
    lateinit var signatureImageView: ImageView
    lateinit var documentsImageView: ImageView


    var params = HashMap<String, Any>()

    var sexIndex = 0
    var nationIndex = 0
    var docTypeIndex = 0
    val myCalendar = Calendar.getInstance()
    var listCountriesName: ArrayList<CountrySearchModel> = ArrayList()
    var listCountriesCode: ArrayList<String> = ArrayList()
    var listItalyCitiesNames: ArrayList<CountrySearchModel> = ArrayList()
    var listItalyCitiesIDs: ArrayList<String> = ArrayList()
    var listDocTypes: ArrayList<JSONObject> = ArrayList()
    var listDocTypesNames: ArrayList<String> = ArrayList()
    var listDocTypesIDs: ArrayList<String> = ArrayList()
    var nationality = ""
    var residenceCountry = ""
    var birthCountry = ""
    var birthCity = ""
    var expeditionCity = ""
    var expeditionCountry = ""
    var residenceCity = ""
    var residenceCityIsOn = false
    var sex = ""
    var docType = ""
    var signature = ""
    var isNationalityProperty = ""
    var idBooking = ""
    var noCheckedGuests = 0
    var checkinDate = ""
    var isISTATOn = false
    var documentsScanned = false
    lateinit var bookingInfo: JSONObject
    lateinit var guestInfo: JSONObject
    var isExtraGuest = false
    private var doRfid: Boolean = false
    private var loadingDialog: AlertDialog? = null

    lateinit var loadDialog: Dialog

    var IS_EDIT_MODE = false
    var newSignatureAdded = false

    var isIDScanned = false
    var isPassportScanned = false
    var isFirstGuest = false
    var isLeaderGuest = false

    //DocumentReader processing callback
    lateinit var completion: DocumentReader.DocumentReaderCompletion






    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_guest)
        HideUtil.init(this);



        println("ENTRAMOS EN ONCREATE DE ADDGUEST")
        buttonAddGuest = findViewById(R.id.buttonAddGuest)
        buttonAddGuestLater = findViewById(R.id.buttonAddAnotherGuest)

        UserProfile.tabIsDisplayed = -1

        completion =  DocumentReader.DocumentReaderCompletion { action, results, error ->
            //processing is finished, all results are ready
            if (action == DocReaderAction.COMPLETE) {
                if (loadingDialog != null && loadingDialog!!.isShowing) {
                    loadingDialog!!.dismiss()
                }

                //Checking, if nfc chip reading should be performed
                if (doRfid && results != null && results.chipPage != 0) {
                    if (DocumentReader.Instance().rfidScenario() == null) {
                        // DocumentReader.Instance().rfidScenario() = RfidScenario()
                    }

                    //setting the chip's access key - mrz on car access number
                    var accessKey: String?
                    accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                    if (accessKey != null && !accessKey.isEmpty()) {
                        accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                            .replace("^", "").replace("\n", "")
                        DocumentReader.Instance().rfidScenario().mrz = accessKey
                        DocumentReader.Instance().rfidScenario().pacePasswordType =
                            eRFID_Password_Type.PPT_MRZ
                    } else {
                        accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_CARD_ACCESS_NUMBER)
                        if (accessKey != null && !accessKey.isEmpty()) {
                            DocumentReader.Instance().rfidScenario().password = accessKey
                            DocumentReader.Instance().rfidScenario().pacePasswordType =
                                eRFID_Password_Type.PPT_CAN
                        }
                    }

                    //starting chip reading
                    DocumentReader.Instance().startRFIDReader { rfidAction, results_RFIDReader, _ ->
                        if (rfidAction == DocReaderAction.COMPLETE) {
                            displayResults(results_RFIDReader)
                        }
                    }
                } else {
                    displayResults(results)
                }
            } else {
                //something happened before all results were ready
                if (action == DocReaderAction.CANCEL) {
                    Toast.makeText(this, "Scanning was cancelled", Toast.LENGTH_LONG).show()
                } else if (action == DocReaderAction.ERROR) {
                    Toast.makeText(this, "Error:$error", Toast.LENGTH_LONG).show()
                }
            }
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        println("ENTRAMOS EN ONCREATE DE ADDGUEST")

        IS_EDIT_MODE = intent.getBooleanExtra("IS_EDIT_MODE", false)
        isNationalityProperty = intent.getStringExtra("isNationalityProperty")
        println("isNationalityProperty es: $isNationalityProperty")
        idBooking = intent.getStringExtra("idBooking")
        noCheckedGuests = intent.getIntExtra("noCheckedGuests", 0)
        if (intent.getStringExtra("checkinDate") !=  null) {
            checkinDate = intent.getStringExtra("checkinDate")
        }
        isExtraGuest = intent.getBooleanExtra("isGuestExtra", false)
        if (intent.getStringExtra("bookingInfo") != null) {
            bookingInfo = JSONObject(intent.getStringExtra("bookingInfo"))
            println("EL VALOR DE BOOKINGINFO ES: $bookingInfo")
        }

        println("EL VALOR DE noCHECKEDGUESTS ES: $noCheckedGuests")

        ChekinNewAPI.getLocations(
            this,
            "IT",
            "3"
        ) { result, status ->
            if (status) {
                if (result is JSONObject) {
                    result.optJSONArray("results").let { resultDiv3 ->

                        for (i in 0..resultDiv3.length() - 1) {
                            resultDiv3[i].let { resultDiv ->
                                if (resultDiv is JSONObject) {
                                    resultDiv.optJSONObject("division_level_3").let { div3City ->
                                        div3City.getString("name").let { name ->
                                            listItalyCitiesNames.add(
                                                CountrySearchModel(
                                                    name,
                                                    i
                                                )
                                            )
                                        }
                                        div3City.getString("code").let { code ->
                                            listItalyCitiesIDs.add(code)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }






        birthCountryView = findViewById(R.id.birthCountryAG)
        expeditionCountryView = findViewById(R.id.expeditionCountryAG)
        birthCountryEditText = findViewById(R.id.birthCountryAGEditText)

        signatureView = findViewById(R.id.signature_view)
        signatureView.setOnClickListener {
            showDialogSignature(0)
        }

        addDocumentView = findViewById(R.id.AddDocumentsLayout)
        addDocumentView.setOnClickListener {
            var i =  Intent(this, ScanDocumentsAddGuest::class.java)
            startActivityForResult(i, 100);
        }


        buttonScan = findViewById(R.id.buttonScanAG)
        buttonScan.setOnClickListener{
            if (DocumentReader.Instance().documentReaderIsReady) {
                //starting video processing
                DocumentReader.Instance().showScanner(completion)
            }
        }
        openScanner()


        nameView = findViewById(R.id.nameAG) as ConstraintLayout
        nameEditText = findViewById(R.id.nameAGEditText) as EditText
        nameEditText.addTextChangedListener(CustomTextWatcher(nameEditText))
        nameEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        surnameView = findViewById(R.id.surnameAG) as RelativeLayout
        surnameEditText = findViewById(R.id.surnameAGEditText) as EditText
        surnameEditText.addTextChangedListener(CustomTextWatcher(surnameEditText))
        surnameEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        secondSurnameView = findViewById(R.id.secondSurnameAG) as RelativeLayout
        secondSurnameEditText = findViewById(R.id.secondSurnameAGEditText) as EditText
        secondSurnameEditText.addTextChangedListener(CustomTextWatcher(secondSurnameEditText))
        secondSurnameEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })



        sexView = findViewById(R.id.sexAG) as RelativeLayout
        sexEditText = findViewById(R.id.sexAGEditText) as EditSpinner
        val adapterSex = ArrayAdapter(
            this, R.layout.item_dropdown,
            resources.getStringArray(R.array.sexType)

        )
        sexEditText.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        sexEditText.setAdapter(adapterSex)

        // triggered when one item in the list is clicked
        sexEditText.setOnItemClickListener { parent, view, position, id ->
            sexIndex = position
            sexView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            sexView.setBackgroundColor(Color.parseColor("#ffffff"))
            sexEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

            if(checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }


        }

        nationalityView = findViewById(R.id.nationalityAG) as RelativeLayout
        nationalityEditText = findViewById(R.id.nationalityAGEditText) as EditText

        Locale.setDefault(Locale(getString(R.string.es)))
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.length > 0 && !countries.contains(country)) {
                countries.add(country)
            }

        }
        Collections.sort(countries)
        var i = 0
        for (country in countries) {
            //println(country)

            val country2code = getCountryCode(country)
            if(country2code != "") {
                if (country.unaccent() == "Espana") {
                    listCountriesName.add(
                        CountrySearchModel(
                            "España",
                            i
                        )
                    )
                } else {
                    listCountriesName.add(
                        CountrySearchModel(
                            country.unaccent(),
                            i
                        )
                    )
                }

                listCountriesCode.add(country2code)
            }
            i++

        }
        //System.out.println("# countries found: " + countries.count())

        val context = this




        val nationalitySearchDialog = SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName,
                        object: SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {



                                if (item != null) {
                                    nationIndex = item.id
                                }
                                nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                                nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                                nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)


                                nationality = listCountriesCode.get(nationIndex)
                                println("La nacionalidad escogida es: $nationality")

                                nationalityEditText.setText(item?.title)

                                if (isNationalityProperty == "ES" && nationality == "ES") {
                                    secondSurnameView.visibility = View.VISIBLE
                                } else {
                                    secondSurnameView.visibility = View.GONE
                                }


                                if(checkFields()) {
                                    buttonAddGuest.isClickable = true
                                    buttonAddGuest.background.alpha = 255
                                    if (noCheckedGuests > 1) {
                                        buttonAddGuestLater.isClickable = true
                                        buttonAddGuestLater.background.alpha = 255
                                    } else {
                                        buttonAddGuestLater.visibility = View.GONE
                                    }
                                } else {
                                    buttonAddGuest.isClickable = false
                                    buttonAddGuest.background.alpha = 128
                                    buttonAddGuestLater.isClickable = false
                                    buttonAddGuestLater.background.alpha = 128

                                }


                                dialog?.dismiss();
                            }
                        })




        birthCityView = findViewById(R.id.birthCityAG)
        expeditionCityView = findViewById(R.id.expeditionCityAG)
        residenceCityView = findViewById(R.id.residenceCityAG)
        birthCityEditText = findViewById(R.id.birthCityAGEditText)
        expeditionCityEditText = findViewById(R.id.expeditionCityAGEditText)
        residenceCityEditText = findViewById(R.id.residenceCityAGEditText)
        residenceAddressEditText = findViewById(R.id.residenceAddressAGEditText)
        residenceAddressView = findViewById(R.id.residenceAdressViewAG)


        //nationalityEditText.setInputType(0);
        nationalityEditText.setOnClickListener { v ->

            nationalitySearchDialog.show()

            nationalitySearchDialog.searchBox.setTextColor(getColor(R.color.darkBlue))
            nationalitySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))


        }




        birthDateView = findViewById(R.id.birthDateAG)
        birthDateEditText = findViewById(R.id.birthDateAGEditText) as EditText
        val date = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateBirthDate()
            }

        }

        birthDateEditText.setOnClickListener{

            // TODO Auto-generated method stub
            val datePicker = DatePickerDialog(
                this,
                R.style.datePickerTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )


            val c = Calendar.getInstance()
            c.add(Calendar.YEAR, -16)//Year,Mounth -1,Day
            datePicker.datePicker.maxDate = c.timeInMillis
            datePicker.show()
        }

        documentTypeView = findViewById(R.id.documentTypeAG) as RelativeLayout
        documentTypeEditText = findViewById(R.id.documentTypeAGEditText) as EditSpinner







        documentNumberView = findViewById(R.id.documentNumberAG) as RelativeLayout
        documentNumberEditText = findViewById(R.id.documentNumberAGEditText) as EditText
        documentNumberEditText.addTextChangedListener(CustomTextWatcher(documentNumberEditText))
        documentNumberEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        expeditionDateView = findViewById(R.id.expeditionDateAG)
        expeditionDateEditText = findViewById(R.id.expeditionDateAGEditText) as EditText
        val dateExp = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateExpDate()
            }

        }

        expeditionDateEditText.setOnClickListener{

            // TODO Auto-generated method stub
            val datePicker = DatePickerDialog(
                this,
                R.style.datePickerTheme, dateExp, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )

            val c = Calendar.getInstance()
            c.add(Calendar.YEAR, -10)//Year,Mounth -1,Day
            datePicker.datePicker.minDate = c.timeInMillis
            datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        if (isNationalityProperty == "IT" || isNationalityProperty == "PT" || isNationalityProperty == "DE") {

            expeditionCountryEditText = findViewById(R.id.expeditionCountryAGEditText)
            if (isNationalityProperty != "DE") {
                birthCountryView.visibility = View.VISIBLE
            }


            expeditionDateView.visibility = View.GONE

            if (isNationalityProperty == "IT") {
                if (!IS_EDIT_MODE) {
                    bookingInfo.getJSONObject("guest_group").let { gGroup ->
                        gGroup.optInt("number_of_guests", -1).let { nGuests ->
                            println("El total de huespedes es: $nGuests y quedan pendientes: $noCheckedGuests")
                            if (noCheckedGuests < nGuests) {
                                expeditionCountryView.visibility = View.GONE
                                documentNumberView.visibility = View.GONE
                                documentTypeView.visibility = View.GONE
                                isFirstGuest = false
                            } else if (noCheckedGuests == nGuests) {

                                expeditionCountryView.visibility = View.VISIBLE
                                documentNumberView.visibility = View.VISIBLE
                                documentTypeView.visibility = View.VISIBLE
                                isFirstGuest = true
                            }
                        }
                    }
                }
            } else if (isNationalityProperty == "PT") {
                expeditionCountryView.visibility = View.VISIBLE
                documentNumberView.visibility = View.VISIBLE
                documentTypeView.visibility = View.VISIBLE
            }



            if (isNationalityProperty == "IT") {

                val birthCountrySearchDialog =
                    SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName,
                        object : SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {
                                birthCountryView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                birthCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                birthCountryEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                                if (item != null) {
                                    birthCountry = listCountriesCode.get(item.id)
                                }

                                birthCountryEditText.setText(item?.title)

                                if (birthCountry == "IT") {

                                    birthCityView.visibility = View.VISIBLE


                                    val birthCitySearchDialog = SimpleSearchDialogCompat(context,
                                        getString(R.string.paises),
                                        "",
                                        null,
                                        listItalyCitiesNames,
                                        object : SearchResultListener<CountrySearchModel> {
                                            override fun onSelected(
                                                dialog: BaseSearchDialogCompat<*>?,
                                                item: CountrySearchModel?,
                                                position: Int
                                            ) {
                                                birthCityView.layoutTransition.enableTransitionType(
                                                    LayoutTransition.CHANGING
                                                )
                                                birthCityView.setBackgroundColor(Color.parseColor("#ffffff"))
                                                birthCityEditText.setBackgroundResource(
                                                    R.drawable.edittext_bottom_line
                                                )

                                                if (item != null) {
                                                    birthCity = listItalyCitiesIDs[item.id]
                                                }
                                                birthCityEditText.setText(item?.title)

                                                if (checkFields()) {
                                                    buttonAddGuest.isClickable = true
                                                    buttonAddGuest.background.alpha = 255
                                                    if (noCheckedGuests > 1) {
                                                        buttonAddGuestLater.isClickable = true
                                                        buttonAddGuestLater.background.alpha = 255
                                                    } else {
                                                        buttonAddGuestLater.visibility = View.GONE
                                                    }
                                                } else {
                                                    buttonAddGuest.isClickable = false
                                                    buttonAddGuest.background.alpha = 128
                                                    buttonAddGuestLater.isClickable = false
                                                    buttonAddGuestLater.background.alpha = 128

                                                }



                                                dialog?.dismiss();
                                            }
                                        })

                                    birthCityEditText.setInputType(0);
                                    birthCityEditText.setOnClickListener { v ->
                                        birthCitySearchDialog.show()
                                        birthCitySearchDialog.titleTextView.setTextColor(getColor(
                                            R.color.darkBlue
                                        ))
                                    }

                                } else {
                                    birthCityView.visibility = View.GONE
                                }


                                dialog?.dismiss();
                            }
                        })

                birthCountryEditText.setInputType(0);
                birthCountryEditText.setOnClickListener {
                    birthCountrySearchDialog.show()
                    birthCountrySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))
                }


                val expCountrySearchDialog =
                    SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName,
                        object : SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {
                                expeditionCountryView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                expeditionCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                expeditionCountryEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                                if (item != null) {
                                    expeditionCountry = listCountriesCode.get(item.id)
                                }
                                expeditionCountryEditText.setText(item?.title)

                                if (expeditionCountry == "IT") {
                                    expeditionCityView.visibility = View.VISIBLE
                                    val expCitySearchDialog =
                                        SimpleSearchDialogCompat(context,
                                            getString(R.string.paises),
                                            "",
                                            null,
                                            listItalyCitiesNames,
                                            object : SearchResultListener<CountrySearchModel> {
                                                override fun onSelected(
                                                    dialog: BaseSearchDialogCompat<*>?,
                                                    item: CountrySearchModel?,
                                                    position: Int
                                                ) {
                                                    expeditionCityView.layoutTransition.enableTransitionType(
                                                        LayoutTransition.CHANGING
                                                    )
                                                    expeditionCityView.setBackgroundColor(
                                                        Color.parseColor(
                                                            "#ffffff"
                                                        )
                                                    )
                                                    expeditionCityEditText.setBackgroundResource(
                                                        R.drawable.edittext_bottom_line
                                                    )

                                                    if (item != null) {
                                                        expeditionCity = listItalyCitiesIDs[item.id]
                                                    }


                                                    expeditionCityEditText.setText(item?.title)

                                                    if (checkFields()) {
                                                        buttonAddGuest.isClickable = true
                                                        buttonAddGuest.background.alpha = 255
                                                        if (noCheckedGuests > 1) {
                                                            buttonAddGuestLater.isClickable = true
                                                            buttonAddGuestLater.background.alpha =
                                                                255
                                                        } else {
                                                            buttonAddGuestLater.visibility =
                                                                View.GONE
                                                        }
                                                    } else {
                                                        buttonAddGuest.isClickable = false
                                                        buttonAddGuest.background.alpha = 128
                                                        buttonAddGuestLater.isClickable = false
                                                        buttonAddGuestLater.background.alpha = 128

                                                    }


                                                    dialog?.dismiss();
                                                }
                                            })

                                    expeditionCityEditText.setInputType(0);

                                    expeditionCityEditText.setOnClickListener { v ->
                                        expCitySearchDialog.show()
                                        expCitySearchDialog.titleTextView.setTextColor(getColor(
                                            R.color.darkBlue
                                        ))

                                    }
                                } else {
                                    expeditionCityView.visibility = View.GONE
                                }




                                dialog?.dismiss();
                            }
                        })

                expeditionCountryEditText.setInputType(0);

                expeditionCountryEditText.setOnClickListener {
                    expCountrySearchDialog.show()
                    expCountrySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))
                }


            } else if (isNationalityProperty == "PT") {
                val birthCountrySearchDialog =
                    SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName,
                        object : SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {
                                birthCountryView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                birthCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                birthCountryEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                                if (item != null) {
                                    birthCountry = listCountriesCode.get(item.id)
                                }

                                birthCountryEditText.setText(item?.title)

                                dialog?.dismiss();

                            }
                        })

                birthCountryEditText.setInputType(0);
                birthCountryEditText.setOnClickListener {
                    birthCountrySearchDialog.show()
                    birthCountrySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))
                }

                val expCountrySearchDialog =
                    SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName,
                        object : SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {
                                expeditionCountryView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                expeditionCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                expeditionCountryEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                                if (item != null) {
                                    expeditionCountry = listCountriesCode.get(item.id)
                                }
                                expeditionCountryEditText.setText(item?.title)

                                dialog?.dismiss();
                            }
                        })

                expeditionCountryEditText.setInputType(0);
                expeditionCountryEditText.setOnClickListener {
                    expCountrySearchDialog.show()
                    expCountrySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))
                }

            }



            if (isNationalityProperty == "IT") {
                bookingInfo.getJSONObject("housing").let { housing ->
                    housing.getBoolean("is_stat_registration_enabled").let { stat_enabled ->
                        if (stat_enabled) {
                            isISTATOn = true
                            residenceView = findViewById(R.id.residenceCountryAG)
                            residenceCountryEditText = findViewById(R.id.residenceCountryAGEditText)
                            residenceView.visibility = View.VISIBLE

                            val resCountrySearchDialog =
                                SimpleSearchDialogCompat(this, getString(R.string.paises),
                                    "", null, listCountriesName,
                                    object : SearchResultListener<CountrySearchModel> {
                                        override fun onSelected(
                                            dialog: BaseSearchDialogCompat<*>?,
                                            item: CountrySearchModel?,
                                            position: Int
                                        ) {
                                            residenceView.layoutTransition.enableTransitionType(
                                                LayoutTransition.CHANGING
                                            )
                                            residenceView.setBackgroundColor(Color.parseColor("#ffffff"))
                                            residenceCountryEditText.setBackgroundResource(
                                                R.drawable.edittext_bottom_line
                                            )

                                            if (item != null) {
                                                residenceCountry = listCountriesCode.get(item.id)
                                            }

                                            println("El pais de residencia añadido es: ${residenceCountry}")
                                            residenceCountryEditText.setText(item?.title)

                                            if (residenceCountry == "IT") {
                                                housing.getJSONObject("stat_account")
                                                    .let { stat_account ->
                                                        stat_account.getString("type").let { type ->
                                                            println("Se comprueba el tipo de ISTAT para la residencia...")
                                                            residenceCityView.visibility =
                                                                View.VISIBLE
                                                            residenceCityEditText = findViewById(
                                                                R.id.residenceCityAGEditText
                                                            )
                                                            residenceCityEditText.setFocusable(false);
                                                            residenceCityEditText.setClickable(false);
                                                            residenceCityIsOn = true

                                                            val residenCitySearchDialog =
                                                                SimpleSearchDialogCompat(context,
                                                                    getString(R.string.paises),
                                                                    "",
                                                                    null,
                                                                    listItalyCitiesNames,
                                                                    object :
                                                                        SearchResultListener<CountrySearchModel> {
                                                                        override fun onSelected(
                                                                            dialog: BaseSearchDialogCompat<*>?,
                                                                            item: CountrySearchModel?,
                                                                            position: Int
                                                                        ) {
                                                                            residenceCityView.layoutTransition.enableTransitionType(
                                                                                LayoutTransition.CHANGING
                                                                            )
                                                                            residenceCityView.setBackgroundColor(
                                                                                Color.parseColor("#ffffff")
                                                                            )
                                                                            residenceCityEditText.setBackgroundResource(
                                                                                R.drawable.edittext_bottom_line
                                                                            )


                                                                            if (item != null) {
                                                                                residenceCity =
                                                                                    listItalyCitiesIDs[item.id]
                                                                            }

                                                                            residenceCityEditText.setText(
                                                                                item?.title
                                                                            )

                                                                            residenceCityIsOn = true

                                                                            if (checkFields()) {
                                                                                buttonAddGuest.isClickable =
                                                                                    true
                                                                                buttonAddGuest.background.alpha =
                                                                                    255
                                                                                if (noCheckedGuests > 1) {
                                                                                    buttonAddGuestLater.isClickable =
                                                                                        true
                                                                                    buttonAddGuestLater.background.alpha =
                                                                                        255
                                                                                } else {
                                                                                    buttonAddGuestLater.visibility =
                                                                                        View.GONE
                                                                                }
                                                                            } else {
                                                                                buttonAddGuest.isClickable =
                                                                                    false
                                                                                buttonAddGuest.background.alpha =
                                                                                    128
                                                                                buttonAddGuestLater.isClickable =
                                                                                    false
                                                                                buttonAddGuestLater.background.alpha =
                                                                                    128

                                                                            }

                                                                            dialog?.dismiss();
                                                                        }
                                                                    })

                                                            residenceCityEditText.setInputType(0);

                                                            residenceCityEditText.setOnClickListener { v ->
                                                                print("Se pulsa residence City Edit Text...")
                                                                residenCitySearchDialog.show()
                                                                residenCitySearchDialog.titleTextView.setTextColor(
                                                                    getColor(R.color.darkBlue)
                                                                )

                                                            }
                                                        }
                                                    }
                                            } else {
                                                residenceCityView.visibility = View.GONE
                                            }



                                            dialog?.dismiss();
                                        }
                                    })

                            residenceCountryEditText.setOnClickListener {
                                resCountrySearchDialog.show()
                                resCountrySearchDialog.titleTextView.setTextColor(getColor(
                                    R.color.darkBlue
                                ))
                            }


                        }
                    }
                }
            } else if (isNationalityProperty == "PT" || isNationalityProperty == "DE") {

                residenceView = findViewById(R.id.residenceCountryAG)
                residenceCountryEditText = findViewById(R.id.residenceCountryAGEditText)
                residenceView.visibility = View.VISIBLE

                residenceCityView.visibility =
                    View.VISIBLE
                arrowResidenceCity = findViewById(R.id.arrowResidenceCity)
                arrowResidenceCity.visibility = View.INVISIBLE

                residenceCityEditText = findViewById(R.id.residenceCityAGEditText)
                residenceCityEditText.addTextChangedListener(CustomTextWatcher(residenceCityEditText))
                residenceCityEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        hideKeyboard(v)
                    }
                })

                if (isNationalityProperty == "DE") {
                    residenceAddressView.visibility = View.VISIBLE

                    residenceAddressEditText.addTextChangedListener(CustomTextWatcher(residenceAddressEditText))
                    residenceAddressEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
                        if (!hasFocus) {
                            hideKeyboard(v)
                        }
                    })
                }



                residenceCityIsOn = true

                val resCountrySearchDialog =
                    SimpleSearchDialogCompat(this, getString(R.string.paises),
                        "", null, listCountriesName, object : SearchResultListener<CountrySearchModel> {
                            override fun onSelected(
                                dialog: BaseSearchDialogCompat<*>?,
                                item: CountrySearchModel?,
                                position: Int
                            ) {
                                residenceView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                residenceView.setBackgroundColor(Color.parseColor("#ffffff"))
                                residenceCountryEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                                if (item != null) {
                                    residenceCountry = listCountriesCode.get(item.id)
                                }

                                println("El pais de residencia añadido es: ${residenceCountry}")
                                residenceCountryEditText.setText(item?.title)

                                dialog?.dismiss();





                                if (checkFields()) {
                                    buttonAddGuest.isClickable =
                                        true
                                    buttonAddGuest.background.alpha =
                                        255
                                    if (noCheckedGuests > 1) {
                                        buttonAddGuestLater.isClickable =
                                            true
                                        buttonAddGuestLater.background.alpha =
                                            255
                                    } else {
                                        buttonAddGuestLater.visibility =
                                            View.GONE
                                    }
                                } else {
                                    buttonAddGuest.isClickable =
                                        false
                                    buttonAddGuest.background.alpha =
                                        128
                                    buttonAddGuestLater.isClickable =
                                        false
                                    buttonAddGuestLater.background.alpha =
                                        128

                                }




                                residenceCityEditText.setOnClickListener { v ->
                                    print("Se pulsa residence City Edit Text...")

                                }

                            }

                        })

                residenceCountryEditText.setOnClickListener {
                    resCountrySearchDialog.show()
                    resCountrySearchDialog.titleTextView.setTextColor(getColor(R.color.darkBlue))
                }


            }

            if (checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }
        } else if (isNationalityProperty == "AE") {
            addDocumentView.visibility = View.VISIBLE
            documentTypeView.visibility = View.GONE
        }




        buttonBack = findViewById(R.id.buttonBackAG)
        buttonBack.setOnClickListener {
            if (!IS_EDIT_MODE) {
                var intent = Intent(this, NavigationAcitivity::class.java)
                intent.putExtra("selectedTab", 0)
                startActivity(intent)
                UserProfile.needBookingReload = true
            } else {
                finish()
            }

        }

        buttonSendChekin = findViewById(R.id.buttonSendChekinA)
        buttonSendChekin.setOnClickListener {
            var intent = Intent(this, SendChekinOnline::class.java)
            intent.putExtra("idBooking", idBooking)
            intent.putExtra("fromAddGuest", true)
            intent.putExtra("isNationalityProperty", isNationalityProperty)
            intent.putExtra("noCheckedGuests", noCheckedGuests)
            intent.putExtra("checkinDate", checkinDate)
            startActivity(intent)
        }


        buttonAddGuest.setOnClickListener {
            if (checkErrors()) {
                showDialogLoading()

                generateParameters()

                if (!IS_EDIT_MODE) {

                    ChekinNewAPI.addGuestRegular(
                        this,
                        params
                    ) { result, status ->
                        if (status) {
                            UserProfile.needBookingReload =
                                true
                            println("HUESPED AÑADIDO CON EXITO")

                            if (result is JSONObject) {
                                result.getString("id").let { idGuest ->
                                    if (isFirstGuest) {
                                        val params = HashMap<String, Any>()
                                        val mapGuestGroup: MutableMap<String, Any> = mutableMapOf()
                                        mapGuestGroup.put("leader_id", idGuest)
                                        params["guest_group"] = mapGuestGroup
                                        ChekinNewAPI.updateChekin(
                                            this,
                                            idBooking,
                                            params
                                        ) { result, status ->
                                            if (status) {
                                                println("Huesped añadido como lider de la reserva")
                                            }
                                        }
                                    }
                                }
                            }





                            if (isExtraGuest) {
                                val paramsUpdate = HashMap<String, Any>()
                                bookingInfo.getJSONObject("guest_group").let { gGroup ->
                                    gGroup.getInt("number_of_guests").let { nGuests ->

                                        val mapGuestGroup: MutableMap<String, Any> = mutableMapOf()
                                        mapGuestGroup.put("number_of_guests", nGuests)
                                        paramsUpdate["guest_group"] = mapGuestGroup
                                        ChekinAPI.updateChekin(
                                            this,
                                            bookingInfo.getString("id"),
                                            paramsUpdate
                                        ) { result, status ->
                                            if (status) {

                                            }
                                        }
                                    }
                                }

                            }
                            hideLoadingDialog()
                            var intent =
                                Intent(
                                    this,
                                    NavigationAcitivity::class.java
                                )

                            startActivity(intent)
                            UserProfile.needBookingReload =
                                true

                        } else {
                            hideLoadingDialog()
                            showErrorMessage(2)
                        }
                    }
                } else {
                    ChekinNewAPI.updateGuestRegular(
                        this,
                        guestInfo.getString("id"),
                        params
                    ) { result, status ->
                        if (status) {
                            UserProfile.needBookingDetailsReload =
                                true
                            if (result != null) {
                                UserProfile.bookingDetailsUpdatedID =
                                    idBooking
                            }
                            hideLoadingDialog()
                            finish()
                        } else {
                            hideLoadingDialog()
                            showErrorMessage(2)
                        }
                    }
                }
            }
        }



        buttonAddGuestLater.setOnClickListener {
            if (checkErrors()) {
                showDialogLoading()

                generateParameters()

                ChekinNewAPI.addGuestRegular(
                    this,
                    params
                ) { result, status ->
                    if (status) {
                        UserProfile.needBookingReload =
                            true
                        println("HUESPED AÑADIDO CON EXITO")

                        hideLoadingDialog()

                        var intent = Intent(
                            this,
                            AddGuest::class.java
                        )
                        intent.putExtra("isNationalityProperty", isNationalityProperty)
                        intent.putExtra("noCheckedGuests", noCheckedGuests - 1)
                        intent.putExtra("checkinDate", checkinDate)
                        intent.putExtra("idBooking", idBooking)
                        DocumentReader.Instance().deinitializeReader()
                        finish()
                        startActivity(intent);

                        UserProfile.needBookingReload =
                            true

                    } else {
                        hideLoadingDialog()
                        showErrorMessage(2)
                    }
                }
            }
        }



        buttonAddGuest.isClickable = false
        buttonAddGuest.background.alpha = 128
        buttonAddGuestLater.isClickable = false
        buttonAddGuestLater.background.alpha = 128

        ChekinNewAPI.getDocTypesByCountry(
            this,
            isNationalityProperty
        ) { result, status ->
            if (status) {
                if (result != null) {
                    for (num in 0..result.length() - 1) {
                        val docType = result[num] as JSONObject
                        docType.getJSONObject("type").let { docDict ->
                            docDict.getString("name").let { name ->
                                listDocTypesNames.add(name)
                            }
                            docDict.getString("code").let { code ->
                                listDocTypesIDs.add(code)
                            }

                        }
                    }

                    val adapterDocType = ArrayAdapter(
                        this, R.layout.item_dropdown,
                        listDocTypesNames
                    )
                    documentTypeEditText.dropDownBackground.setColorFilter(
                        Color.WHITE,
                        PorterDuff.Mode.SRC_ATOP
                    )
                    documentTypeEditText.setAdapter(adapterDocType)

                    // triggered when one item in the list is clicked
                    documentTypeEditText.setOnItemClickListener { parent, view, position, id ->
                        docTypeIndex = position
                        documentTypeView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                        documentTypeView.setBackgroundColor(Color.parseColor("#ffffff"))
                        documentTypeEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                        if (checkFields()) {
                            buttonAddGuest.isClickable = true
                            buttonAddGuest.background.alpha = 255
                            if (noCheckedGuests > 1) {
                                buttonAddGuestLater.isClickable = true
                                buttonAddGuestLater.background.alpha = 255
                            } else {
                                buttonAddGuestLater.visibility = View.GONE
                            }
                        } else {
                            buttonAddGuest.isClickable = false
                            buttonAddGuest.background.alpha = 128
                            buttonAddGuestLater.isClickable = false
                            buttonAddGuestLater.background.alpha = 128

                        }


                    }

                }
            }

            if (IS_EDIT_MODE) {

                println("EL VALOR DE GUESTINFO ES: $guestInfo")
                println("Se abre loading desde general")

                setupEditMode()
            }

        }


        if(IS_EDIT_MODE) {
            if (intent.getStringExtra("guestInfo") != null) {
                guestInfo = JSONObject(intent.getStringExtra("guestInfo"))

                bookingInfo.getJSONObject("guest_group").let { gGroup ->
                    gGroup.getString("leader_id").let { leaderID ->
                        if (leaderID == guestInfo.getString("id")) {
                            println("El huesped a editar es el lider del grupo")
                            isLeaderGuest = true
                        }
                    }
                }
            }
            println("EL VALOR DE GUESTINFO ES: $guestInfo")
            showDialogLoading()
            setupEditMode()
        }





    }

    fun setupEditMode() {
        buttonSendChekin.visibility = View.GONE
        buttonAddGuestLater.visibility = View.GONE

        titleAddGuest = findViewById(R.id.titleBarAdPro)
        titleAddGuest.text = getString(R.string.editGuest)
        buttonAddGuest.text = getString(R.string.guardar_cambios)

        guestInfo.getString("name").let {
            name ->
            nameEditText.setText(name)
        }

        guestInfo.getString("surname").let {
            surname ->
            surnameEditText.setText(surname)
        }

        guestInfo.getString("second_surname").let {
                secondsurname ->
            secondSurnameEditText.setText(secondsurname)
        }


        guestInfo.getString("gender").let {
            gender ->
            sex = gender
                if (sex == "M") {
                    sexIndex = 0
                    sexEditText.setText(getString(R.string.masculino))
                } else {
                    sexIndex = 1
                    sexEditText.setText(getString(R.string.femenino))
                }
            sexView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            sexView.setBackgroundColor(Color.parseColor("#ffffff"))
            sexEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        }


        guestInfo.getJSONObject("nationality").let {
            nationality ->
            nationality.getString("code").let {
                code ->
                this.nationality = code
            }
            nationality.getString("name").let {
                    name ->
                nationalityEditText.setText(name)

                nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

                if (isNationalityProperty == "IT") {
                    println("Se abre loading para birth city and exp city")
                    ChekinNewAPI.getLocations(
                        this,
                        "IT",
                        "3"
                    ) { result, status ->
                        if (status) {
                            if (result is JSONObject) {
                                result.optJSONArray("results").let { resultDiv3 ->

                                    for (i in 0..resultDiv3.length() - 1) {
                                        resultDiv3[i].let { resultDiv ->
                                            if (resultDiv is JSONObject) {
                                                resultDiv.optJSONObject("division_level_3")
                                                    .let { div3City ->
                                                        div3City.getString("name").let { name ->
                                                            listItalyCitiesNames.add(
                                                                CountrySearchModel(
                                                                    name,
                                                                    i
                                                                )
                                                            )
                                                        }
                                                        div3City.getString("code").let { code ->
                                                            listItalyCitiesIDs.add(code)
                                                        }
                                                    }
                                            }
                                        }
                                    }

                                    birthCityView =
                                        findViewById(R.id.birthCityAG)
                                    expeditionCityView =
                                        findViewById(R.id.expeditionCityAG)
                                    birthCityEditText =
                                        findViewById(R.id.birthCityAGEditText)
                                    expeditionCityEditText =
                                        findViewById(R.id.expeditionCityAGEditText)

                                    if (birthCountry == "IT") {

                                        if (guestInfo.optJSONObject("birth_place") != null) {
                                            guestInfo.getJSONObject("birth_place").let { birthP ->
                                                birthP.getString("city").let { cityCode ->
                                                    // println("El cityCode de birthplace es: $cityCode")
                                                    birthCity = cityCode
                                                    birthCityEditText.setText(
                                                        listItalyCitiesNames[listItalyCitiesIDs.indexOf(
                                                            cityCode
                                                        )].title
                                                    )
                                                    birthCityView.layoutTransition.enableTransitionType(
                                                        LayoutTransition.CHANGING
                                                    )
                                                    birthCityView.setBackgroundColor(
                                                        Color.parseColor(
                                                            "#ffffff"
                                                        )
                                                    )
                                                    birthCityEditText.setBackgroundResource(
                                                        R.drawable.edittext_bottom_line
                                                    )
                                                }
                                            }
                                        }

                                        birthCityView.visibility = View.VISIBLE
                                        if (isFirstGuest) {
                                            expeditionCityView.visibility = View.VISIBLE
                                        }

                                        val birthCitySearchDialog =
                                            SimpleSearchDialogCompat(this,
                                                getString(R.string.paises),
                                                "",
                                                null,
                                                listItalyCitiesNames,
                                                object :
                                                    SearchResultListener<CountrySearchModel> {
                                                    override fun onSelected(
                                                        dialog: BaseSearchDialogCompat<*>?,
                                                        item: CountrySearchModel?,
                                                        position: Int
                                                    ) {
                                                        birthCityView.layoutTransition.enableTransitionType(
                                                            LayoutTransition.CHANGING
                                                        )
                                                        birthCityView.setBackgroundColor(
                                                            Color.parseColor(
                                                                "#ffffff"
                                                            )
                                                        )
                                                        birthCityEditText.setBackgroundResource(
                                                            R.drawable.edittext_bottom_line
                                                        )

                                                        if (item != null) {
                                                            birthCity =
                                                                listItalyCitiesIDs[item.id]
                                                        }
                                                        birthCityEditText.setText(item?.title)

                                                        if (checkFields()) {
                                                            buttonAddGuest.isClickable = true
                                                            buttonAddGuest.background.alpha =
                                                                255
                                                            if (noCheckedGuests > 1) {
                                                                buttonAddGuestLater.isClickable =
                                                                    true
                                                                buttonAddGuestLater.background.alpha =
                                                                    255
                                                            } else {
                                                                buttonAddGuestLater.visibility =
                                                                    View.GONE
                                                            }
                                                        } else {
                                                            buttonAddGuest.isClickable = false
                                                            buttonAddGuest.background.alpha =
                                                                128
                                                            buttonAddGuestLater.isClickable =
                                                                false
                                                            buttonAddGuestLater.background.alpha =
                                                                128

                                                        }


                                                        dialog?.dismiss();
                                                    }
                                                })


                                        birthCityEditText.setOnClickListener { v ->

                                            birthCitySearchDialog.show()
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                birthCitySearchDialog.titleTextView.setTextColor(
                                                    getColor(R.color.darkBlue)
                                                )
                                            }


                                        }
                                    }


                                    if (expeditionCountry == "IT") {

                                        if (isLeaderGuest) {
                                            println("Se inicializa expeditionCity")
                                            guestInfo.getJSONArray("documents")
                                                .let { documentsArray ->
                                                    documentsArray[0].let { documents ->
                                                        if (documents is JSONObject) {
                                                            if (documents.getJSONObject("issue_place") != null) {
                                                                documents.getJSONObject("issue_place")
                                                                    .let { issuePl ->
                                                                        issuePl.getString("city")
                                                                            .let { cityCode ->
                                                                                expeditionCity =
                                                                                    cityCode
                                                                                expeditionCityView.visibility =
                                                                                    View.VISIBLE

                                                                                if (cityCode != "" && cityCode != null) {
                                                                                    expeditionCityEditText.setText(
                                                                                        listItalyCitiesNames[listItalyCitiesIDs.indexOf(
                                                                                            cityCode
                                                                                        )].title
                                                                                    )
                                                                                }

                                                                                expeditionCityView.layoutTransition.enableTransitionType(
                                                                                    LayoutTransition.CHANGING
                                                                                )
                                                                                expeditionCityView.setBackgroundColor(
                                                                                    Color.parseColor(
                                                                                        "#ffffff"
                                                                                    )
                                                                                )
                                                                                expeditionCityEditText.setBackgroundResource(
                                                                                    R.drawable.edittext_bottom_line
                                                                                )

                                                                                if (checkFields()) {
                                                                                    buttonAddGuest.isClickable =
                                                                                        true
                                                                                    buttonAddGuest.background.alpha =
                                                                                        255
                                                                                    if (noCheckedGuests > 1) {
                                                                                        buttonAddGuestLater.isClickable =
                                                                                            true
                                                                                        buttonAddGuestLater.background.alpha =
                                                                                            255
                                                                                    } else {
                                                                                        buttonAddGuestLater.visibility =
                                                                                            View.GONE
                                                                                    }
                                                                                } else {
                                                                                    buttonAddGuest.isClickable =
                                                                                        false
                                                                                    buttonAddGuest.background.alpha =
                                                                                        128
                                                                                    buttonAddGuestLater.isClickable =
                                                                                        false
                                                                                    buttonAddGuestLater.background.alpha =
                                                                                        128

                                                                                }

                                                                            }

                                                                    }
                                                            }
                                                        }
                                                    }
                                                }
                                        }

                                        val expCitySearchDialog = SimpleSearchDialogCompat(this,
                                            getString(R.string.paises),
                                            "",
                                            null,
                                            listItalyCitiesNames,
                                            object :
                                                SearchResultListener<CountrySearchModel> {
                                                override fun onSelected(
                                                    dialog: BaseSearchDialogCompat<*>?,
                                                    item: CountrySearchModel?,
                                                    position: Int
                                                ) {
                                                    expeditionCityView.layoutTransition.enableTransitionType(
                                                        LayoutTransition.CHANGING
                                                    )
                                                    expeditionCityView.setBackgroundColor(
                                                        Color.parseColor(
                                                            "#ffffff"
                                                        )
                                                    )
                                                    expeditionCityEditText.setBackgroundResource(
                                                        R.drawable.edittext_bottom_line
                                                    )

                                                    if (item != null) {
                                                        expeditionCity = listItalyCitiesIDs[item.id]
                                                    }
                                                    expeditionCityEditText.setText(item?.title)


                                                    if (checkFields()) {
                                                        buttonAddGuest.isClickable = true
                                                        buttonAddGuest.background.alpha = 255
                                                        if (noCheckedGuests > 1) {
                                                            buttonAddGuestLater.isClickable = true
                                                            buttonAddGuestLater.background.alpha =
                                                                255
                                                        } else {
                                                            buttonAddGuestLater.visibility =
                                                                View.GONE
                                                        }
                                                    } else {
                                                        buttonAddGuest.isClickable = false
                                                        buttonAddGuest.background.alpha = 128
                                                        buttonAddGuestLater.isClickable = false
                                                        buttonAddGuestLater.background.alpha = 128

                                                    }

                                                    dialog?.dismiss();
                                                }
                                            })

                                        expeditionCityEditText.setOnClickListener { v ->
                                            expCitySearchDialog.show()
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                expCitySearchDialog.titleTextView.setTextColor(
                                                    getColor(R.color.darkBlue)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (isNationalityProperty == "ES" && this.nationality == "ES") {
                    secondSurnameView.visibility = View.VISIBLE
                }
            }
        }




        guestInfo.getString("birth_date").let {
                birthDate ->
            birthDateEditText.setText(birthDate)
            birthDateView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            birthDateView.setBackgroundColor(Color.parseColor("#ffffff"))
            birthDateEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
        }

        guestInfo.getJSONArray("documents").let {
            documentsArray ->
            documentsArray[0].let { documents ->
                if (documents is JSONObject) {
                    documents.getString("type").let { typeDoc ->
                        var i = 0
                        for (type in listDocTypesIDs) {
                            if (type == typeDoc) {
                                this.docType = typeDoc
                                documentTypeEditText.setText(listDocTypesNames[i])
                                documentTypeView.layoutTransition.enableTransitionType(
                                    LayoutTransition.CHANGING
                                )
                                documentTypeView.setBackgroundColor(Color.parseColor("#ffffff"))
                                documentTypeEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                            }
                            i++
                        }
                    }

                    documents.getString("number").let { docNumber ->
                        documentNumberEditText.setText(docNumber)
                        documentNumberView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                        documentNumberView.setBackgroundColor(Color.parseColor("#ffffff"))
                        documentNumberEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                    }

                    if (isNationalityProperty != "IT" || isNationalityProperty != "PT") {
                        documents.getString("issue_date").let { issueDate ->
                            expeditionDateEditText.setText(issueDate)
                            expeditionDateView.layoutTransition.enableTransitionType(
                                LayoutTransition.CHANGING
                            )
                            expeditionDateView.setBackgroundColor(Color.parseColor("#ffffff"))
                            expeditionDateEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                        }
                    }

                    if ((isLeaderGuest && isNationalityProperty == "IT") || isNationalityProperty == "PT") {
                        expeditionCountryView.visibility = View.VISIBLE
                        println("HACIENDO VISIBLE EXPEDITIONCOUNTRY...")
                        if (documents.getJSONObject("issue_place") != null) {
                            documents.getJSONObject("issue_place").let { issuePl ->
                                println("ENTRAMOS EN ISSUEPLACE...")
                                if (issuePl.optJSONObject("country") != null) {
                                    issuePl.getJSONObject("country").let { issuePlace ->
                                        println("ENTRAMOS EN COUNTRY...")
                                        expeditionCountryEditText.setText(issuePlace.getString("name"))
                                        expeditionCountry = issuePlace.getString("code")
                                        expeditionCountryView.layoutTransition.enableTransitionType(
                                            LayoutTransition.CHANGING
                                        )
                                        expeditionCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                        expeditionCountryEditText.setBackgroundResource(
                                            R.drawable.edittext_bottom_line
                                        )

                                        println("HACEMOS VIEW.VISIBLE EXPEDITIONCOUNTRY")


                                    }
                                }
                            }
                        }
                    }



                    if (isNationalityProperty == "AE") {
                        documentsImageView = findViewById(R.id.imageViewDocuments)
                        documents.getString("front_side_scan").let { frontScan ->
                            Picasso.get().load(frontScan).into(documentsImageView);
                        }
                        documentsScanned = true
                        buttonAddDocuments = findViewById(R.id.buttonGoToAddDocumentsAG)
                        buttonAddDocuments.visibility = View.INVISIBLE
                    }


                }
            }

        }

        bookingInfo.getJSONObject("housing").let { housing ->
            housing.getJSONObject("location").let { location ->
                location.getJSONObject("country").let { country ->
                    country.getString("code").let { code ->

                        if (code == "IT" || code == "PT") {
                            if (guestInfo.optJSONObject("birth_place") != null) {
                                guestInfo.getJSONObject("birth_place").let { birthP ->
                                    birthP.getJSONObject("country").let { birthPlace ->
                                        birthCountryEditText.setText(birthPlace.getString("name"))
                                        birthCountry = birthPlace.getString("code")
                                        birthCountryView.layoutTransition.enableTransitionType(
                                            LayoutTransition.CHANGING
                                        )
                                        birthCountryView.setBackgroundColor(Color.parseColor("#ffffff"))
                                        birthCountryEditText.setBackgroundResource(
                                            R.drawable.edittext_bottom_line
                                        )
                                        birthCountryView.visibility = View.VISIBLE
                                    }


                                }
                            }
                        }



                        if (code == "IT") {
                            housing.getBoolean("is_stat_registration_enabled").let { stat_enabled ->
                                if (stat_enabled) {

                                    if (guestInfo.optJSONObject("residence") != null) {
                                        guestInfo.getJSONObject("residence").let { resid ->
                                            resid.getJSONObject("country").let { residence ->
                                                residenceCountryEditText.setText(
                                                    residence.getString(
                                                        "name"
                                                    )
                                                )
                                                residenceCountry = residence.getString("code")
                                                residenceView.layoutTransition.enableTransitionType(
                                                    LayoutTransition.CHANGING
                                                )
                                                residenceView.setBackgroundColor(Color.parseColor("#ffffff"))
                                                residenceCountryEditText.setBackgroundResource(
                                                    R.drawable.edittext_bottom_line
                                                )

                                                residenceView.visibility = View.VISIBLE

                                                if (residenceCountry == "IT") {
                                                    println("Se abre loading para residence city")
                                                    ChekinNewAPI.getLocations(
                                                        this,
                                                        "IT",
                                                        "3"
                                                    ) { result, status ->
                                                        if (status) {
                                                            if (result is JSONObject) {
                                                                result.optJSONArray("results")
                                                                    .let { resultDiv3 ->

                                                                        for (i in 0..resultDiv3.length() - 1) {
                                                                            resultDiv3[i].let { resultDiv ->
                                                                                if (resultDiv is JSONObject) {
                                                                                    resultDiv.optJSONObject(
                                                                                        "division_level_3"
                                                                                    )
                                                                                        .let { div3City ->
                                                                                            div3City.getString(
                                                                                                "name"
                                                                                            )
                                                                                                .let { name ->
                                                                                                    listItalyCitiesNames.add(
                                                                                                        CountrySearchModel(
                                                                                                            name,
                                                                                                            i
                                                                                                        )
                                                                                                    )
                                                                                                }
                                                                                            div3City.getString(
                                                                                                "code"
                                                                                            )
                                                                                                .let { code ->
                                                                                                    listItalyCitiesIDs.add(
                                                                                                        code
                                                                                                    )
                                                                                                }
                                                                                        }
                                                                                }
                                                                            }
                                                                        }

                                                                        resid.getString("city")
                                                                            .let { cityResidence ->
                                                                                residenceCityEditText.setText(
                                                                                    listItalyCitiesNames[listItalyCitiesIDs.indexOf(
                                                                                        cityResidence
                                                                                    )].title
                                                                                )
                                                                                residenceCity =
                                                                                    cityResidence
                                                                                residenceCityView.layoutTransition.enableTransitionType(
                                                                                    LayoutTransition.CHANGING
                                                                                )
                                                                                residenceCityView.setBackgroundColor(
                                                                                    Color.parseColor(
                                                                                        "#ffffff"
                                                                                    )
                                                                                )
                                                                                residenceCityEditText.setBackgroundResource(
                                                                                    R.drawable.edittext_bottom_line
                                                                                )
                                                                            }

                                                                        residenceCityIsOn = true
                                                                        residenceCityView.visibility =
                                                                            View.VISIBLE
                                                                        val residenCitySearchDialog =
                                                                            SimpleSearchDialogCompat(
                                                                                this,
                                                                                getString(
                                                                                    R.string.paises
                                                                                ),
                                                                                "",
                                                                                null,
                                                                                listItalyCitiesNames,
                                                                                object :
                                                                                    SearchResultListener<CountrySearchModel> {
                                                                                    override fun onSelected(
                                                                                        dialog: BaseSearchDialogCompat<*>?,
                                                                                        item: CountrySearchModel?,
                                                                                        position: Int
                                                                                    ) {
                                                                                        residenceCityView.layoutTransition.enableTransitionType(
                                                                                            LayoutTransition.CHANGING
                                                                                        )
                                                                                        residenceCityView.setBackgroundColor(
                                                                                            Color.parseColor(
                                                                                                "#ffffff"
                                                                                            )
                                                                                        )
                                                                                        residenceCityEditText.setBackgroundResource(
                                                                                            R.drawable.edittext_bottom_line
                                                                                        )

                                                                                        if (item != null) {
                                                                                            residenceCity =
                                                                                                listItalyCitiesIDs[item.id]
                                                                                        }
                                                                                        residenceCityEditText.setText(
                                                                                            item?.title
                                                                                        )
                                                                                        residenceCityIsOn =
                                                                                            true

                                                                                        if (checkFields()) {
                                                                                            buttonAddGuest.isClickable =
                                                                                                true
                                                                                            buttonAddGuest.background.alpha =
                                                                                                255
                                                                                            if (noCheckedGuests > 1) {
                                                                                                buttonAddGuestLater.isClickable =
                                                                                                    true
                                                                                                buttonAddGuestLater.background.alpha =
                                                                                                    255
                                                                                            } else {
                                                                                                buttonAddGuestLater.visibility =
                                                                                                    View.GONE
                                                                                            }
                                                                                        } else {
                                                                                            buttonAddGuest.isClickable =
                                                                                                false
                                                                                            buttonAddGuest.background.alpha =
                                                                                                128
                                                                                            buttonAddGuestLater.isClickable =
                                                                                                false
                                                                                            buttonAddGuestLater.background.alpha =
                                                                                                128

                                                                                        }


                                                                                        dialog?.dismiss();
                                                                                    }
                                                                                })

                                                                        residenceCityEditText.setInputType(
                                                                            0
                                                                        );

                                                                        residenceCityEditText.setOnClickListener { v ->
                                                                            residenCitySearchDialog.show()
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                                residenCitySearchDialog.titleTextView.setTextColor(
                                                                                    getColor(
                                                                                        R.color.darkBlue
                                                                                    )
                                                                                )
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
                                }
                            }
                        } else if (code == "PT" || code == "DE") {
                            if (guestInfo.optJSONObject("residence") != null) {
                                guestInfo.getJSONObject("residence").let { resid ->
                                    resid.getJSONObject("country").let { residence ->
                                        residenceCountryEditText.setText(
                                            residence.getString(
                                                "name"
                                            )
                                        )
                                        residenceCountry = residence.getString("code")
                                        residenceView.layoutTransition.enableTransitionType(
                                            LayoutTransition.CHANGING
                                        )
                                        residenceView.setBackgroundColor(Color.parseColor("#ffffff"))
                                        residenceCountryEditText.setBackgroundResource(
                                            R.drawable.edittext_bottom_line
                                        )

                                        residenceView.visibility = View.VISIBLE

                                        resid.getString("city")
                                            .let { cityResidence ->
                                                if (cityResidence.isNotBlank()) {
                                                    residenceCityEditText.setText(cityResidence)
                                                    residenceCity =
                                                        cityResidence
                                                    residenceCityView.layoutTransition.enableTransitionType(
                                                        LayoutTransition.CHANGING
                                                    )
                                                    residenceCityView.setBackgroundColor(
                                                        Color.parseColor(
                                                            "#ffffff"
                                                        )
                                                    )
                                                    residenceCityEditText.setBackgroundResource(
                                                        R.drawable.edittext_bottom_line
                                                    )
                                                }
                                            }

                                        if (isNationalityProperty == "DE") {
                                            resid.getString("address")
                                                .let { addressRes ->
                                                    if (addressRes.isNotBlank()) {
                                                        residenceAddressEditText.setText(addressRes)

                                                        residenceAddressView.layoutTransition.enableTransitionType(
                                                            LayoutTransition.CHANGING
                                                        )
                                                        residenceAddressView.setBackgroundColor(
                                                            Color.parseColor(
                                                                "#ffffff"
                                                            )
                                                        )
                                                        residenceAddressEditText.setBackgroundResource(
                                                            R.drawable.edittext_bottom_line
                                                        )
                                                    }
                                                }
                                        }

                                        residenceCityIsOn = true
                                        residenceCityView.visibility =
                                            View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        guestInfo.getString("signature").let {
            signature ->

            println("EL ENLACE A SIGNATURE ES: $signature")
            signatureImageView = findViewById(R.id.imageViewSignature)
            Picasso.get().load(signature).into(signatureImageView);


            ExtraButtonAddSign = findViewById(R.id.extrabuttonaddSign)
            ExtraButtonAddSign.visibility = View.INVISIBLE
        }




        if(checkFields()) {
            buttonAddGuest.isClickable = true
            buttonAddGuest.background.alpha = 255
            if (noCheckedGuests > 1) {
                buttonAddGuestLater.isClickable = true
                buttonAddGuestLater.background.alpha = 255
            } else {
                buttonAddGuestLater.visibility = View.GONE
            }
        } else {
            buttonAddGuest.isClickable = false
            buttonAddGuest.background.alpha = 128
            buttonAddGuestLater.isClickable = false
            buttonAddGuestLater.background.alpha = 128

        }

        println("Se cierra loading desde general")

        Timer().schedule(object : TimerTask() {
            override fun run() {
                hideLoadingDialog()
                hideLoadingDialog()
            }
        }, 3000)








    }

    fun generateParameters() {

        if (!IS_EDIT_MODE) {
            params["reservation_id"] = idBooking
        }
        params["name"] = nameEditText.text.toString()
        params["surname"] = surnameEditText.text.toString()
        params["birth_date"] = birthDateEditText.text.toString()
        params["nationality"] = nationality
        params["gender"] = sex

        if (newSignatureAdded) {
            params["signature"] = signature
        }

        val mapDocuments : MutableMap<String,Any> = mutableMapOf()
        if (isNationalityProperty == "AE") {
            mapDocuments.put("type", "P")
            mapDocuments.put("number", documentNumberEditText.text.toString())
            mapDocuments.put("issue_date", expeditionDateEditText.text.toString())
            if (isIDScanned) {
                mapDocuments.put("front_side_scan",
                    UserProfile.IDFrontDocumentScanned
                )
                mapDocuments.put("back_side_scan",
                    UserProfile.IDBackDocumentScanned
                )
                params["document"] = mapDocuments
            } else {
                mapDocuments.put("front_side_scan",
                    UserProfile.PassportDocumentScanned
                )
                params["document"] = mapDocuments
            }
        } else if (isNationalityProperty == "IT"){

            if (isFirstGuest || isLeaderGuest) {
                mapDocuments.put("type", docType)
                mapDocuments.put("number", documentNumberEditText.text.toString())
                val locationIssuePlace : MutableMap<String,Any> = mutableMapOf()
                println("El parametro expeditionCountry es: $expeditionCountry")
                locationIssuePlace.put("country", expeditionCountry)
                mapDocuments.put("issue_place", locationIssuePlace)
                params["document"] = mapDocuments

                if(expeditionCountry == "IT") {
                    if (isFirstGuest || isLeaderGuest) {
                        locationIssuePlace.put("city", expeditionCity)
                        mapDocuments.put("issue_place", locationIssuePlace)
                        params["document"] = mapDocuments
                    }
                }
            }


            val locationBirthPlace : MutableMap<String,Any> = mutableMapOf()
            locationBirthPlace.put("country", birthCountry)
            params["birth_place"] = locationBirthPlace

            if (isISTATOn) {
                val locationResidence : MutableMap<String,Any> = mutableMapOf()
                locationResidence.put("country", residenceCountry)
                params["residence"] = locationResidence

                if (residenceCountry == "IT") {
                    locationResidence.put("city", residenceCity)
                     params["residence"] = locationResidence
                }
            }

            if (birthCountry == "IT") {
                locationBirthPlace.put("city", birthCity)
                params["birth_place"] = locationBirthPlace
            }



        } else if (isNationalityProperty == "ES") {
            mapDocuments.put("type", docType)
            mapDocuments.put("number", documentNumberEditText.text.toString())
            mapDocuments.put("issue_date", expeditionDateEditText.text.toString())
            params["document"] = mapDocuments

            if (nationality == "ES") {
                params["second_surname"] = secondSurnameEditText.text.toString()
            }

        } else if (isNationalityProperty == "PT") {
            mapDocuments.put("type", docType)
            mapDocuments.put("number", documentNumberEditText.text.toString())
            val locationIssuePlace : MutableMap<String,Any> = mutableMapOf()
            locationIssuePlace.put("country", expeditionCountry)
            mapDocuments.put("issue_place", locationIssuePlace)
            params["document"] = mapDocuments

            val locationBirthPlace : MutableMap<String,Any> = mutableMapOf()
            locationBirthPlace.put("country", birthCountry)
            params["birth_place"] = locationBirthPlace

            val locationResidence : MutableMap<String,Any> = mutableMapOf()
            locationResidence.put("country", residenceCountry)
            if (residenceCityEditText.text.isNotBlank()) {
                locationResidence.put("city", residenceCityEditText.text.toString())
            }
            params["residence"] = locationResidence

        } else if (isNationalityProperty == "DE") {
            mapDocuments.put("type", docType)
            mapDocuments.put("number", documentNumberEditText.text.toString())
            params["document"] = mapDocuments


            val locationResidence: MutableMap<String, Any> = mutableMapOf()
            locationResidence.put("country", residenceCountry)
            if (residenceCityEditText.text.isNotBlank()) {
                locationResidence.put("city", residenceCityEditText.text.toString())
            }
            if (residenceAddressEditText.text.isNotBlank()) {
                locationResidence.put("address", residenceAddressEditText.text.toString())
            }
            params["residence"] = locationResidence
        }



    }

    fun openScanner() {
        println("ENTRAMOS EN OPENSCANNER")
        if (!DocumentReader.Instance().documentReaderIsReady) {
            val initDialog = showDialog("Initializing")
            println("Se inicializa el escaner")

            //Reading the license from raw resource file
            try {
                val licInput = resources.openRawResource(R.raw.regula)
                val available = licInput.available()
                val license = ByteArray(available)

                licInput.read(license)
                //preparing database files, it will be downloaded from network only one time and stored on user device
                DocumentReader.Instance().prepareDatabase(
                    this,
                    "Full",
                    object : DocumentReader.DocumentReaderPrepareCompletion {
                        override fun onPrepareProgressChanged(progress: Int) {
                            println("Downloading database: $progress%")
                        }

                        override fun onPrepareCompleted(status: Boolean, error: String) {

                            //Initializing the reader
                            DocumentReader.Instance().initializeReader(
                                this@AddGuest, license
                            ) { success, error_initializeReader ->
                                if (initDialog.isShowing) {
                                    initDialog.dismiss()
                                }

                                DocumentReader.Instance().customization().isShowResultStatusMessages = true
                                DocumentReader.Instance().customization().isShowStatusMessages = true
                                DocumentReader.Instance().functionality().isVideoCaptureMotionControl = true

                                DocumentReader.Instance().functionality().setCameraFrame(DocReaderFrame.MAX)

                                //initialization successful
                                if (success) {
                                    buttonScan!!.setOnClickListener {

                                        println("Se pulsa el boton de escaneo")

                                        //starting video processing
                                        DocumentReader.Instance().showScanner(completion)
                                    }


                                    //getting current processing scenario and loading available scenarios to ListView
                                    var currentScenario: String? = DocumentReader.Instance().processParams().scenario
                                    val scenarios = java.util.ArrayList<String>()
                                    for (scenario in DocumentReader.Instance().availableScenarios) {
                                        scenarios.add(scenario.name)
                                    }

                                    //setting default scenario
                                    if (currentScenario == null || currentScenario.isEmpty()) {
                                        currentScenario = scenarios[0]
                                        DocumentReader.Instance().processParams().scenario = currentScenario
                                    }



                                } else {
                                    Toast.makeText(
                                        this@AddGuest,
                                        "Init failed:$error_initializeReader",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }//Initialization was not successful
                            }
                        }
                    })

                licInput.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }




    fun showDialog(msg: String): AlertDialog {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, scrollView3, false)
        dialog.setTitle(msg)
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        return dialog.show()
    }

    fun getCountryName(national: String): String {
        var i = 0
        for (isoNation in listCountriesCode){
            i++
            if(isoNation == national) {
                println("La isoNation es: $isoNation y la nacionalidad es: $nationality")
                return listCountriesName[i-1].title
            }
        }
        return ""
    }

    //show received results on the UI
    private fun displayResults(results: DocumentReaderResults?) {
        if (results != null) {
            val name = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES)
            if (name != null) {
                var ArrName = name.split(" ")
                nameEditText.setText(capitalizeFirstLetter(ArrName[0].toLowerCase()))
                println("El nombre ${ArrName[0]} parseado: ${capitalizeFirstLetter(ArrName[0].toLowerCase())}")
                for(i in 1..ArrName.count()-1) {
                    nameEditText.setText("${nameEditText.text} ${capitalizeFirstLetter(ArrName[i].toLowerCase())}")
                }

                println("Nombre es: ${nameEditText.text}")
                nameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nameView.setBackgroundColor(Color.parseColor("#ffffff"))
                nameEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }


            val sex = results.getTextFieldValueByType(eVisualFieldType.FT_SEX)
            if (sex != null) {
                if (sex == "M") {
                    sexEditText.setText("Masculino")
                    sexIndex = 0
                } else {
                    sexEditText.setText("Femenino")
                    sexIndex = 1
                }
                sexView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                sexView.setBackgroundColor(Color.parseColor("#ffffff"))
                sexEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }

            val nation = results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY_CODE)
            println("La nacion escaneada es: -$nation-")
            if (nation != null) {
                if (nation != null && getCountryName(nation) != "") {

                    if (nation.length > 1) {
                        nationality = nation.substring(0, 2)
                    } else if (nation == "D") {
                        nationality = "DE"
                    }

                    if (nation == "CHN") {
                        nationality = "CN"
                    } else if (nation == "PRT") {
                        nationality = "PT"
                    }

                    println("La nacionalidad guardada es: $nationality")

                    nationalityEditText.setText(getCountryName(nationality))
                    nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                    nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                } else if (nation == "D<<" || nation == "D<" || nation == "D") {
                    nationality = "DE"
                    nationalityEditText.setText(getCountryName(nationality))
                    nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                    nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                } else {
                    val MRZCode = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS_WITH_CORRECT_CHECK_SUMS)
                    println("NACIONALIDAD NO DETECTADA Y EL MRZ CODE ES: $MRZCode")
                    println("La nacionalidad podria ser: ${MRZCode.substring(2, 4)}")


                    nationality = MRZCode.substring(2, 4)

                    if (nation == "KOR") {
                        nationality = "KR"
                        println("Nacionalidad cambiada de KOR a KR")
                    }

                    nationalityEditText.setText(getCountryName(nationality))
                    nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                    nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                    nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                }
            } else {
                val MRZCode = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS_WITH_CORRECT_CHECK_SUMS)
                println("NACIONALIDAD NO DETECTADA Y EL MRZ CODE ES: $MRZCode")
                println("La nacionalidad podria ser: ${MRZCode.substring(2, 4)}")

                nationality = MRZCode.substring(2, 4)
                nationalityEditText.setText(getCountryName(nationality))
                nationalityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nationalityView.setBackgroundColor(Color.parseColor("#ffffff"))
                nationalityEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }

            if (nationality == "ES" && isNationalityProperty == "ES") {
                secondSurnameView.visibility = View.VISIBLE
            } else {
                secondSurnameView.visibility = View.GONE
            }

            val surname = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME)
            if (surname != null) {
                var ArrSurname = surname.split(" ")
                surnameEditText.setText(capitalizeFirstLetter(capitalizeFirstLetter(ArrSurname[0].toLowerCase())))
                if (nationality == "ES" && isNationalityProperty == "ES") {
                    for (i in 1..ArrSurname.count() - 1) {
                        secondSurnameEditText.setText(
                            "${capitalizeFirstLetter(
                                ArrSurname[i].toLowerCase()
                            )}"
                        )
                    }
                }
                println("Apellido es: ${surnameEditText.text}")
                surnameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                surnameView.setBackgroundColor(Color.parseColor("#ffffff"))
                surnameEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }




            val birthDate = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH)
            println("La fecha antes de ser parseada es: $birthDate")
            if (birthDate != null) {

                try {
                    val sdf = SimpleDateFormat("dd/MM/yy")
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                    var date = sdf2.format(sdf.parse(birthDate))
                    birthDateEditText.setText(date.replace("/", "-"))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }



                birthDateView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                birthDateView.setBackgroundColor(Color.parseColor("#ffffff"))
                birthDateEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }

            val docT = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_CLASS_CODE)
            if (docT != null) {
                if ( docT == "ID") {
                    if (isNationalityProperty == "ES") {
                        if (nationality == "ES") {
                            documentTypeEditText.setText(listDocTypesNames[4])
                            docTypeIndex = 4
                        } else {
                            documentTypeEditText.setText(listDocTypesNames[1])
                            docTypeIndex = 1
                        }
                    } else if (isNationalityProperty == "PT") {
                        documentTypeEditText.setText(listDocTypesNames[0])
                        docTypeIndex = 0
                    } else if (isNationalityProperty == "IT") {
                        documentTypeEditText.setText(listDocTypesNames[1])
                        docTypeIndex = 1
                    }
                }else if (docT == "D" || docT == "I" || docT == "IO") {
                    if (isNationalityProperty == "ES") {
                        documentTypeEditText.setText(listDocTypesNames[1])
                        docTypeIndex = 1
                    } else if (isNationalityProperty == "PT") {
                        documentTypeEditText.setText(listDocTypesNames[0])
                        docTypeIndex = 0
                    }
                    else if(isNationalityProperty == "IT") {
                        documentTypeEditText.setText(listDocTypesNames[1])
                        docTypeIndex = 1
                    }
                } else if (docT == "P"  || docT == "PO" || docT == "P0" || docT == "PM" || docT == "PT") {
                    if (isNationalityProperty == "ES") {
                        documentTypeEditText.setText(listDocTypesNames[0])
                        docTypeIndex = 0
                    } else if(isNationalityProperty == "IT") {
                        documentTypeEditText.setText(listDocTypesNames[0])
                        docTypeIndex = 0
                    } else if (isNationalityProperty == "PT") {
                        documentTypeEditText.setText(listDocTypesNames[1])
                        docTypeIndex = 1
                    }
                } else if (docT == "C") {
                    if(isNationalityProperty == "IT") {
                        documentTypeEditText.setText(listDocTypesNames[6])
                        docTypeIndex = 6
                    } else if (isNationalityProperty == "ES") {
                        if (nationality == "ES") {
                            documentTypeEditText.setText(listDocTypesNames[5])
                            docTypeIndex = 5
                        } else {
                            documentTypeEditText.setText(listDocTypesNames[3])
                            docTypeIndex = 3
                        }
                    } else if (isNationalityProperty == "PT") {
                        documentTypeEditText.setText(listDocTypesNames[2])
                        docTypeIndex = 2
                    }
                } else if (docT == "IX") {
                    if (isNationalityProperty == "ES") {
                        documentTypeEditText.setText(listDocTypesNames[2])
                        docTypeIndex = 2
                    } else if (isNationalityProperty == "PT") {
                        documentTypeEditText.setText(listDocTypesNames[2])
                        docTypeIndex = 2
                    } else if (isNationalityProperty == "IT") {
                        documentTypeEditText.setText(listDocTypesNames[3])
                        docTypeIndex = 3
                    }
                }
                documentTypeView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                documentTypeView.setBackgroundColor(Color.parseColor("#ffffff"))
                documentTypeEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }

            if(docT == "ID" && nationality == "ES") {
                val docPN = results.getTextFieldValueByType(eVisualFieldType.FT_PERSONAL_NUMBER)
                if (docPN != null) {
                    documentNumberEditText.setText(docPN)
                }
            } else {
                val docN = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER)
                if (docN != null) {
                    documentNumberEditText.setText(docN)
                }

            }
            documentNumberView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            documentNumberView.setBackgroundColor(Color.parseColor("#ffffff"))
            documentNumberEditText.setBackgroundResource(R.drawable.edittext_bottom_line)

            val expDate = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY)
            if (expDate != null) {
                try {
                    val sdf = SimpleDateFormat("dd/MM/yy")
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd")

                    var date = sdf2.format(sdf.parse(expDate))
                    var datePlus1 = date.split('-').toMutableList();
                    datePlus1[0] = ((datePlus1[0]).toInt() - 10).toString()
                    date = TextUtils.join("-", datePlus1);
                    expeditionDateEditText.setText(date.replace("/", "-"))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }


                expeditionDateView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                expeditionDateView.setBackgroundColor(Color.parseColor("#ffffff"))
                expeditionDateEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
            }



            if(checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }



            // through all text fields
            if (results.textResult != null && results.textResult.fields != null) {
                for (textField in results.textResult.fields) {
                    val value = results.getTextFieldValueByType(textField.fieldType, textField.lcid)
                    Log.d("MainActivity", value + "\n")
                }
            }


        }
    }

    private fun updateBirthDate() {
        val myFormat = "yyyy/MM/dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        birthDateEditText.setText((sdf.format(myCalendar.time)).replace("/", "-"))
        birthDateView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        birthDateView.setBackgroundColor(Color.parseColor("#ffffff"))
        birthDateView.setBackgroundResource(R.drawable.edittext_bottom_line)

        if(checkFields()) {
            buttonAddGuest.isClickable = true
            buttonAddGuest.background.alpha = 255
            if (noCheckedGuests > 1) {
                buttonAddGuestLater.isClickable = true
                buttonAddGuestLater.background.alpha = 255
            } else {
                buttonAddGuestLater.visibility = View.GONE
            }
        } else {
            buttonAddGuest.isClickable = false
            buttonAddGuest.background.alpha = 128
            buttonAddGuestLater.isClickable = false
            buttonAddGuestLater.background.alpha = 128

        }
    }

    private fun updateExpDate() {
        val myFormat = "yyyy/MM/dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        expeditionDateEditText.setText((sdf.format(myCalendar.time)).replace("/", "-"))
        expeditionDateView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        expeditionDateView.setBackgroundColor(Color.parseColor("#ffffff"))
        expeditionDateView.setBackgroundResource(R.drawable.edittext_bottom_line)

        if(checkFields()) {
            buttonAddGuest.isClickable = true
            buttonAddGuest.background.alpha = 255
            if (noCheckedGuests > 1) {
                buttonAddGuestLater.isClickable = true
                buttonAddGuestLater.background.alpha = 255
            } else {
                buttonAddGuestLater.visibility = View.GONE
            }
        } else {
            buttonAddGuest.isClickable = false
            buttonAddGuest.background.alpha = 128
            buttonAddGuestLater.isClickable = false
            buttonAddGuestLater.background.alpha = 128

        }
    }



    fun getCountryCode(countryName: String): String {
        val code = Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
        return code.toString()
    }

    fun checkFields():Boolean {

        if (IS_EDIT_MODE == false) {
            if (isNationalityProperty == "ES") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentTypeEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    expeditionDateEditText.text.isBlank() ||
                    signature == ""
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

                if (isNationalityProperty == "ES" && nationality == "ES") {
                    if (secondSurnameEditText.text.isBlank()) {
                        return false
                    }
                }
            } else if (isNationalityProperty == "IT") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    birthCountryEditText.text.isBlank()

                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

                if (isFirstGuest) {
                    if (expeditionCountryEditText.text.isBlank() || documentTypeEditText.text.isBlank() ||
                        documentNumberEditText.text.isBlank()
                    ) {
                        println("CHECKFIELDS DOCUMENTS - Algun campo del formulario esta vacio.")
                        return false
                    }
                }

                if (isISTATOn) {
                    if (residenceCountryEditText.text.isEmpty()) {
                        println("CHECKFIELDS RESIDENCE COUNTRY - Algun campo del formulario esta vacio.")
                        return false
                    }
                }

                if (residenceCountry == "IT") {
                    if (residenceCityEditText.text.isEmpty()) {
                        println("CHECKFIELDS RESIDENCE CITY - Algun campo del formulario esta vacio.")
                        return false
                    }
                }

                if (birthCountry == "IT") {
                    if (birthCityEditText.text.isEmpty()) {
                        println("CHECKFIELDS BIRTH CITY - Algun campo del formulario esta vacio.")
                        return false
                    }
                }

                if (expeditionCountry == "IT") {
                    if (expeditionCityEditText.text.isEmpty()) {
                        println("CHECKFIELDS EXPEDITION CITY - Algun campo del formulario esta vacio.")
                        return false
                    }
                }
            } else if (isNationalityProperty == "PT") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentTypeEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    residenceCountryEditText.text.isBlank() ||
                    expeditionCountryEditText.text.isBlank() ||
                    birthCountryEditText.text.isBlank() ||
                    signature == ""
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

            } else if (isNationalityProperty == "DE") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentTypeEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    residenceCountryEditText.text.isBlank() ||
                    residenceCityEditText.text.isBlank() ||
                    signature == ""
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

            } else if (isNationalityProperty == "AE") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    expeditionDateEditText.text.isBlank() ||
                    signature == "" ||
                    !documentsScanned
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }
            }
        } else {
            if (isNationalityProperty == "ES") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentTypeEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    expeditionDateEditText.text.isBlank()
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

                if (isNationalityProperty == "ES" && nationality == "ES") {
                    if (secondSurnameEditText.text.isBlank()) {
                        return false
                    }
                }
            } else if (isNationalityProperty == "IT") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    birthCountryEditText.text.isBlank()
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }

                if (isLeaderGuest) {
                    if (expeditionCountryEditText.text.isBlank() || documentTypeEditText.text.isBlank() ||
                        documentNumberEditText.text.isBlank()
                    ) {
                        return false
                    }
                }

                if (isISTATOn) {
                    if (residenceCountryEditText.text.isEmpty()) {
                        return false
                    }
                }

                if (residenceCountry == "IT") {
                    if (residenceCityEditText.text.isEmpty()) {
                        return false
                    }
                }

                if (birthCountry == "IT") {
                    if (birthCityEditText.text.isEmpty()) {
                        return false
                    }
                }

                if (expeditionCountry == "IT") {
                    if (expeditionCityEditText.text.isEmpty()) {
                        return false
                    }
                }
            } else if (isNationalityProperty == "PT") {
                    if (nameEditText.text.isBlank() ||
                        surnameEditText.text.isBlank() ||
                        sexEditText.text.isBlank() ||
                        nationalityEditText.text.isBlank() ||
                        birthDateEditText.text.isBlank() ||
                        documentTypeEditText.text.isBlank() ||
                        documentNumberEditText.text.isBlank() ||
                        residenceCountryEditText.text.isBlank() ||
                        expeditionCountryEditText.text.isBlank() ||
                        birthCountryEditText.text.isBlank() ||
                        signature == ""
                    ) {
                        println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                        return false
                    }

            } else if (isNationalityProperty == "AE") {
                if (nameEditText.text.isBlank() ||
                    surnameEditText.text.isBlank() ||
                    sexEditText.text.isBlank() ||
                    nationalityEditText.text.isBlank() ||
                    birthDateEditText.text.isBlank() ||
                    documentNumberEditText.text.isBlank() ||
                    expeditionDateEditText.text.isBlank() ||
                    !documentsScanned
                ) {
                    println("CHECKFIELDS - Algun campo del formulario esta vacio.")
                    return false
                }
            }
        }



        if (sexIndex == 0) {
            sex = "M"
        } else {
            sex = "F"
        }

        if (listDocTypesIDs.size > 0) {
            docType = listDocTypesIDs[docTypeIndex]
        }

        return true
    }


    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == nameEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                nameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                nameView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == surnameEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                surnameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                surnameView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == secondSurnameEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                secondSurnameView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                secondSurnameView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == documentNumberEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                documentNumberView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                documentNumberView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == residenceCityEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                residenceCityView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                residenceCityView.setBackgroundColor(Color.parseColor("#ffffff"))
            } else if (mEditText == residenceAddressEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                residenceAddressView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                residenceAddressView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }
        }

        override fun afterTextChanged(s: Editable) {

        }

    }

    fun capitalizeFirstLetter(original: String?): String? {
        return if (original == null || original.length == 0) {
            original
        } else original.substring(0, 1).toUpperCase() + original.substring(1)
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }

    fun hide(activity: Activity){
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm is InputMethodManager) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        }
    }


    fun showDialogSignature(typeError: Int) {

        MyDialog = Dialog(this)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.signature_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonAddSignature = MyDialog.findViewById(R.id.buttonAddSignature) as Button
        buttonAddSignature.setEnabled(true)
        buttonCancelSignature = MyDialog.findViewById(R.id.buttonCancelSignature)
        signatureField = MyDialog.findViewById(R.id.signature_view)

        MyDialog.show()
        buttonCancelSignature.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })

        buttonAddSignature.setOnClickListener(View.OnClickListener {
            val signatureImage = signatureField.signatureBitmap
            val baos = ByteArrayOutputStream()
            signatureImage.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            Base64Image.instance.encode(signatureImage) { base64 ->
                base64?.let {
                    signBase64 ->
                    println("Se añade la firma en base64 al string Signature")
                    //if (IS_EDIT_MODE) {
                        newSignatureAdded = true
                    //}
                    signature = signBase64


                    if(checkFields()) {
                        buttonAddGuest.isClickable = true
                        buttonAddGuest.background.alpha = 255
                        if (noCheckedGuests > 1) {
                            buttonAddGuestLater.isClickable = true
                            buttonAddGuestLater.background.alpha = 255
                        } else {
                            buttonAddGuestLater.visibility = View.GONE
                        }
                    } else {
                        buttonAddGuest.isClickable = false
                        buttonAddGuest.background.alpha = 128
                        buttonAddGuestLater.isClickable = false
                        buttonAddGuestLater.background.alpha = 128

                    }
                    // success
                }
            }



            val dr = BitmapDrawable(signatureImage);
            signatureView.setBackgroundDrawable(dr);

            ExtraButtonAddSign = findViewById(R.id.extrabuttonaddSign)
            ExtraButtonAddSign.visibility = View.INVISIBLE

            if(checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }


            MyDialog.cancel()

        })

    }

    fun checkErrors(): Boolean {
        if (nationality == "ESP") {
            val surn = surnameEditText.text.split(" ")
            if (surn.count() == 1) {
                showErrorMessage(0)
                return false
            }
        }

        if (nationality == "ESP" && docType == "D") {
            val validatorDNI =
                ValidatorDNI(
                    documentNumberEditText.text.toString()
                )
            if (!validatorDNI.validar()) {
                showErrorMessage(1)
                return false
            }
        }



        return true
    }

    fun showDialogLoading() {

            MyDialog = Dialog(this)
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

    fun showErrorMessage(typeError: Int = 0) {

        MyDialog = Dialog(this)
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        MyDialog.setContentView(R.layout.custom_error_dialog)
        MyDialog.getWindow()!!.getAttributes().windowAnimations =
            R.style.MyAnimation_Window
        MyDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        buttonCorrectError = MyDialog.findViewById(R.id.buttonErrorDialog) as Button


        errorMessage = MyDialog.findViewById(R.id.messageErrorDialog)


        if (typeError == 0) {
            errorMessage.setText("Por favor, inserte sus dos apellidos.")
        } else if (typeError == 1) {
            errorMessage.setText("El DNI introducido no es valido.")
        } else {
            errorMessage.setText("Se ha producido un error en el servidor.")
            buttonCorrectError.setText("VOLVER")
        }



        MyDialog.show()
        buttonCorrectError.setOnClickListener(View.OnClickListener {
            MyDialog.cancel()

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode != 0) {
            println("Entramos en onActivityResult desde ADD GUEST, con resultCode: $resultCode")
            if (UserProfile.auxIsID) {
                UserProfile.auxIsID = false
                UserProfile.auxSecondScanned = false
                UserProfile.auxfirstScanned = false
                println("El resultCode es 100 y es un document ID")
                super.onActivityResult(requestCode, resultCode, intent)
                isIDScanned = true
                val dr = BitmapDrawable(UserProfile.IDimage);
                addDocumentView.setBackgroundDrawable(dr);


            } else {
                println("El resultCode es 101 y es un pasaporte")
                isPassportScanned = true
                val dr = BitmapDrawable(UserProfile.IDimage);
                addDocumentView.setBackgroundDrawable(dr);
            }


            documentsScanned = true
            if (checkFields()) {
                buttonAddGuest.isClickable = true
                buttonAddGuest.background.alpha = 255
                if (noCheckedGuests > 1) {
                    buttonAddGuestLater.isClickable = true
                    buttonAddGuestLater.background.alpha = 255
                } else {
                    buttonAddGuestLater.visibility = View.GONE
                }
            } else {
                buttonAddGuest.isClickable = false
                buttonAddGuest.background.alpha = 128
                buttonAddGuestLater.isClickable = false
                buttonAddGuestLater.background.alpha = 128

            }
        }
    }


    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

}
