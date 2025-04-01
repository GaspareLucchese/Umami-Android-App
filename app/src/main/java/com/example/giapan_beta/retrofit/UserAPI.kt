package com.example.giapan_beta.retrofit

import com.example.giapan_beta.ItemProdotto
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @POST(USER_URI)
    fun insertUtente( @Body body: JsonObject): Call<JsonObject>

    @GET(USER_URI)
    fun getUtenti(): Call<JsonArray>

    @GET("pwm/login")
    fun loginUser(@Query("email") email: String, @Query("password") password: String): Call<JsonObject>

    @GET("$USER_URI/email")
    fun getUserByEmail(@Query("email") email: String): Call<JsonObject>

    @PUT("$USER_URI/email/{email}")
    fun updateUserByEmail(@Path("email") email: String, @Body body: JsonObject): Call<JsonObject>

    @PUT("$USER_URI/transazioni/email/{email}")
    fun updateTransazioni(@Path("email") email: String, @Body body: JsonObject): Call<JsonObject>

    @GET("$USER_URI/indirizzo")
    fun getAddressByEmail(@Query("email") email: String): Call<String>


    /*@GET(ITEM_URI)
   fun getProdotti(): Call<JsonArray>*/

    @PUT("$ITEM_URI/idProdotto/{idProdotto}")
    fun updateNumeroPezzi(@Path("idProdotto") idProdotto: Int, @Body body: JsonObject): Call<JsonObject>

    @GET("$ITEM_URI/nuovi")
    fun getProdottiNuovi(): Call<JsonArray>

    @GET("$ITEM_URI/offerte")
    fun getProdottiOfferte(): Call<JsonArray>

    @GET("$ITEM_URI/bestsellers")
    fun getProdottiBestSellers(): Call<JsonArray>

    @GET("$ITEM_URI/restock")
    fun getProdottiRestock(): Call<JsonArray>

    @GET("$ITEM_URI/categoria")
    fun getProdottiCategoria(@Query("categoria") categoria: String): Call<JsonArray>

    @GET("$ITEM_URI/ricerca")
    fun getProdottiTramiteNome(@Query("name") name: String): Call<JsonArray>

    @GET("pwm/acquistaDiNuovo")
    fun getAcquistaDiNuovo(@Query("email") email: String): Call<JsonArray>

    @PUT("$ITEM_URI/idProdotto/{idProdotto}")
    fun updateRating(@Path("idProdotto") idProdotto: Int, @Body body: JsonObject): Call<JsonObject>


    @POST(ORDER_URI)
    fun insertOrdine(@Body body: JsonObject): Call<JsonObject>

    @GET("$ORDER_URI/nonconsegnati")
    fun getNonConsegnati(@Query("RefUtente") RefUtente: String): Call<JsonArray>

    @GET("$ORDER_URI/consegnati")
    fun getConsegnati(@Query("RefUtente") RefUtente: String): Call<JsonArray>

    @PUT("$ORDER_URI/idordine/{idordine}")
    fun updateValutazionePersonale(@Path("idordine") idordine: Int, @Body body: JsonObject): Call<JsonObject>


    @POST(ASSIST_URI)
    fun inviaMessaggioAssistenza(@Body json: JsonObject): Call<JsonObject>


    @POST(LIST_URI)
    fun aggiungiAllaListaDesideri(@Body body: JsonObject): Call<JsonObject>

    @GET(LIST_URI)
    fun getListaDesideri(@Query("email") email: String): Call<JsonArray>

    @DELETE("$LIST_URI/{email}/{idProdotto}")
    fun rimuoviDallaListaDesideri(@Path("email") email: String, @Path("idProdotto") idProdotto: Int): Call<JsonObject>

    @GET("$LIST_URI/verifica")
    fun verificaWishlist(@Query("RefUtenteLista") RefUtenteLista: String, @Query("RefProdottoLista") RefProdottoLista: Int): Call<JsonObject>


    @GET(NOTIFY_URI)
    fun getNotifiche(@Query("RefUtenteNotifica") RefUtenteNotifica: String): Call<JsonArray>

    @POST("$NOTIFY_URI/registrazione")
    fun aggiungiNotificaRegistrazione(@Body body: JsonObject): Call<JsonObject>


    @GET(FOTO_URI)
    fun getFoto(@Query("RefProdotto") RefProdotto: Int): Call<JsonArray>


    companion object {
        //10.0.2.2
        //192.168.146.249
        const val BASE_URL = "http://192.168.146.249:9000/"
        const val USER_URI = "pwm/utenti"
        const val ITEM_URI = "pwm/prodotti"
        const val ORDER_URI = "pwm/ordini"
        const val ASSIST_URI = "pwm/assistenze"
        const val LIST_URI = "pwm/liste"
        const val NOTIFY_URI = "pwm/notifiche"
        const val FOTO_URI = "pwm/foto"
    }

}
