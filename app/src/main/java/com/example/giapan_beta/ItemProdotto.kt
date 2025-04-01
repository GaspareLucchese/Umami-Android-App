package com.example.giapan_beta

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Date

@Parcelize
data class ItemProdotto(
    val idProdotto: Int,
    val name: String,
    val price: Double,
    val categoria: String,
    //Oppure array di risorse?
    val resource: String,
    val starImage: Float = 0f,
    val description: String,
    val dataInserimento: Date,
    var nPezzi : Int,
    val dataRestock: Date,
    val nValutazioni : Int
): Parcelable {}
