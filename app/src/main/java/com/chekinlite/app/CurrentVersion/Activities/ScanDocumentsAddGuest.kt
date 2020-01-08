package com.chekinlite.app.CurrentVersion.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Base64
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.camerakit.CameraKitView
import android.graphics.Bitmap
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R
import com.justinnguyenme.base64image.Base64Image


class ScanDocumentsAddGuest : AppCompatActivity() {

    lateinit var cameraKitView: CameraKitView
    lateinit var scanButton: Button
    lateinit var overlayScan: ImageView
    lateinit var animationCheck: LottieAnimationView
    lateinit var messageScanned: TextView
    lateinit var titleScan: TextView
    lateinit var subTitleScan: TextView
    lateinit var imageScanned: ImageView

    lateinit var buttonBack: Button

    lateinit var cameraDocumentLayout: ConstraintLayout
    lateinit var idDocumentOptionView: ConstraintLayout
    lateinit var passportOptionView: ConstraintLayout

    var isPassport = false

    var continueON = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_documents_add_guest)

        cameraKitView = findViewById(R.id.camera)
        overlayScan = findViewById(R.id.overlayImageView)
        scanButton = findViewById(R.id.buttonScanDocumentSD)
        animationCheck = findViewById(R.id.lottieAnimationCheck)
        animationCheck.setVisibility(View.GONE)
        messageScanned = findViewById(R.id.messageScanned)
        messageScanned.setVisibility(View.GONE)
        cameraDocumentLayout = findViewById(R.id.cameraDocumentLayout)
        titleScan = findViewById(R.id.titleScanDocument)
        subTitleScan = findViewById(R.id.subTitleScanDocument)
        imageScanned = findViewById(R.id.imageScannedAD)



        idDocumentOptionView = findViewById(R.id.IDOptionView)
        idDocumentOptionView.setOnClickListener {
            UserProfile.auxIsID = true
            idDocumentOptionView.visibility = View.GONE
            passportOptionView.visibility = View.GONE
            cameraDocumentLayout.visibility = View.VISIBLE
            titleScan.text = "ESCANEA TU ID"
            subTitleScan.text = "Por la parte de la foto."

        }
        passportOptionView = findViewById(R.id.passportOptionView)
        passportOptionView.setOnClickListener {
            isPassport = true
            idDocumentOptionView.visibility = View.GONE
            passportOptionView.visibility = View.GONE
            cameraDocumentLayout.visibility = View.VISIBLE
            titleScan.text = "ESCANEA TU PASAPORTE"
            subTitleScan.text = "Por la parte de la foto."


        }

        buttonBack = findViewById(R.id.buttonBackAD)
        buttonBack.setOnClickListener {
            finish()
        }


        val sdkVersion = android.os.Build.VERSION.SDK_INT
        println("EL SDK VERSION DE ESTE MOVIL ES: $sdkVersion")

        if (UserProfile.auxIsID && !UserProfile.auxSecondScanned) {
            idDocumentOptionView.visibility = View.GONE
            passportOptionView.visibility = View.GONE
            cameraDocumentLayout.visibility = View.VISIBLE

            continueON = false
            imageScanned.setImageDrawable(null)
            subTitleScan.text = "Por la parte del código MRZ (Por detrás)."
            scanButton.setText("CAPTURAR")
            animationCheck.setVisibility(View.INVISIBLE)
            messageScanned.setVisibility(View.INVISIBLE)
            overlayScan.visibility = View.INVISIBLE
        }


        cameraKitView.imageMegaPixels = 1.9f



        scanButton.setOnClickListener(View.OnClickListener {
            if (!continueON) {
                cameraKitView.captureImage(CameraKitView.ImageCallback { cameraKitView, capturedImage ->


                    val captured = Base64.encodeToString(capturedImage, Base64.DEFAULT)
                    Base64Image.instance.decode(captured, { bitmap ->
                        bitmap?.let { dr ->
                            UserProfile.IDimage = Bitmap.createBitmap(
                                dr,
                                cameraDocumentLayout.left,
                                cameraDocumentLayout.top,
                                cameraDocumentLayout.getWidth(),
                                cameraDocumentLayout.getHeight()
                            )
                        }
                    })
                    cameraKitView.onPause()


                    if (isPassport) {
                        println("FOTO PASAPORTE GUARDADA")
                        UserProfile.PassportDocumentScanned =
                            Base64.encodeToString(capturedImage, Base64.DEFAULT)
                    } else if (UserProfile.auxIsID && !UserProfile.auxfirstScanned) {
                        UserProfile.auxfirstScanned = true
                        println("FOTO ID FRONTAL GUARDADA")
                        UserProfile.IDFrontDocumentScanned =
                            Base64.encodeToString(capturedImage, Base64.DEFAULT)
                    } else {
                        UserProfile.auxSecondScanned = true
                        println("FOTO ID TRASERA GUARDADA")
                        UserProfile.IDBackDocumentScanned =
                            Base64.encodeToString(capturedImage, Base64.DEFAULT)
                    }

                    scanButton.setText("CONTINUAR")
                    continueON = true

                    val animFadeIn =
                        AnimationUtils.loadAnimation(applicationContext,
                            R.anim.fadein
                        )
                    overlayScan.visibility = View.VISIBLE
                    overlayScan.setBackgroundColor(-0x4cffbd66)
                    animFadeIn.reset()
                    overlayScan.clearAnimation()
                    overlayScan.startAnimation(animFadeIn)

                    animationCheck.setVisibility(View.VISIBLE)
                    animationCheck.playAnimation()

                    messageScanned.setVisibility(View.VISIBLE)
                    messageScanned.clearAnimation()
                    messageScanned.startAnimation(animFadeIn)


                })
            } else {
                if (UserProfile.auxIsID && !UserProfile.auxSecondScanned) {
                    finish();
                    startActivityForResult(getIntent(), 100);
                } else {
                    val intent = Intent()
                    if (UserProfile.auxIsID) {
                        setResult(200, intent);
                    } else {
                        setResult(101, intent);
                    }
                    finish()
                }
            }
        })

    }


    override fun onStart() {
        super.onStart()
        cameraKitView.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView.onResume()
    }

    override fun onPause() {
        cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        println("Entramos en onActivityResult desde SCAN DOCUMENTS")
        if (resultCode == 200) {
            println("onActivityResult Status 200")
            val intent = Intent()
            setResult(200, intent);
            finish()
        } else {
            println("onActivityResult Status 101")
            val intent = Intent()
            setResult(101, intent);
            finish()
        }

    }
}
