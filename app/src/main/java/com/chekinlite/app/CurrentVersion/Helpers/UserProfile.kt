package com.chekinlite.app.CurrentVersion.Helpers

import android.graphics.Bitmap
import android.support.design.widget.BottomNavigationView
import com.chekinlite.app.CurrentVersion.NavigationMenu.Booking
import org.json.JSONObject

object UserProfile {
    var token = ""
    var bookingNeedReload = true
    var provincesPortugal = arrayOf("Aveiro","Beja","Braga","Bragança","Castelo Branco","Coimbra","Faro","Guarda","Leiria","Lisboa","Portalegre","Porto","Região Autónoma da Madeira","Região Autónoma dos Açores","Santarém","Setúbal","Viana do Castelo","Vila Real","Viseu","Évora")
    var provincesItaly = arrayOf("Agrigento","Alessandria","Ancona","Aosta","Arezzo","Ascoli Piceno","Asti","Avellino","Bari","Barletta-Andria-Trani","Belluno","Benevento","Bergamo","Biella","Bologna","Bolzano/ Bogen","Brescia","Cagliari","Caltanissetta","Campobasso","Caserta","Catania","Catanzaro","Chieti","Como","Cosenza","Cremona","Crotone","Cuneo","Enna","Fermo","Ferrara","Florence","Foggia","Forlì-Cesena","Frosinone","Genoa","Grosseto","Imperia","Isernia","L'Aquila","La Spezia","Latina","Lecce","Lecco","Livorno","Lodi","Lucca","Macerata","Mantua","Massa and Carrara","Matera","Messina","Milan","Modena","Monza and Brianza","Napoles","Novara","Nuoro","Oristano","Padua","Palermo","Parma","Pavia","Perugia","Pesaro and Urbino","Pescara","Piacenza","Pisa","Pistoia","Potenza","Prato","Ragusa","Ravenna","Reggio Calabria","Reggio Emilia","Rieti","Rimini","Roma","Rovigo","Salerno","Sassari","Savona","Siena","Sondrio","South Sardinia","Syracuse","Taranto","Teramo","Terni","Trapani","Trento","Treviso","Turin","Varese","Venice","Verbano-Cusio-Ossola","Vercelli","Verona","Vibo Valentia","Vicenza","Viterbo")
    var provincesSpain = arrayOf("Álava", "Albacete", "Alicante","Almería","Ávila","Badajoz","Barcelona","Burgos","Castellón","Ceuta","Ciudad Real","Cuenca","Cáceres","Cádiz","Córdoba","Gerona","Granada","Guadalajara","Guipúzcoa","Huelva","Huesca","Jaén","La Coruña","Las Palmas","León","Lérida","Logroño","Lugo","Madrid","Málaga","Melilla","Murcia","Orense","Palencia","Pamplona","Pontevedra","Salamanca","Segovia","Sevilla","Soria","Tarragona","Tenerife","Teruel","Toledo","Valencia","Valladolid","Vizcaya","Zamora","Zaragoza")
    var listProperties: ArrayList<JSONObject> = ArrayList()
    var listBookings: ArrayList<JSONObject> = ArrayList()
    var listGuestBookings: ArrayList<Booking> = ArrayList()
    var filterMode = 0

    var needBookingReload = true
    var needChekinReload = true
    var needPropertyReload = true
    var needDocumentReload = true
    var needBookingDetailsReload = false
    lateinit var bookingDetailsUpdatedID: String
    lateinit var IDimage: Bitmap

    var auxIsID = false
    var auxSecondScanned = false
    var auxfirstScanned = false
    var auxX = 0
    var auxY = 0

    var IDFrontDocumentScanned = ""
    var IDBackDocumentScanned = ""
    var PassportDocumentScanned = ""


    var tabIsDisplayed = 0

    var nPage = 1

    lateinit var navigationBar: BottomNavigationView
}