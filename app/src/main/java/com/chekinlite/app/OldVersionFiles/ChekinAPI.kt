package com.chekinlite.app.OldVersionFiles

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.AuthFailureError
import com.android.volley.request.JsonArrayRequest
import com.android.volley.request.JsonObjectRequest
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.*
import okhttp3.MultipartBody
import com.chekinlite.app.CurrentVersion.Models.SignatureModel
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile


object ChekinAPI {


    val apiStaging = "https://test4.chekin.io/api/v1/"
    val apiProduction = "https://api.chekin.io/api/v1/"
    val APIUrl = apiProduction

    val apiKeyStaging = "81f23bc4a4a4676ba78a8edae1fcc92c59d367b6"
    val apiKeyProduction = "e0ab7db7ac0492e897bfc2723ff55b011db4db05"
    val apiKey = apiKeyProduction

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
                APIUrl + "users/token/create/",
                parameters,
                Response.Listener {
                        response -> println("Respuesta del login de usuario: $response")
                        completion(response, true)
                },
                Response.ErrorListener {
                   completion(null, false )
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"


                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun registerUser(context: Activity, email: String, name: String, phone: String, password: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["name"] = name
        params["phone"] = phone
        params["type"] = "MGR"

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "users/register",
            parameters,
            Response.Listener {
                response -> println("Respuesta del registro de usuario: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"


                println("Las headers son:$params")
                return params
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
                APIUrl + "auth/password/reset/",
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
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"


                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun getUserProfile(context: Activity, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.GET,
                APIUrl + "users/profile",
                null,
                Response.Listener {
                        response -> println("Perfil recuperado con exito: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al recuperar perfil de usuario: $error")
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun changePassword(context: Activity, oldPass: String, newPass: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, String>()
        params["old_password"] = oldPass
        params["new_password"] = newPass

        println("Los parametros son:$params")
        println("El token es: ${UserProfile.token}")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.PUT,
                APIUrl + "users/password/change/",
                parameters,
                Response.Listener {
                        response -> println("Respuesta del cambiar contraseña: $response")
                        completion(response, true)

                },
                Response.ErrorListener {
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"


                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }


    fun getPlansSuscription(context: Activity, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonArrayRequest(Request.Method.GET,
                APIUrl + "payments/plans/",
                null,
                Response.Listener {
                        response -> println("Respuesta del recuperar planes de suscripcion: $response")
                        completion(response, true)

                },
                Response.ErrorListener {
                    error -> println("Error al recuperar planes de suscripcion: $error")
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"


                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun createSuscription(context: Activity, planType: String, housings: Int, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["plan"] = planType
        params["max_houses"] = housings

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "payments/user/subscriptions/",
            parameters,
            Response.Listener {
                    response -> println("Respuesta de crear suscripcion de usuario: $response")
                completion(response, true)

            },
            Response.ErrorListener {
                error ->
                println("Error al crear suscripcion: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun getCheckins(context: Activity, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET,
            APIUrl + "reservations/",
            null,
            Response.Listener {
                    response -> println("Respuesta de recuperar las reservas: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar las reservas: $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

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
              //  onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun getCheckinStatus(context: Activity, idCheckin: String, completion: (result: JSONArray?, status: Boolean) -> Unit) {


        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET,
            APIUrl + "reservations/$idCheckin/guests/",
            null,
            Response.Listener {
                    response -> println("Respuesta de Checkin Status de la reserva $idCheckin es: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error de Checkin Status de la reserva $idCheckin : $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun getAccommodations(context: Activity, completion: (result: JSONArray?, status: Boolean) -> Unit) {


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
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }
        propertiesRequest.setShouldCache(false)
        requestQueue.add(propertiesRequest)

    }

    fun addAccommodation(context: Activity, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "housings/",
            parameters,
            Response.Listener { response ->
                println("Propiedad creada con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al crear propiedad: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }

    fun updateAccommodation(context: Activity, idAccommodation:String, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.PUT,
            APIUrl + "housings/$idAccommodation",
            parameters,
            Response.Listener { response ->
                println("Propiedad actualizada con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al actualizar propiedad: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }



    fun getHostelryCodesSpain(context: Activity, police_user: String, pass_police: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, String>()
        params["police_user"] = police_user
        params["police_password"] = pass_police

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "housings/police_hostelry_codes",
            parameters,
            Response.Listener {
                    response -> println("Codigos de hospederia recuperadas con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar Codigos de hospederia: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun getHostelryCodesPoliceItaly(context: Activity, police_user: String, pass_police: String, certificate: String, completion: (result: JSONArray?, status: Boolean) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)

        val sr = object : StringRequest(Request.Method.POST, APIUrl + "housings/police_hostelry_codes",
            Response.Listener {
                    response ->
                    //completion(response, true)

                    Log.d(TAG, response.toString()) },
            Response.ErrorListener {
                    error ->
            }) {
            override fun getParams(): Map<String, String> {
                val parameters = HashMap<String, String>()
                parameters["police_user"] = police_user
                parameters["police_password"] = pass_police
                parameters["cert_password"] = certificate

                println("Los parametros son:$parameters")

                return parameters
            }

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }
        }

        requestQueue.add(sr)

    }


    fun getHostelryCodesIstatItaly(context: Activity, police_user: String, pass_police: String, portalIstat: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["istat_user"] = police_user
        params["istat_password"] = pass_police
        params["istat_type"] = portalIstat

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "housings/istat_houses",
            parameters,
            Response.Listener {
                    response -> println("Codigos de hospederia ISTAT recuperadas con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener {
                    error ->
                println("Error al recuperar Codigos de hospederia ISTAT: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun newChekin(context: Activity, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros de NEW CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "checkins/bookings",
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
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }

    fun updateChekin(context: Activity, idChekin:String, params: HashMap<String, Any>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val parameters = JSONObject(params)

        println("Los parametros son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.PATCH,
            APIUrl + "checkins/bookings/$idChekin",
            parameters,
            Response.Listener { response ->
                println("Reserva actualizada con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al actualizar reserva: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }

    fun deleteBooking(context: Activity, idBooking: String) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.DELETE,
                APIUrl + "checkins/bookings/$idBooking",
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
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }

    fun deleteProperty(context: Context, idProperty: String) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.DELETE,
                APIUrl + "housings/$idProperty",
                null,
                Response.Listener {
                        response -> println("Propiedad eliminada con exito: $response")

                },
                Response.ErrorListener {
                        error -> println("Error al eliminar propiedad: $error")
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun addGuestRegular(context: Activity, params: HashMap<String, Any>, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val obj = JSONObject(params)
        val parameters = JSONArray("["+obj.toString()+"]")

        println("Los parametros de ADD GUEST REGULAR son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonArrayRequest(Request.Method.POST,
            APIUrl + "checkins/persons",
            parameters,
            Response.Listener { response ->
                println("Huesped añadido con exito: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->

                completion(null, false)
                println("EL MENSAJE DE ERROR ES: ${error.networkResponse.headers}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }


        }

        requestQueue.add(jsonObjectRequest)


    }

    fun postSignature(context: Activity, idGuest: Int, signatureImage: Bitmap,  completion: (result: String?, status: Boolean) -> Unit) {


        //create a file to write bitmap data
        val f = File(context.getCacheDir(), "signatureFile");
        f.createNewFile();

        //Convert bitmap to byte array
        val bitmap = signatureImage
        val bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        val bitmapdata = bos.toByteArray();

        //write the bytes in file
        val fos = FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();


        val fbody = RequestBody.create(MediaType.parse("image/*"), f);
        val body = MultipartBody.Part.createFormData("signature_file", "signatureFile.png", fbody)

        var retrofit = RetrofitClient.getClient(APIUrl + "checkins/persons/")?.create(
            InterfaceChekinAPI::class.java)
        println("Los parametros de POST SIGNATURE son: ${UserProfile.token}, $apiKey, ${fbody}, $idGuest")
        val call = retrofit?.addSignature("Token ${UserProfile.token}",
            apiKey, body, idGuest)

        call?.enqueue(object: Callback<SignatureModel> {
            override fun onResponse(call: Call<SignatureModel>?, response: retrofit2.Response<SignatureModel>?) {
                println("RETROFIT POST SIGNATURE REALIZADO CON EXITO. CODIGO: ${response?.code()} ")
                /*
                val jObjError = JSONObject(response?.errorBody()?.string())
                println("RETROFIT POST SIGNATURE RESULTADO: ${response?.message().toString()} - ${jObjError} - ${response?.body().toString()}")*/
                if (response?.code()  == 200) {

                    val signatureResponse = response?.body()!!
                    println("RETROFIT POST SIGNATURE RESULTADO: $signatureResponse -- ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<SignatureModel>?, t: Throwable?) {
                println("RETROFIT POST SIGNATURE ERROR MENSAJE: ${t?.message}")
            }

        })



    }

    fun getPartGuest(context: Context, idGuest: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.GET,
                APIUrl + "checkins/persons/$idGuest/part",
                null,
                Response.Listener {
                        response -> println("Respuesta del recuperar parte de huesped: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al recuperar parte del huesped: $error")
                    onErrorResponse(error)
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"


                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun getGuestbook(context: Context, idProperty: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonObjectRequest(Request.Method.GET,
                APIUrl + "housings/$idProperty/books",
                null,
                Response.Listener {
                        response -> println("Respuesta del recuperar Guestbook: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al recuperar Guestbook: $error")
                    onErrorResponse(error)
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"


                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun getCollaborators(context: Activity, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonArrayRequest(Request.Method.GET,
                APIUrl + "users/collaborators",
                null,
                Response.Listener {
                        response -> println("Respuesta del recuperar lista de colaboradores: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al recuperar lista de colaboradores: $error")
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }
        jsonArrayRequest.setShouldCache(false)
        requestQueue.add(jsonArrayRequest)

    }

    fun deleteColaborator(context: Activity, idCollaborator: String, completion: (result: JSONArray?, status: Boolean) -> Unit) {

        val requestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = object :
            JsonArrayRequest(Request.Method.DELETE,
                APIUrl + "users/collaborators/$idCollaborator/",
                null,
                Response.Listener {
                        response -> println("Colaborador eliminado con exito: $response")
                    completion(response, true)

                },
                Response.ErrorListener {
                        error -> println("Error al eliminar colaborador: $error")
                    completion(null, false)
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonArrayRequest)

    }

    fun addCollaborator(context: Activity, id: String, email: String, name: String, phone: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["type"] = "COL"
        params["email"] = email
        params["name"] = name
        params["phone"] = phone
        params["manager"] = id
        params["password"] = "123"

        println("Los parametros son:$params")

        val parameters = JSONObject(params)

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "users/register",
            parameters,
            Response.Listener {
                    response -> println("Colaborador registrado con exito: $response")
                completion(response, true)

            },
            Response.ErrorListener {
                    error ->
                println("Error al registrar colaborador: $error")
                completion(null, false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)

    }





    fun retriggerGuests(context: Activity, arrayGuests: ArrayList<Int>, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["ids"] = arrayGuests

        val parameters = JSONObject(params)

        println("Los parametros de NEW CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "checkins/persons/retrigger",
            parameters,
            Response.Listener { response ->
                println("Huespedes relanzados con exito a la policia: $response")
                completion(response, true)
            },
            Response.ErrorListener { error ->
                println("Error al relanzar huespedes: $error")
                completion(null, false)
                //onErrorResponse(error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

                println("Las headers son:$params")
                return params
            }

        }

        requestQueue.add(jsonObjectRequest)


    }

    fun sendChekinOnline(context: Activity, idChekin: String, email: String, completion: (result: JSONObject?, status: Boolean) -> Unit) {

        val params = HashMap<String, Any>()
        params["booking"] = idChekin
        params["email"] = email

        val parameters = JSONObject(params)

        println("Los parametros de NEW CHEKIN son:$params")

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            APIUrl + "checkins/send_precheckin_link",
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
                params["Api-Key"] =
                    apiKey
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Token ${UserProfile.token}"

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

