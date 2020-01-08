package com.chekinlite.app.OldVersionFiles


import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.json.JSONObject
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.os.Handler
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.R


class fragment_Properties : Fragment(),
    TablePropertiesController.ItemClickListener {


    lateinit var recyclerView: RecyclerView
    var adapter: TablePropertiesController? = null
    var listProperties: ArrayList<JSONObject> = ArrayList()

    lateinit var buttonAddProperty: ImageButton
    lateinit var progressBar: ProgressBar

    lateinit var noPropertiesView: ConstraintLayout
    lateinit var noPropertyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_properties, container, false)
        // Inflate the layout for this fragment

        UserProfile.tabIsDisplayed = 1

        progressBar = view.findViewById(R.id.progressBarProperties)

        noPropertiesView = view.findViewById(R.id.noPropertiesView)
        noPropertiesView.visibility = View.INVISIBLE
        noPropertyButton = view.findViewById(R.id.buttonNoProperty)
        noPropertyButton.setOnClickListener {
            val intent = Intent(activity, AddProperty::class.java)
            // start your next activity
            startActivity(intent)
        }

        println("ENTRAMOS EN FRAGMENT PROPIEDADES desde ONCREATE")
        recyclerView = view.findViewById<RecyclerView>(R.id.propertiesTableView)

        loadProperties()



        buttonAddProperty = view.findViewById(R.id.buttonAddPropertyCircle)
        buttonAddProperty.setOnClickListener{
            val intent = Intent(activity, AddProperty::class.java)
            // start your next activity
            startActivity(intent)
        }







        return view
    }

    fun loadProperties() {

            listProperties.clear()

            progressBar.visibility = View.VISIBLE
            UserProfile.needChekinReload = true
            println("Se pone en falso needPropertyReload")

            println("Recuperamos las propiedades...")
            activity?.let {
                ChekinAPI.getAccommodations(it) { result, status ->
                    if (status) {
                        if (result != null) {
                            for (num in 0..result.length() - 1) {
                                listProperties?.add(result[num] as JSONObject)
                                adapter?.notifyDataSetChanged()
                            }

                            if (listProperties.count() == 0) {
                                progressBar.visibility = View.VISIBLE
                            } else {
                                progressBar.visibility = View.INVISIBLE
                            }
                            UserProfile.listProperties = listProperties
                        }

                    }
                }
            }

            // set up the RecyclerView

            recyclerView?.setLayoutManager(LinearLayoutManager(activity))

            adapter = this.context?.let {
                listProperties?.let { it1 ->
                    TablePropertiesController(
                        it,
                        it1
                    )
                }
            }
            adapter?.setClickListener(this)
            recyclerView?.setAdapter(adapter)

        val handler = Handler()
        handler.postDelayed(Runnable {
            if (listProperties.count() == 0) {
                noPropertiesView.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            } else {
                noPropertiesView.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }

        }, 5000)

    }

    override fun onResume() {
        super.onResume()
        println("ENTRAMOS EN FRAGMENT PROPIEDADES desde ONRESUME")
        /*
        if (UserProfile.needPropertyReload) {
            loadProperties()
        }*/

    }



    override fun onItemClick(view: View, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
