package com.chekinlite.app.OldVersionFiles;

import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

public class NotifyDocumentAdapter <GVH extends BookingDocumentViewHolder, CVH extends GuestDocumentViewHolder> extends ExpandableRecyclerViewAdapter<GVH, CVH> {

    public NotifyDocumentAdapter(ArrayList<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public CVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindChildViewHolder(CVH holder, int flatPosition, ExpandableGroup group, int childIndex) {

    }

    @Override
    public void onBindGroupViewHolder(GVH holder, int flatPosition, ExpandableGroup group) {

    }

    public void notifyGroupDataChanged() {
        expandableList.expandedGroupIndexes = new boolean[getGroups().size()];
        for (int i = 0; i < getGroups().size(); i++) {
            expandableList.expandedGroupIndexes[i] = false;
        }
    }
}
