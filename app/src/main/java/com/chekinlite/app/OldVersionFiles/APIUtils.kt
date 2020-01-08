package com.chekinlite.app.OldVersionFiles

object ApiUtils {

    val BASE_URL = "http://jsonplaceholder.typicode.com/"

    val apiService: InterfaceChekinAPI
        get() = RetrofitClient.getClient(BASE_URL)!!.create(
            InterfaceChekinAPI::class.java)
}