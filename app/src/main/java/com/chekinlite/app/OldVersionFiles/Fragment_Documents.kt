package com.chekinlite.app.OldVersionFiles


import android.animation.LayoutTransition
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import com.reginald.editspinner.EditSpinner
import org.json.JSONObject
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.widget.Button
import android.widget.ProgressBar
import com.chekinlite.app.CurrentVersion.NavigationMenu.Booking
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R


class Fragment_Documents : Fragment() {

    var listProperties: ArrayList<JSONObject> = ArrayList()
    var listBookings: ArrayList<JSONObject> = ArrayList()
    var listCompletedBookings: ArrayList<Booking> = ArrayList()
    var nameProperties: ArrayList<String> = ArrayList()
    var propertyRow = 0
    var isFirstPropertyLoaded = false
    var bookingsDocuments = ArrayList<BookingDocument>()
    var listGuests: ArrayList<JSONObject> = ArrayList()

    lateinit var buttonGuestbook : Button

    lateinit var adapter: GuestDocumentAdapter

    lateinit var guestAdapter: GuestDocumentAdapter

    lateinit var filterPropertySpinner: EditSpinner
    lateinit var filterPropertyView: RelativeLayout

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    lateinit var noDocumentsView: ConstraintLayout
    var guestbookLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_documents, container, false)

        UserProfile.tabIsDisplayed = 3

        filterPropertySpinner = view.findViewById(R.id.propertySpinnerDocumentEditText)
        filterPropertyView = view.findViewById(R.id.propertiesDocumentView)

        progressBar = view.findViewById(R.id.progressBarDocuments)
        noDocumentsView = view.findViewById(R.id.noDocumentsView)
        noDocumentsView.visibility = View.INVISIBLE

        /*
        if (UserProfile.needDocumentReload) {

            val handler = Handler()
            handler.postDelayed(Runnable {
                listProperties = UserProfile.listProperties
                getPropertiesNames()

                recyclerView = view.findViewById(R.id.guestsDocumentsTableView)
                recyclerView.setLayoutManager(LinearLayoutManager(activity))

                buttonGuestbook = view.findViewById(R.id.buttonGuestbook)
                buttonGuestbook.isClickable = false
                buttonGuestbook.background.alpha = 64
                var idProper = listProperties[propertyRow].getInt("id")
                activity?.let { it1 ->
                    println("Se hace la llamada a Guestbook")
                    ChekinAPI.getGuestbook(it1, idProper.toString()) { result, status ->
                        if (status) {
                            if (result != null) {
                                buttonGuestbook.isClickable = true
                                buttonGuestbook.background.alpha = 255
                                result.getJSONArray("books").let { books ->
                                    for (i in 0..books.length() - 1) {
                                        var book = books[i] as JSONObject
                                        println("Guestbook $i es: ${book.getString("book_file_permalink")}")
                                        guestbookLoaded = true
                                    }
                                }

                            }
                        }

                    }
                }
                buttonGuestbook.setOnClickListener {
                    if (guestbookLoaded) {

                    }
                }

            }, 2000)
        }*/


        return view
    }

    fun loadDocumentsInProperty() {
        println("LA LISTA DE RESERVAS FILTRADAS Y COMPLETADAS ES: ${listCompletedBookings.count()}")

        bookingsDocuments.clear()


        for (i in 0..listCompletedBookings.count()-1) {
            //println("Reserva: ${listCompletedBookings[i].booking}")
            var guestsInBooking = ArrayList<GuestDocument>()
            listGuests.clear()
            for (num in 0..listCompletedBookings[i].listGuests.length()-1) {
                listGuests?.add(listCompletedBookings[i].listGuests?.get(num) as JSONObject)
            }
            for (j in  0..listGuests.count()-1) {
               // println("Huesped ${listGuests[j]} añadido a la reserva ${listCompletedBookings[i].booking.getString("guest_name")}")
                var name = listGuests[j].getString("name")
                var idG = listGuests[j].getString("legacy_id")
                name = name.toLowerCase()

                var surname = listGuests[j].getString("surname")
                var surnArray = surname.split(" ")

                surname = surnArray[0].toLowerCase()


                name = name.capitalize()
                surname = surname.capitalize()

                //println("Se añade al huesped $name $surname a la documentacion.")
                guestsInBooking.add(
                    GuestDocument(
                        ("$name $surname"),
                        idG,
                        activity
                    )
                )
            }

            var bD = BookingDocument(
                listCompletedBookings[i].booking.getString("checkin_date"),
                guestsInBooking
            )
            bookingsDocuments.add(bD)

            println("La lista de documentos en la iteraccion $i es: ")
            for (t in 0..bookingsDocuments.count() - 1) {
                for (m in 0..bookingsDocuments[t].itemCount - 1) {
                    println("Reserva $i en huesped $m : ${bookingsDocuments[t].items[m].name}")
                }
                println("---------")
            }
        }


        guestAdapter =
            GuestDocumentAdapter(bookingsDocuments)

        if (!isFirstPropertyLoaded) {
            adapter = guestAdapter
            recyclerView.adapter = adapter
            isFirstPropertyLoaded = true
        } else {
            println("Notificamos al adaptor que actualice las reservas documentadas")
            recyclerView.setLayoutManager(LinearLayoutManager(activity))
            adapter = guestAdapter
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        progressBar.visibility = View.INVISIBLE

    }

    fun getPropertiesNames() {
        if (listProperties.count() > 0) {
            println("La cantidad de propiedades es: ${listProperties.count()}")
            for (i in 0..listProperties.count() - 1) {
                val property = listProperties.get(i)
                property.getString("tradename").let { name ->
                    println("Se añade nombre de propiedad a nameProperties en index $i")
                    nameProperties?.add(name)
                }
            }
            println("nameProperties es: $nameProperties")

            val adapter = ArrayAdapter(
                activity, R.layout.item_dropdown,
                nameProperties
            )
            filterPropertySpinner.setText(nameProperties?.get(0))
            filterPropertySpinner.setBackgroundResource(R.drawable.edittext_bottom_line)
            filterPropertyView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            filterPropertyView.setBackgroundColor(Color.parseColor("#ffffff"))
            filterPropertySpinner.dropDownBackground.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            filterPropertySpinner.setAdapter(adapter)

            // triggered when one item in the list is clicked
            filterPropertySpinner.setOnItemClickListener { parent, view, position, id ->
                propertyRow = position
                listCompletedBookings.clear()
                listBookings.clear()
                nameProperties.clear()
                val ft = fragmentManager!!.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false)
                }
                ft.detach(this).attach(this).commit()

                //getCheckinFromProperty(propertyRow)
            }

            getCheckinFromProperty(propertyRow)
        }
    }

    fun getCheckinFromProperty(index: Int) {
        var listFilteredBookings: ArrayList<JSONObject> = ArrayList()
        if (nameProperties.count() > 0) {
            filterPropertySpinner.setText(nameProperties[index])
            println("DVC - Vamos a chequear las reservas en la propiedad: ${nameProperties[index]}")
            activity?.let { ChekinAPI.getCheckins(it) {
                result, status ->
                if(status) {
                    if(result != null) {
                        if (result.length() > 0) {
                            for (num in 0..result.length()-1) {
                                listBookings?.add(result[num] as JSONObject)
                            }
                            for(book in listBookings){
                                book.getString("housing").let {
                                    propertyID ->
                                        if(bookingOnProperty(propertyID, index)) {
                                            println("DVC - Añadimos reserva -${book}- a la lista para la propiedad: $propertyID")
                                            listFilteredBookings.add(book)
                                        }
                                }
                            }
                            println("DVC - Chequeamos los huespedes en la lista de reservas filtradas para la propiedad: $listFilteredBookings")
                            checkGuestsInBookings(listFilteredBookings, 0)
                        }
                    }
                }
            } }

        } else {
            progressBar.visibility = View.INVISIBLE

        }
    }

    fun checkGuestsInBookings (bookings: ArrayList<JSONObject>, index: Int) {
        var i = index
        if (bookings.count() != 0){
            noDocumentsView.visibility = View.INVISIBLE
            bookings[i].getString("origin").let {
                type ->
                if (type == "REGULAR" || type == "CHEKIN_ONLINE") {
                    bookings[i].getString("id").let {
                        idBook ->
                        var nGuests = bookings[i].optInt("total_guests_qty", -1)
                            if (nGuests >= 0) {
                                println("El numero de adultos es: $nGuests")
                                activity?.let {
                                    ChekinAPI.getCheckinStatus(it, idBook) { result, status ->
                                        if(status){
                                            if(result != null) {
                                                    if (result.length() > 0) {
                                                        println("La reserva $idBook tiene al menos un huesped registrado")
                                                        listCompletedBookings.add(
                                                            Booking(
                                                                nGuests,
                                                                result.length(),
                                                                bookings[i],
                                                                result,
                                                                false
                                                            )
                                                        )
                                                        println("Hay ${result.length()} huespedes registrados en total")
                                                        if(index == bookings.count() - 1) {
                                                            println("Se resetea la tabla solo con las reservas de la propiedad seleccionada")
                                                            println("Se termina el -- de reservas")
                                                            loadDocumentsInProperty()
                                                            adapter?.notifyDataSetChanged()
                                                        } else {
                                                            println("Reserva $idBook añadida. Se pasa a la siguiente reserva.")
                                                            checkGuestsInBookings(bookings, i+1)
                                                        }
                                                    } else {
                                                        if(index == bookings.count() - 1) {
                                                            println("Se resetea la tabla solo con las reservas de la propiedad seleccionada")
                                                            println("Se termina el -- de reservas")
                                                            loadDocumentsInProperty()
                                                            adapter?.notifyDataSetChanged()
                                                        } else {
                                                            println("Reserva $idBook vacia. Se pasa a la siguiente reserva.")
                                                            checkGuestsInBookings(bookings, i+1)
                                                        }
                                                    }
                                                } else {
                                                    println("Reserva $idBook vacia. RESULT IS NULL")
                                                    adapter?.notifyDataSetChanged()
                                            }
                                        }  else {
                                            println("Reserva $idBook vacia. STATUS IS FALSE")
                                        }

                                    }
                                }
                        } else {
                                if(index == bookings.count() - 1) {
                                    println("Se resetea la tabla solo con las reservas de la propiedad seleccionada")
                                    println("Se termina el -- de reservas")
                                    loadDocumentsInProperty()
                                    adapter?.notifyDataSetChanged()
                                } else {
                                    println("Reserva $idBook vacia. Se pasa a la siguiente reserva.")
                                    checkGuestsInBookings(bookings, i+1)
                                }
                                listBookings.removeAt(index)
                            }

                    }
                }
            }
        } else {
            progressBar.visibility = View.INVISIBLE
            noDocumentsView.visibility = View.VISIBLE
        }
    }

    fun bookingOnProperty(idProperty: String, index: Int): Boolean {
        listProperties[index].getInt("id").let {
            idProp ->
            if(idProperty == idProp.toString()) {
                return true
            }
        }
        return false
    }




}


