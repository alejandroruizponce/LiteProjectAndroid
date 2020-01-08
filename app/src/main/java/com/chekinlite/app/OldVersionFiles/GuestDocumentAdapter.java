package com.chekinlite.app.OldVersionFiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chekinlite.app.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GuestDocumentAdapter extends ExpandableRecyclerViewAdapter<BookingDocumentViewHolder, GuestDocumentViewHolder> {
    public GuestDocumentAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public BookingDocumentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_bookingdocument, parent, false);
        return new BookingDocumentViewHolder(v);
    }

    @Override
    public GuestDocumentViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_guestdocument, parent, false);
        return new GuestDocumentViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(GuestDocumentViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final GuestDocument product = (GuestDocument) group.getItems().get(childIndex);
        holder.bind(product);
    }

    @Override
    public void onBindGroupViewHolder(BookingDocumentViewHolder holder, int flatPosition, ExpandableGroup group) {
        final BookingDocument company = (BookingDocument) group;
        holder.bind(company);
    }
}