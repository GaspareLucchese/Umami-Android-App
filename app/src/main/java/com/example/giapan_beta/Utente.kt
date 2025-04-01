package com.example.giapan_beta

data class Utente
    (val idUtente : Int,
     val nome: String,
     val cognome : String,
     val email : String,
     val indirizzo : String,
     val password : String,
     val carta : ULong,
     val intestatario : String,
     val meseScadenza : Int,
     val annoScadenza : Int,
     val codiceCvv : Int)
{}