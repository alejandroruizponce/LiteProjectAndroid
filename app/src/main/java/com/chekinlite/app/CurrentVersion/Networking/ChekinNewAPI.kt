package com.chekinlite.app.CurrentVersion.Networking

import android.app.Activity
import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.AuthFailureError
import com.android.volley.request.JsonArrayRequest
import com.android.volley.request.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException

object ChekinNewAPI {


    val apiStaging = "http://api.chekintest.xyz/api/v3/"
    val apiProduction = "https://a.chekin.io/api/v3/"
    val APIUrl =
        apiProduction


    fun loginUser(context: Activity, user: String, password: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, String>()
        params["email"] = user
        params["password"] = password

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context as Context?)

        val jsonObjectRequest = object :
            JsonObjectRequest(
                Request.Method.POST,
                APIUrl + "token/",
                parameters,
                Response.Listener { response ->
                    println("Respuesta del login de usuario: $response")
                    completion(response, true)
                },
                Response.ErrorListener {
                    completion(null, false)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                params["Content-Type"] = "application/json"


                println("Las headers son:$headers")
                return headers
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun resetPassword(context: Activity, email: String) {

        val params = HashMap<String, String>()
        params["email"] = email

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.POST,
                APIUrl + "password/reset/email/",
                parameters,
                Response.Listener {
                        response -> println("Respuesta del recuperar contraseña: $response")

                },
                Response.ErrorListener {
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8";


                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }



    fun getCheckinsByPage(context: Activity, parametersURL: String, page: Int, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object : JsonObjectRequest(Request.Method.GET,
            APIUrl + "$parametersURL",
            null,
            Response.Listener {
                    response -> println("Respuesta de recuperar las reservas en pagina $page: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar las reservas en pagina $page: $error")
                completion(null, false)
                onErrorResponse(
                    error
                )
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun newChekin(context: Activity, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros de NEW CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "reservations/",
            parameters,
            Response.Listener { response ->
                println("Reserva creada con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al crear reserva: $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun updateChekin(context: Activity, idBooking: String, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros de UPDATE CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.PATCH,
            APIUrl + "reservations/$idBooking/",
            parameters,
            Response.Listener { response ->
                println("Reserva actualizada con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al actualizar reserva: $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun getOneBooking(context: Activity, idBooking: String,  completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.GET,
                APIUrl + "reservations/$idBooking/",
                null,
                Response.Listener {
                    response ->
                    completion(response, true)
                    println("Reserva recuperada con exito: $response")

                },
                Response.ErrorListener {
                        error ->
                    completion(null, false)
                    println("Error al recuperar reserva: $error")
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }

    fun deleteBooking(context: Activity, idBooking: String) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.DELETE,
                APIUrl + "reservations/$idBooking/",
                null,
                Response.Listener {
                        response -> println("Reserva eliminada con exito: $response")

                },
                Response.ErrorListener {
                        error -> println("Error al eliminar reserva: $error")
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }



    fun getAccommodations(context: Activity, completion: (result: Any, status: Boolean) -> Unit) {


        val requestQueue = Volley.newRequestQueue(context)

        val propertiesRequest = object : JsonArrayRequest(Request.Method.GET,
            APIUrl + "housings/",
            null,
            Response.Listener {
                    response -> println("Propiedades recuperadas con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar las propiedades: $error")
                completion(error, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }
        propertiesRequest.setShouldCache(false)
        requestQueue.add(propertiesRequest)

    }

    fun getLocations(context: Context, countryID: String, levelDiv: String, completion: (result: Any, status: Boolean) -> Unit) {


        val requestQueue = Volley.newRequestQueue(context)

        val propertiesRequest = object : JsonObjectRequest(Request.Method.GET,
            APIUrl + "locations/?country=$countryID&division_level=$levelDiv",
            null,
            Response.Listener {
                    response -> println("Localizaciones para $countryID en div level $levelDiv recuperadas con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar las localizaciones: $error")
                completion(error, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }
        propertiesRequest.setShouldCache(false)
        requestQueue.add(propertiesRequest)

    }

    fun getDocTypesByCountry(context: Activity, countryProvin: String, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonArrayRequest(Request.Method.GET,
                APIUrl + "doc-types/?country=$countryProvin",
                null,
                Response.Listener {
                        response -> println("Respuesta del recuperar lista de doc types: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al recuperar lista de doc types: $error")
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun addGuestRegular(context: Activity, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val obj = JSONObject(params)
        //val parameters = JSONArray("["+obj.toString()+"]")

        println("Los parametros de ADD GUEST REGULAR son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "guests/",
            obj,
            Response.Listener { response ->
                println("Huesped añadido con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                onErrorResponse(
                    error
                )
                completion(null, false)
                println("EL MENSAJE DE ERROR ES: ${error.networkResponse.headers}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }


        }

        requestQueue.add(jsonObjectRequest)
    }

    fun updateGuestRegular(context: Activity, idGuest: String, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val obj = JSONObject(params)
        //val parameters = JSONArray("["+obj.toString()+"]")

        println("Los parametros de UPDATE GUEST REGULAR son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.PATCH,
            APIUrl + "guests/$idGuest/",
            obj,
            Response.Listener { response ->
                println("Huesped actualizado con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                onErrorResponse(
                    error
                )
                completion(null, false)
                println("EL MENSAJE DE ERROR ES: ${error.networkResponse.headers}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }


        }

        requestQueue.add(jsonObjectRequest)
    }

    fun deleteGuest(context: Context, idGuest: String) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.DELETE,
                APIUrl + "guests/$idGuest/",
                null,
                Response.Listener {
                        response -> println("Huesped eliminada con exito: $response")

                },
                Response.ErrorListener {
                        error -> println("Error al eliminar huesped: $error")
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }

    fun sendToPoliceManually(context: Activity, idChekin: String, completion: (result: Any, status: Boolean) -> Unit) {


        val requestQueue = Volley.newRequestQueue(context)
        println("La id de la reserva para enviar la policia manualmente es: $idChekin")

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "reservations/$idChekin/resend-reservation-to-police/",
            null,
            Response.Listener { response ->
                println("Envio a la policia manualmente enviado con exito!: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al enviar a la policia manualmente: $error")
                completion(error, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }




    fun sendChekinOnline(context: Activity, idChekin: String, email: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["reservation"] = idChekin
        params["email_address"] = email

        val parameters = JSONObject(params)

        println("Los parametros de NEW CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "reservation-emails/",
            parameters,
            Response.Listener { response ->
                println("Chekin online enviado con exito!: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al enviar el Chekin Online: $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Authorization"] =
                    UserProfile.token

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }

    fun onErrorResponse(error: com.android.volley.error.VolleyError) {
        val body: String
        //get status code here
//        val statusCode = error.networkResponse.statusCode.toString()
        //get response body and parse with appropriate encoding
        if (error.networkResponse.data != null) {
            try {
                body = String(error.networkResponse.data, Charsets.UTF_8)
                println("EL MENSAJE DE ERROR ES: $body")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

        }


        //do stuff with the body...
    }


}
