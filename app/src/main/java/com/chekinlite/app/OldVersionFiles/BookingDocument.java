package com.chekinlite.app.OldVersionFiles;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class BookingDocument extends ExpandableGroup<GuestDocument> {
    public BookingDocument(String title, List<GuestDocument> items) {
        super(title, items);
    }
}

