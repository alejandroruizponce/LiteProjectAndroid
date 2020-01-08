package com.chekinlite.app.OldVersionFiles

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.content.Intent
import android.widget.RelativeLayout
import com.chekinlite.app.CurrentVersion.Activities.MainActivity
import com.chekinlite.app.R


class TermsAndConditionsActivity : AppCompatActivity() {

    lateinit var textTyc: TextView
    lateinit var textPagination: TextView
    lateinit var buttonNext: Button
    lateinit var buttonPrevious: Button
    lateinit var buttonBack: Button
    lateinit var imagePagination: ImageView
    lateinit var bottomPagination: RelativeLayout
    lateinit var buttonAccept: Button
    lateinit var titleTerms: TextView
    var nPage = 1
    var typeTerms = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        typeTerms = intent.getIntExtra("typeTerms", 0)




        textPagination = findViewById(R.id.textViewNumberPag)
        var sourceString = "<b>1/</b>6"
        textPagination.setText(Html.fromHtml(sourceString))

        imagePagination = findViewById(R.id.imagePagination)



        textTyc = findViewById(R.id.textViewTyC)
        textTyc.setMovementMethod(ScrollingMovementMethod())

        textTyc.setText(Html.fromHtml(getString(R.string.page1TYC)))

        titleTerms = findViewById(R.id.titleBarTyC)
        bottomPagination = findViewById(R.id.bottomTermsAndConditions)
        buttonPrevious = findViewById(R.id.buttonPrevious)
        buttonNext = findViewById(R.id.buttonNext)

        if (typeTerms == 0) {
            buttonNext.setOnClickListener {
                if (nPage == 1) {
                    buttonPrevious.visibility = View.VISIBLE
                    nPage = 2
                    sourceString = "<b>2/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page2TYC)))
                    imagePagination.setImageResource(R.drawable.pagination2)
                } else if (nPage == 2) {
                    nPage = 3
                    sourceString = "<b>3/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page3TYC)))
                    imagePagination.setImageResource(R.drawable.pagination3)
                } else if (nPage == 3) {
                    nPage = 4
                    sourceString = "<b>4/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page4TYC)))
                    imagePagination.setImageResource(R.drawable.pagination4)
                } else if (nPage == 4) {
                    nPage = 5
                    sourceString = "<b>5/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page5TYC)))
                    imagePagination.setImageResource(R.drawable.pagination5)
                } else if (nPage == 5) {
                    nPage = 6
                    buttonNext.visibility = View.INVISIBLE
                    sourceString = "<b>6/</b>6"
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.scrollTo(0, 0)
                    textTyc.setText(Html.fromHtml(getString(R.string.page6TYC)))
                    imagePagination.setImageResource(R.drawable.pagination6)
                }
            }


            buttonPrevious.visibility = View.INVISIBLE
            buttonPrevious.setOnClickListener {
                if (nPage == 2) {
                    buttonPrevious.visibility = View.INVISIBLE
                    nPage = 1
                    sourceString = "<b>1/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page1TYC)))
                    imagePagination.setImageResource(R.drawable.pagination1)
                } else if (nPage == 3) {
                    nPage = 2
                    sourceString = "<b>2/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page2TYC)))
                    imagePagination.setImageResource(R.drawable.pagination2)
                } else if (nPage == 4) {
                    nPage = 3
                    sourceString = "<b>3/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page3TYC)))
                    imagePagination.setImageResource(R.drawable.pagination3)
                } else if (nPage == 5) {
                    nPage = 4
                    sourceString = "<b>4/</b>6"
                    textTyc.scrollTo(0, 0)
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page4TYC)))
                    imagePagination.setImageResource(R.drawable.pagination4)
                } else if (nPage == 6) {
                    nPage = 5
                    sourceString = "<b>5/</b>6"
                    textTyc.scrollTo(0, 0)
                    buttonNext.visibility = View.VISIBLE
                    textPagination.setText(Html.fromHtml(sourceString))
                    textTyc.setText(Html.fromHtml(getString(R.string.page5TYC)))
                    imagePagination.setImageResource(R.drawable.pagination5)
                }
            }
        } else {
            buttonPrevious.visibility = View.INVISIBLE
            buttonNext.visibility = View.INVISIBLE
            nPage = 1
            titleTerms.setText(R.string.pol_tica_de_privacidad)
            sourceString = "<b>1/</b>6"
            textTyc.scrollTo(0, 0)
            imagePagination.visibility = View.GONE
            textPagination.visibility = View.GONE
            bottomPagination.visibility = View.GONE
            textTyc.setText(Html.fromHtml(getString(R.string.pagePrivacyPolicy)))
        }


        buttonBack = findViewById(R.id.buttonBackTYC)
        buttonBack.setOnClickListener {
            finish()
        }

        buttonAccept = findViewById(R.id.buttonAcceptTyC)
        buttonAccept.setOnClickListener {
            if (typeTerms == 0) {
                val previousScreen = Intent(applicationContext, MainActivity::class.java)
                //Sending the data to Activity_A
                previousScreen.putExtra("TyCStatus", true)
                setResult(200, previousScreen)
                finish()
            } else {
                val previousScreen = Intent(applicationContext, MainActivity::class.java)
                //Sending the data to Activity_A
                previousScreen.putExtra("TyCStatus", true)
                setResult(201, previousScreen)
                finish()
            }
        }



    }
}
