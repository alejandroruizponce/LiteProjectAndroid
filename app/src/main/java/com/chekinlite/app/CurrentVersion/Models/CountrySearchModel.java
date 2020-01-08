package com.chekinlite.app.CurrentVersion.Models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class CountrySearchModel implements Searchable {
    private String mTitle;
    private int mId;

    public CountrySearchModel(String title, int id) {
        mTitle = title;
        mId = id;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }


    public int getID() {
        return mId;
    }

    public CountrySearchModel setTitle(String title, int id) {
        mTitle = title;
        mId = id;
        return this;
    }
}