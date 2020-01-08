package com.chekinlite.app.OldVersionFiles;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class GuestDocument implements Parcelable {
    public final String name;
    public String id;
    public Context context;

    public GuestDocument(String name, String id, Context context) {
        this.name = name;
        this.id = id;
        this.context = context;

    }

    protected GuestDocument(Parcel in,  String id, Context context) {
        name = in.readString();
        this.id = id;
        this.context = context;
    }

    public static final Creator<GuestDocument> CREATOR = new Creator<GuestDocument>() {
        @Override
        public GuestDocument createFromParcel(Parcel in) {
            return new GuestDocument(in, "", null);
        }

        @Override
        public GuestDocument[] newArray(int size) {
            return new GuestDocument[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}

