package com.chekinlite.app.OldVersionFiles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chekinlite.app.CurrentVersion.Models.TableCollaboratorsController
import com.chekinlite.app.R
import org.json.JSONObject

class CollaboratorsActivity : AppCompatActivity(), TableCollaboratorsController.ItemClickListener {


    lateinit var recyclerView: RecyclerView
    var adapter: TableCollaboratorsController? = null
    var listCollaborators  = ArrayList<JSONObject>()
    lateinit var buttonBack: Button
    lateinit var buttonMySelf: Button
    lateinit var buttonAddColaborator: Button
    var LIST_COLLABORATORS = false

    lateinit var titleBar: TextView
    lateinit var selectColaborator: TextView
    lateinit var orTextView: TextView
    lateinit var mySelfView: ConstraintLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collaborators)

        LIST_COLLABORATORS = intent.getBooleanExtra("LIST_COLLABORATORS", false)

        titleBar = findViewById(R.id.titleBarColaborators)
        selectColaborator = findViewById(R.id.selectListColTextView)
        orTextView = findViewById(R.id.orTextView)
        mySelfView = findViewById(R.id.mySelfView)

        if (LIST_COLLABORATORS) {
            titleBar.setText(getString(R.string.collaborators))
            selectColaborator.setText(getString(R.string.collaboratorsList))
            orTextView.visibility = View.GONE
            mySelfView.visibility = View.GONE
        }

        buttonBack = findViewById(R.id.buttonBackCol)
        buttonBack.setOnClickListener {
           finish()
        }

        buttonMySelf = findViewById(R.id.buttonMySelf)
        buttonMySelf.setOnClickListener {
            val intent = Intent()
            var resp = getString(R.string.me)
            intent.putExtra("responsible", resp)
            setResult(100 , intent);
            finish()
        }


        ChekinAPI.getCollaborators(this) {
            result, status ->
            if(status) {
                if (result != null) {
                    for (num in 0..result.length() - 1) {
                        listCollaborators?.add(result[num] as JSONObject)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }




        // set up the RecyclerView
        val recView = findViewById<RecyclerView>(R.id.collaboratorTableView)
        if (recView != null) {
            recyclerView = recView
        }
        recyclerView?.setLayoutManager(LinearLayoutManager(this))

        adapter = this.let { listCollaborators?.let { it1 ->
            TableCollaboratorsController(
                it,
                it1
            )
        } }
        adapter?.setClickListener(this)
        recyclerView?.setAdapter(adapter)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        println("Entramos en onActivityResult")
        if (resultCode == 100) {
            ChekinAPI.getCollaborators(this) {
                    result, status ->
                if(status) {
                    if (result != null) {
                        for (num in 0..result.length() - 1) {
                            listCollaborators?.add(result[num] as JSONObject)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, intent)

        }
    }

    override fun onItemClick(view: View, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
