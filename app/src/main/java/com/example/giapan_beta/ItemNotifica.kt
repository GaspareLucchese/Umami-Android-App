package com.example.giapan_beta

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
data class ItemNotifica(val messaggio : String, val dataInvio : Date) : Parcelable {}
