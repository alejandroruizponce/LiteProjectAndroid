package com.chekinlite.app.CurrentVersion.Models

import com.google.gson.annotations.SerializedName

class SignatureModel {

    @SerializedName("coord")
    var guestID: String? = null
    @SerializedName("sys")
    var signature_file: String? = null

}