package com.chekinlite.app.OldVersionFiles

import com.chekinlite.app.CurrentVersion.Models.SignatureModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InterfaceChekinAPI {

    @Multipart
    @POST( "signature")
    fun addSignature(
        @Header("Authorization") authorization: String, @Header("Api-Key") apiKey: String, @Part image: MultipartBody.Part, @Part("checkin") idGuest: Int): Call<SignatureModel>
}