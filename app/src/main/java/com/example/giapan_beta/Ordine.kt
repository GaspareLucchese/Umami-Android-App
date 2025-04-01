package com.example.giapan_beta

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Date

@Parcelize
data class Ordine(
    val idordine: Int,
    val codiceSpedizione: String,
    val flagConsegnato: Int = 0,
    var valutazioneOrdine: Float? = null,
    val RefUtente : String,
    val RefProdotto : Int,
    val dataConsegna: Date? = null,
    val name : String,
    val price : Double = 0.0,
    val resource : String,
    val indirizzo : String
):
Parcelable{}