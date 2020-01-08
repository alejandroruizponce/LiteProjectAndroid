package com.chekinlite.app.OldVersionFiles

import android.animation.LayoutTransition
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
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.R
import com.reginald.editspinner.EditSpinner
import kotlinx.android.synthetic.main.activity_suscription.typeSuscriptionView
import org.json.JSONObject


class SuscriptionActivity : AppCompatActivity() {

    var priceMonthlyHouse: Double = 0.0
    var priceYearlyHouse: Double = 0.0
    var priceMonthlyHotel: Double = 0.0
    var priceYearlyHotel: Double = 0.0

    lateinit var numberHousesEditText: EditText
    lateinit var numberHousesView: RelativeLayout
    lateinit var mEditSpinner: EditSpinner
    lateinit var numberHouses: String
    var indexSuscription: Int = 0
    lateinit var buttonStart: Button
    lateinit var pricePlanTextView: TextView
    lateinit var subPricePlanTextView: TextView

    lateinit var buttonHousePlan: ImageButton
    lateinit var buttonHotelPlan: ImageButton

    lateinit var planSelected: String

    var housePlanIsOn = false
    var hotelPlanIsOn = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscription)



        ChekinAPI.getPlansSuscription(this) {
            result, status ->
            if (status) {
                val housePlan = result?.get(0) as? JSONObject
                val hotelPlan = result?.get(1) as? JSONObject

                priceMonthlyHouse = housePlan?.get("monthly_unitary_cost") as Double
                priceYearlyHouse = housePlan?.get("yearly_unitary_cost") as Double

                priceMonthlyHotel = hotelPlan?.get("monthly_unitary_cost") as Double
                priceYearlyHotel = hotelPlan?.get("yearly_unitary_cost") as Double

                println("El precio de casa mensual es $priceMonthlyHouse y el precio de hotel anual es: $priceYearlyHotel")
            }
        }

        buttonHousePlan = findViewById(R.id.buttonHousePlan)
        buttonHousePlan.setOnClickListener {
            buttonHousePlan.setImageResource(R.drawable.house_blue)
            housePlanIsOn = true
            if (hotelPlanIsOn) {
                hotelPlanIsOn = false
                buttonHotelPlan.setImageResource(R.drawable.hotel)
            }
            checkFields()
        }
        buttonHotelPlan = findViewById(R.id.buttonHotelPlan)
        buttonHotelPlan.setOnClickListener {
            buttonHotelPlan.setImageResource(R.drawable.hotel_blue)
            hotelPlanIsOn = true
            if (housePlanIsOn) {
                housePlanIsOn = false
                buttonHousePlan.setImageResource(R.drawable.house)
            }
            checkFields()
        }

        pricePlanTextView = findViewById(R.id.pricePlanTextView)
        subPricePlanTextView = findViewById(R.id.subPriceTextView)

        numberHousesEditText = findViewById(R.id.numberHousesEditText)
        numberHousesView = findViewById(R.id.numberHousesView)
        numberHousesEditText.addTextChangedListener(CustomTextWatcher(numberHousesEditText))

        buttonStart = findViewById(R.id.buttonStartTrial)
        buttonStart.isClickable = false
        buttonStart.getBackground().setAlpha(128);
        buttonStart.setOnClickListener {
            ChekinAPI.createSuscription(this, planSelected, numberHouses.toInt()) {
                result, status ->
                if (status) {

                    val intent = Intent(this, NavigationAcitivity::class.java)
                    // start your next activity
                    startActivity(intent)
                }
            }
        }

        mEditSpinner = findViewById<View>(R.id.typeSuscriptionEditText) as EditSpinner
        val adapter = ArrayAdapter(
            this, R.layout.item_dropdown,
            resources.getStringArray(R.array.typeSuscription)

        )
        mEditSpinner.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        mEditSpinner.setAdapter(adapter)

        // triggered when one item in the list is clicked
        mEditSpinner.setOnItemClickListener { parent, view, position, id ->
            indexSuscription = position
            typeSuscriptionView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            typeSuscriptionView.setBackgroundColor(Color.parseColor("#ffffff"))
            checkFields()
        }

        numberHousesEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        mEditSpinner.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


    }

    fun checkFields() {
        val nHouses = numberHousesEditText.text.toString();
        val tSus = mEditSpinner.text.toString()

        if (nHouses.isNotBlank() && tSus.isNotBlank() && (housePlanIsOn || hotelPlanIsOn)) {
            numberHouses = nHouses
            if (indexSuscription == 0) {
                if (housePlanIsOn) {
                    planSelected = "basic-monthly-plan"
                    pricePlanTextView.text = String.format("%.2f", (nHouses.toInt() * priceMonthlyHouse)) + "€"
                } else {
                    planSelected = "hotel-monthly-plan"
                    pricePlanTextView.text = String.format("%.2f", (nHouses.toInt() * priceMonthlyHotel)) + "€"
                }
                var monthPriceText = getString(R.string.monthTextPrice)
                var monthPriceText2 = getString(R.string.propertiesPrice)
                subPricePlanTextView.text = "$monthPriceText $nHouses $monthPriceText2"
            } else if (indexSuscription == 1) {
                if (housePlanIsOn) {
                    planSelected = "basic-yearly-plan"
                    pricePlanTextView.text = String.format("%.2f",  (nHouses.toInt() * priceYearlyHouse)) + "€"
                } else {
                    planSelected = "hotel-yearly-plan"
                    pricePlanTextView.text = String.format("%.2f", (nHouses.toInt() * priceYearlyHotel)) + "€"
                }
                var yearPriceText = getString(R.string.yearTextPrice)
                var yearPriceText2 = getString(R.string.propertiesPrice)
                subPricePlanTextView.text = "$yearPriceText $nHouses $yearPriceText2"
            }


            buttonStart.isClickable = true
            buttonStart.getBackground().setAlpha(255);
        } else {
            buttonStart.isClickable = false
            buttonStart.getBackground().setAlpha(128);
        }
    }

    private inner class CustomTextWatcher(private val mEditText: EditText) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mEditText == numberHousesEditText) {
                mEditText.setBackgroundResource(R.drawable.edittext_bottom_line)
                numberHousesView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                numberHousesView.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            checkFields()
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
    }


}
