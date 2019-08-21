package com.kerberosns.rccar.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
    private String name;
    private String address;
    private boolean paired;

    public Device(String name, String address, boolean paired) {
        this.name = name;
        this.address = address;
        this.paired = paired;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isPaired() {
        return paired;
    }

    /*
     * Parcelable implementation.
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeInt(paired ? 1 : 0);
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    private Device(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.paired = in.readInt() == 1;
    }
}
