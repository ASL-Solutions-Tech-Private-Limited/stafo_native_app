package com.stafo.app.screens.recharge.dataclass

import android.os.Parcel
import android.os.Parcelable

data class RechargeInfo(
    val price: String,
    val details: String,
    val validity: String,
    val offer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(price)
        parcel.writeString(details)
        parcel.writeString(validity)
        parcel.writeString(offer)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RechargeInfo> {
        override fun createFromParcel(parcel: Parcel): RechargeInfo = RechargeInfo(parcel)
        override fun newArray(size: Int): Array<RechargeInfo?> = arrayOfNulls(size)
    }
}
