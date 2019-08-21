package com.kerberosns.rccar;

import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {
    private boolean mDevMode;

    Settings() {
        mDevMode = false;
    }

    public boolean isDevelopmentMode() {
        return mDevMode;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        mDevMode = developmentMode;
    }

    /*
     * Parcelable implementation.
     *
     * The code below writes integers as oppose to boolean into the parcel. This is because of the
     * API 16. 16 does not support writing booleans.
     */

    public void writeToParcel(Parcel dest, int flags) {
        int _mDevMode = mDevMode  ? 1 : 0;
        dest.writeInt(_mDevMode);
    }

    private Settings(Parcel in) {
        mDevMode = in.readInt() != 0;
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public int describeContents() {
        return 0;
    }
}
