package com.example.giapan_beta

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class DatiRegistrazione(
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val password: String = "",
    val indirizzo: String = "",
    val carta: ULong = 0uL,
    val intestatario: String = "",
    val meseScadenza: Int = 0,
    val annoScadenza: Int = 0,
    val codiceCvv: Int = 0
)

class SharedViewModel : ViewModel() {

    private val notificheList = MutableLiveData<ArrayList<ItemNotifica>>()

    private val _datiRegistrazione = MutableLiveData<DatiRegistrazione>()
    val datiRegistrazione: MutableLiveData<DatiRegistrazione> get() = _datiRegistrazione

    private val itemList = MutableLiveData<ArrayList<Utente>>()
    val success = MutableLiveData<Boolean>()

    init {
        this.getListaUtenti()
    }

    val utenteSelezionato = MutableLiveData<Utente?>()
    init {
        utenteSelezionato.value = null
    }

    init {
        success.value = false
    }

    fun addItem(item: Utente) {
        itemList.value?.add(item)
    }

    fun getListaUtenti(){
        Client.retrofit.getUtenti().enqueue(
            object : Callback<JsonArray> {
                override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                    if (response.isSuccessful){
                        val risposta: JsonArray? = response.body()
                        Log.d("risposta", risposta.toString())
                        itemList.value = risposta?.let { parseJsonToModel(it) }
                    }
                    else
                    {
                        Log.e("ViewModelOggetto", "Errore risposta non valida")
                    }
                }
                override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                    Log.e("ViewModelOggetto", "Errore: ${t.message}")
                }
            }
        )
    }

    fun insertItem(dati: DatiRegistrazione) {
        val gson = Gson()
        val string  =
            "{\"nome\": \"${dati.nome}\", \"cognome\": \"${dati.cognome}\", \"email\": \"${dati.email}\", \"indirizzo\" : \"${dati.indirizzo}\", \"password\" : \"${dati.password}\", \"carta\" : \"${dati.carta}\", \"intestatario\" : \"${dati.intestatario}\", \"meseScadenza\" : ${dati.meseScadenza}, \"annoScadenza\" : ${dati.annoScadenza}, \"codiceCvv\" : ${dati.codiceCvv}}"
        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertUtente(json).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(p0: Call<JsonObject>, p1: Response<JsonObject>) {
                    if (p1.isSuccessful) {
                        val risposta = JsonArray()
                        risposta.add(p1.body())
                        Log.v("risposta", risposta.toString())
                        addItem(parseJsonToModel(risposta)[0])
                        success.value = true
                    } else {
                        success.value = false
                    }
                }
                override fun onFailure(p0: Call<JsonObject>, p1: Throwable) {
                    success.value = false
                }
            }
        )
    }

    fun login(email: String, password: String) {
        Client.retrofit.loginUser(email, password).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.has("user")) {
                        val user = body.getAsJsonObject("user")
                        _datiRegistrazione.value = DatiRegistrazione(
                            nome = user.get("nome").asString,
                            cognome = user.get("cognome").asString,
                            email = user.get("email").asString,
                            indirizzo = user.get("indirizzo").asString

                            // Aggiungi altri campi se necessario
                        )
                        success.value = true
                    } else {
                        success.value = false
                    }
                } else {
                    success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                success.value = false
            }
        })
    }

    fun conferma() {
        val dati = _datiRegistrazione.value
        if (dati != null) {
            insertItem(dati)
        }
    }

    fun getUserData(email: String) {
        Client.retrofit.getUserByEmail(email).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        _datiRegistrazione.value = DatiRegistrazione(
                            nome = body.get("nome").asString,
                            cognome = body.get("cognome").asString,
                            email = body.get("email").asString,
                            indirizzo = body.get("indirizzo").asString,
                            password = body.get("password").asString,
                            intestatario = body.get("intestatario").asString,
                            carta = body.get("carta").asString.toULongOrNull() ?: 0uL,
                            meseScadenza = body.get("meseScadenza").asInt,
                            annoScadenza = body.get("annoScadenza").asInt,
                            codiceCvv = body.get("codiceCvv").asInt
                        )
                    }
                } else {
                    Log.e("SharedViewModel", "Errore nella risposta: ${response.errorBody()?.string()}")
                    success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("SharedViewModel", "Errore nella richiesta: ${t.message}")
                success.value = false
            }
        })
    }

    fun updateUserData(email: String, nome: String, cognome: String, indirizzo: String, password: String) {
        val updatedData = DatiRegistrazione(
            nome = nome,
            cognome = cognome,
            email = email,
            indirizzo = indirizzo,
            password = password
        )

        val jsonObject = JsonObject().apply {
            addProperty("nome", updatedData.nome)
            addProperty("cognome", updatedData.cognome)
            addProperty("email", updatedData.email)
            addProperty("indirizzo", updatedData.indirizzo)
            addProperty("password", updatedData.password)
        }

        Log.d("SharedViewModel", "Dati aggiornati: $jsonObject")

        Client.retrofit.updateUserByEmail(email, jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.has("error")) {
                            Log.e("SharedViewModel", "Errore nella risposta del server: ${body.get("error").asString}")
                            success.value = false
                        } else {
                            _datiRegistrazione.value = updatedData
                            success.value = true
                        }
                    }
                } else {
                    Log.e("SharedViewModel", "Errore nella risposta del server: ${response.errorBody()?.string()}")
                    success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("SharedViewModel", "Errore nella richiesta: ${t.message}")
                success.value = false
            }
        })
    }

    fun updateTransazioni(email: String, intestatario: String, carta: String, dataScadenza: String, codiceCvv: String) {
        val meseAnnoScadenza = dataScadenza.split("/")
        val meseScadenza = meseAnnoScadenza[0].toInt()
        val annoScadenza = meseAnnoScadenza[1].toInt()
        val cartaULong = carta.toULongOrNull() ?: 0uL
        val codiceCvvInt = codiceCvv.toIntOrNull() ?: 0

        val jsonObject = JsonObject().apply {
            addProperty("intestatario", intestatario)
            addProperty("carta", cartaULong.toString())
            addProperty("meseScadenza", meseScadenza)
            addProperty("annoScadenza", annoScadenza)
            addProperty("codiceCvv", codiceCvvInt)
        }

        Log.d("SharedViewModel", "Dati transazione aggiornati: $jsonObject")

        Client.retrofit.updateTransazioni(email, jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.has("error")) {
                            Log.e("SharedViewModel", "Errore nella risposta del server: ${body.get("error").asString}")
                            success.value = false
                        } else {
                            _datiRegistrazione.value = _datiRegistrazione.value?.copy(
                                intestatario = intestatario,
                                carta = cartaULong,
                                meseScadenza = meseScadenza,
                                annoScadenza = annoScadenza,
                                codiceCvv = codiceCvvInt
                            )
                            success.value = true
                        }
                    }
                } else {
                    Log.e("SharedViewModel", "Errore nella risposta del server: ${response.errorBody()?.string()}")
                    success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("SharedViewModel", "Errore nella richiesta: ${t.message}")
                success.value = false
            }
        })
    }

    fun inviaAssistenza(context: Context, email: String, messaggio: String) {
        val gson = Gson()
        val string = "{\"email\": \"$email\", \"messaggio\": \"$messaggio\"}"
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.inviaMessaggioAssistenza(json).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.v("risposta", response.body().toString())
                        mostraToast(context, "Messaggio inviato con successo")
                    } else {
                        Log.e("errore", response.errorBody()?.string().orEmpty())
                        mostraToast(context, "Errore nell'invio del messaggio")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("errore di rete", t.message.orEmpty())
                    mostraToast(context, "Errore di rete: ${t.message}")
                }
            }
        )
    }

    fun getNotifiche(RefUtenteNotifica: String): LiveData<ArrayList<ItemNotifica>> {
        Client.retrofit.getNotifiche(RefUtenteNotifica).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Full Response", body.toString())
                    if (body != null) {
                        Log.d("risposta ORDER", body.toString())
                        notificheList.value = parseJsonToModelToNotifica(body)
                    } else {
                        Log.e("ViewModelOggetto", "Errore: risposta BODY NULL")
                    }
                } else {
                    Log.e("ViewModelOggetto", "Errore risposta non valida: codice di stato ${response.code()} - body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore FAILURE: ${t.message}")
            }
        })
        return notificheList
    }


    fun aggiungiNotificaRegistrazione(email:String){
        val gson = Gson()
        val string = "{\"email\": \"$email\"}"
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.aggiungiNotificaRegistrazione(json).enqueue(
            object :Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        Log.v("risposta", response.body().toString())
                    }
                    else{
                        Log.e("errore", response.errorBody()?.string().orEmpty())
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("errore di rete", t.message.orEmpty())
                }
            }

        )
    }

    private fun parseJsonToModelToNotifica(jsonArray: JsonArray): ArrayList<ItemNotifica> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val itemList = ArrayList<ItemNotifica>()
        for (jsonElement in jsonArray) {
            val item = gson.fromJson(jsonElement, ItemNotifica::class.java)
            itemList.add(item)
        }
        return itemList
    }

    private fun mostraToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Utente>>() {}.type)
    }


}