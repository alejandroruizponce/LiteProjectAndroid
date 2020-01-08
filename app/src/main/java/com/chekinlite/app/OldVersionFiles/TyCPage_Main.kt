package com.chekinlite.app.OldVersionFiles


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chekinlite.app.R


class TyCPage_Main : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_ty_cpage_main, container, false)
        // Inflate the layout for this fragment
        return view
    }


}
