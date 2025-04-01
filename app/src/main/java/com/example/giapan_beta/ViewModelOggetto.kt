package com.example.giapan_beta

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class DatiOrdine(
    val email: String = "",
    val idprodotto: Int = 0,
    val prezzoProdotto : Double = 0.0,
    val indirizzoUtente : String = "",
    val resourceProdotto : String = "",
)

class ViewModelOggetto : ViewModel() {

    var categoria : String = ""
    var ricercaStr : String = ""
    val success = MutableLiveData<Boolean>()

    var tipoLista : String? = null

    private val itemList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val newsList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val offersList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val restockList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val bestSellersList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val categoryList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val acquistatiList = MutableLiveData<ArrayList<ItemProdotto>>()
    private val notDeliveredList = MutableLiveData<ArrayList<Ordine>>()
    private val deliveredList = MutableLiveData<ArrayList<Ordine>>()

    val prodottoSelezionato = MutableLiveData<ItemProdotto?>().apply { value = null }
    val posizione = MutableLiveData<Int>().apply { value = -1 }

    private val _prodottiSelezionati = MutableLiveData<MutableList<ItemProdotto>>(mutableListOf())
    val prodottiSelezionati: MutableLiveData<MutableList<ItemProdotto>> = _prodottiSelezionati
    val prezzoTotale = MutableLiveData<Double>(0.0)

    fun getNewsList(): LiveData<ArrayList<ItemProdotto>> = newsList
    fun getOffersList(): LiveData<ArrayList<ItemProdotto>> = offersList
    fun getRestockList(): LiveData<ArrayList<ItemProdotto>> = restockList
    fun getBestSellersList(): LiveData<ArrayList<ItemProdotto>> = bestSellersList

    init {
        prodottiSelezionati.value = mutableListOf()
        updateListeHomepage()
        success.value = false
    }

    fun updateListeHomepage()
    {
        getListaNuoviProdotti()
        getListaBestSellersProdotti()
        getListaOfferteProdotti()
        getListaRestockProdotti()
    }


    fun getListaNuoviProdotti() {
        Client.retrofit.getProdottiNuovi().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val risposta: JsonArray? = response.body()
                    Log.d("risposta", risposta.toString())
                    newsList.value = risposta?.let { parseJsonToModel(it) }
                } else {
                    Log.e("ViewModelOggetto", "Errore risposta non valida")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun getListaOfferteProdotti() {
        Client.retrofit.getProdottiOfferte().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val risposta: JsonArray? = response.body()
                    Log.d("risposta", risposta.toString())
                    offersList.value = risposta?.let { parseJsonToModel(it) }
                } else {
                    Log.e("ViewModelOggetto", "Errore risposta non valida")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun getListaBestSellersProdotti() {
        Client.retrofit.getProdottiBestSellers().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val risposta: JsonArray? = response.body()
                    Log.d("risposta", risposta.toString())
                    bestSellersList.value = risposta?.let { parseJsonToModel(it) }
                } else {
                    Log.e("ViewModelOggetto", "Errore risposta non valida")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun getListaRestockProdotti() {
        Client.retrofit.getProdottiRestock().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val risposta: JsonArray? = response.body()
                    Log.d("risposta", risposta.toString())
                    restockList.value = risposta?.let { parseJsonToModel(it) }
                } else {
                    Log.e("ViewModelOggetto", "Errore risposta non valida")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun getProdottiCategoria(category: String): LiveData<ArrayList<ItemProdotto>> {
        Client.retrofit.getProdottiCategoria(category).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Full Response", body.toString())
                    if (body != null) {
                        Log.d("risposta ITEM", body.toString())
                        categoryList.value = parseJsonToModelT(body)
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
        return categoryList
    }


    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<ItemProdotto> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<ItemProdotto>>() {}.type)
    }


    private fun parseJsonToModelT(jsonArray: JsonArray): ArrayList<ItemProdotto> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val itemList = ArrayList<ItemProdotto>()
        for (jsonElement in jsonArray) {
            val item = gson.fromJson(jsonElement, ItemProdotto::class.java)
            itemList.add(item)
        }
        return itemList
    }

    private val _listaCarrello = MutableLiveData<MutableList<ItemProdotto>>(mutableListOf())
    val listaCarrello: MutableLiveData<MutableList<ItemProdotto>> = _listaCarrello

    val quantitaProdotti = mutableMapOf<Int, Int>()

    fun aggiungiAlCarrello(prodotto: ItemProdotto) {
        val currentList = _listaCarrello.value ?: mutableListOf()
        val prodottoID = prodotto.idProdotto

        val quantitaAttuale = quantitaProdotti[prodottoID] ?: 0
        if(quantitaAttuale < prodotto.nPezzi)
        {
            currentList.add(prodotto)
            _listaCarrello.value = currentList
            quantitaProdotti[prodottoID] = quantitaAttuale + 1
        }
    }

    private val _suggestedProducts = MutableLiveData<List<ItemProdotto>>()
    val suggestedProducts: LiveData<List<ItemProdotto>> get() = _suggestedProducts

    fun updateSuggestedProducts() {
        val categoriaCountMap = getCategoriaCountMap()
        val mostCommonCategory = categoriaCountMap.maxByOrNull { it.value }?.key

        if (mostCommonCategory != null) {
            getProdottiCategoria(mostCommonCategory).observeForever { productList ->
                val currentList = _listaCarrello.value ?: emptyList()
                val currentProductIds = currentList.map { it.idProdotto }
                val filteredProductList = productList.filter { it.idProdotto !in currentProductIds }

                // Prendi 12 prodotti casuali dalla lista filtrata
                _suggestedProducts.value = filteredProductList.shuffled().take(12)
            }
        }
    }

    // La funzione getCategoriaCountMap() rimane la stessa
    fun getCategoriaCountMap(): MutableMap<String, Int> {
        val currentList = _listaCarrello.value ?: mutableListOf()
        val categorieMap = currentList.groupingBy { it.categoria }.eachCount()
        val mutableCategorieMap = categorieMap.toMutableMap()
        return mutableCategorieMap
    }

    fun selezionaOggetto(item: ItemProdotto, isSelected: Boolean)
    {
        val selezionato = prodottiSelezionati.value ?: mutableListOf()
        if(isSelected)
        {
            selezionato.add(item)
        }
        else
        {
            selezionato.remove(item)
        }
        prodottiSelezionati.value = selezionato
        aggiornaPrezzoTotale()
    }

    fun selezionaTutto(items: List<ItemProdotto>)
    {
        prodottiSelezionati.value = listaCarrello.value?.toMutableList()
        aggiornaPrezzoTotale()
    }

    fun deselezionaTutto()
    {
        prodottiSelezionati.value = mutableListOf()
        prezzoTotale.value = 0.0
    }

    fun rimuoviSelezionati()
    {
        val currentList = listaCarrello.value ?: mutableListOf()
        val selezionato = prodottiSelezionati.value ?: listOf()
        currentList.removeAll(selezionato)
        listaCarrello.value = currentList
        deselezionaTutto()
    }

    private fun aggiornaPrezzoTotale()
    {
        prezzoTotale.value = prodottiSelezionati.value?.sumOf { it.price } ?: 0.0
    }


    fun rimuoviCarrello(prodotto: ItemProdotto)
    {
        val currentItems = listaCarrello.value ?: mutableListOf()
        currentItems.remove(prodotto)
        listaCarrello.value = currentItems
        prodottiSelezionati.value?.remove(prodotto)
        val prodottoID = prodotto.idProdotto
        val quantitaAttuale = quantitaProdotti[prodottoID] ?: 0
        quantitaProdotti[prodottoID] = quantitaAttuale - 1
        aggiornaPrezzoTotale()
    }

    private val orderList = MutableLiveData<ArrayList<Ordine>>()

    fun insertSelectedItems(email: String, address: String) {
        prodottiSelezionati.value?.forEach { item ->
            updatePezziProdotti(item.idProdotto, item.nPezzi)
            Log.d("datiOrdine", "Indirizzo: ${address}")
            val datiOrdine = DatiOrdine(email, item.idProdotto, item.price, address) // Crea un oggetto DatiOrdine
            insertOrdine(datiOrdine)
            updatePezziProdotti(item.idProdotto, item.nPezzi)
            item.nPezzi -= 1
        }
    }

    fun insertOrdine(dati: DatiOrdine) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("RefUtente", dati.email)
        jsonObject.addProperty("RefProdotto", dati.idprodotto)
        jsonObject.addProperty("indirizzo", dati.indirizzoUtente)
        jsonObject.addProperty("price", dati.prezzoProdotto)
        jsonObject.addProperty("resource", dati.resourceProdotto)

        Client.retrofit.insertOrdine(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(p0: Call<JsonObject>, p1: Response<JsonObject>) {
                    if (p1.isSuccessful) {
                        val risposta = JsonArray()
                        risposta.add(p1.body())
                        Log.v("risposta ordine", risposta.toString())
                        addOrder(parseJsonToModelOrdine(risposta)[0])
                        success.value = true
                    } else {
                        success.value = false
                        Log.e("Retrofit Error", "Errore nella risposta del server")
                    }
                }
                override fun onFailure(p0: Call<JsonObject>, p1: Throwable) {
                    success.value = false
                    Log.e("Retrofit Error", "Errore nella chiamata Retrofit: ${p1.message}")
                }
            }
        )
    }

    private fun parseJsonToModelOrdine(jsonString: JsonArray): ArrayList<Ordine> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Ordine>>() {}.type)
    }

    private fun parseJsonToModelTOrdine(jsonArray: JsonArray): ArrayList<Ordine> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val itemList = ArrayList<Ordine>()
        for (jsonElement in jsonArray) {
            val item = gson.fromJson(jsonElement, Ordine::class.java)
            itemList.add(item)
        }
        return itemList
    }

    fun addOrder(item: Ordine) {
        orderList.value?.add(item)
    }

    fun getProdottiNonConsegnati(RefUtente : String): LiveData<ArrayList<Ordine>> {
        Client.retrofit.getNonConsegnati(RefUtente).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Full Response", body.toString())
                    if (body != null) {
                        Log.d("risposta ORDER", body.toString())
                        notDeliveredList.value = parseJsonToModelTOrdine(body)
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
        return notDeliveredList
    }

    fun getProdottiConsegnati(RefUtente: String): LiveData<ArrayList<Ordine>> {
        Client.retrofit.getConsegnati(RefUtente).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Full Response", body.toString())
                    if (body != null) {
                        Log.d("risposta ORDER", body.toString())
                        deliveredList.value = parseJsonToModelTOrdine(body)
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
        return deliveredList
    }

    fun getProdottiAcquistati(email: String): LiveData<ArrayList<ItemProdotto>> {
        Client.retrofit.getAcquistaDiNuovo(email).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Full Response", body.toString())
                    if (body != null) {
                        Log.d("risposta ITEM", body.toString())
                        acquistatiList.value = parseJsonToModelT(body)
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
        return acquistatiList
    }

    private val _prodottiRisultatoRicerca = MutableLiveData<List<ItemProdotto>>()
    val prodottiRisultatoRicerca: LiveData<List<ItemProdotto>> get() = _prodottiRisultatoRicerca

    fun getProdottiTramiteNome(name: String): LiveData<List<ItemProdotto>> {
        val client = Client.retrofit
        val call = client.getProdottiTramiteNome(name)

        call.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val jsonArray = response.body()
                    val gson = GsonBuilder().create()
                    val itemType = object : TypeToken<List<ItemProdotto>>() {}.type
                    val itemList: List<ItemProdotto> = gson.fromJson(jsonArray, itemType)
                    _prodottiRisultatoRicerca.value = itemList
                } else {
                    Log.e("ViewModelOggetto", "Errore nella risposta:  ${response.message()}")
                    _prodottiRisultatoRicerca.value = emptyList()
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore di rete: ${t.message}")
                _prodottiRisultatoRicerca.value = emptyList()
            }
        })

        return prodottiRisultatoRicerca
    }


    fun updateOrdine(ordine: Ordine) {
        val currentList = deliveredList.value ?: arrayListOf()  // Ottiene il valore corrente di deliveredList
        val index = currentList.indexOfFirst { it.idordine == ordine.idordine }
        if (index != -1) {
            currentList[index] = ordine  // Modifica l'ordine nella lista corrente
            deliveredList.value = currentList  // Aggiorna il valore di deliveredList con la nuova lista modificata
        }
    }

    fun aggiungiValutazione(idordine: Int, valutazioneOrdine: Float) {
        val jsonObject = JsonObject().apply {
            addProperty("idordine", idordine)
            addProperty("valutazioneOrdine", valutazioneOrdine)
        }

        Log.d("SharedViewModel", "Dati aggiornati: $jsonObject")

        Client.retrofit.updateValutazionePersonale(idordine, jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.has("error")) {
                            Log.e("SharedViewModel", "Errore nella risposta del server: ${body.get("error").asString}")
                            success.value = false
                        } else if (body.has("message")) {
                            val message = body.get("message").asString
                            Log.i("SharedViewModel", message)
                            success.value = true
                        } else {
                            Log.e("SharedViewModel", "Risposta inattesa del server: $body")
                            success.value = false
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



    fun aggiornaValutazione(idProdotto: Int, starImage: Float) {
        val jsonObject = JsonObject().apply {
            addProperty("idProdotto", idProdotto)
            addProperty("starImage", starImage)
        }

        Log.d("SharedViewModel", "Dati aggiornati: $jsonObject")

        Client.retrofit.updateRating(idProdotto, jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.has("error")) {
                            Log.e("SharedViewModel", "Errore nella risposta del server: ${body.get("error").asString}")
                            success.value = false
                        } else if (body.has("message")) {
                            val message = body.get("message").asString
                            Log.i("SharedViewModel", message)
                            success.value = true
                        } else {
                            Log.e("SharedViewModel", "Risposta inattesa del server: $body")
                            success.value = false
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



    fun updatePezziProdotti(idProdotto: Int, nPezzi : Int) {

        val jsonObject = JsonObject().apply {
            addProperty("idProdotto", idProdotto)
            addProperty("nPezzi", nPezzi)
        }

        Log.d("SharedViewModel", "Dati aggiornati: $jsonObject")

        Client.retrofit.updateNumeroPezzi(idProdotto, jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.has("error")) {
                            Log.e("SharedViewModel", "Errore nella risposta del server: ${body.get("error").asString}")
                            success.value = false
                        } else {
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

    fun getIndirizzo(email: String): String {
        val call = Client.retrofit.getAddressByEmail(email)
        return try {
            val response: Response<String> = call.execute()
            if (response.isSuccessful) {
                val indirizzo = response.body() ?: ""
                Log.d("SharedViewModel", "Indirizzo ricevuto: $indirizzo") // Log per l'indirizzo ricevuto
                indirizzo
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("SharedViewModel", "Errore nella risposta: $errorBody")
                ""
            }
        } catch (e: Exception) {
            Log.e("SharedViewModel", "Errore nella richiesta: ${e.message}", e) // Log con l'eccezione completa
            ""
        }
    }

    private val _listaDesideri = MutableLiveData<List<ItemProdotto>>()
    val listaDesideri: LiveData<List<ItemProdotto>> = _listaDesideri

    fun aggiungiAllaListaDesideri(email: String, idProdotto: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("email", email)
            addProperty("idProdotto", idProdotto)
        }

        Client.retrofit.aggiungiAllaListaDesideri(jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d("ViewModelOggetto", "Prodotto aggiunto alla lista dei desideri")
                } else {
                    Log.e("ViewModelOggetto", "Errore aggiunta alla lista dei desideri")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun getListaDesideri(email: String): LiveData<List<ItemProdotto>> {
        val client = Client.retrofit
        val call = client.getListaDesideri(email)

        call.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val jsonArray = response.body()
                    val gson = GsonBuilder().create()
                    val itemType = object : TypeToken<List<ItemProdotto>>() {}.type
                    val itemList: List<ItemProdotto> = gson.fromJson(jsonArray, itemType)
                    _listaDesideri.value = itemList
                } else {
                    Log.e("ViewModelOggetto", "Errore nella risposta: ${response.message()}")
                    _listaDesideri.value = emptyList()  // Aggiungere questa linea per gestire i casi di errore
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore di rete: ${t.message}")
                _listaDesideri.value = emptyList()  // Aggiungere questa linea per gestire i casi di errore
            }
        })

        return listaDesideri
    }

    fun rimuoviDallaListaDesideri(email: String, idProdotto: Int) {
        Client.retrofit.rimuoviDallaListaDesideri(email, idProdotto).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _listaDesideri.value = _listaDesideri.value?.filter { it.idProdotto != idProdotto }
                    Log.d("ViewModelOggetto", "Prodotto rimosso dalla lista dei desideri")
                } else {
                    Log.e("ViewModelOggetto", "Errore rimozione dalla lista dei desideri")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore: ${t.message}")
            }
        })
    }

    fun verificaPresenzaLista(email: String, idProdotto: Int) {
        Client.retrofit.verificaWishlist(email, idProdotto).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.has("wishlist")) {
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

    private val _fotoList = MutableLiveData<List<ItemFoto>>()
    val fotoList: LiveData<List<ItemFoto>> get() = _fotoList

    fun getFoto(refProdotto: Int) {
        Client.retrofit.getFoto(refProdotto).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val fotoItems = parseJsonToModelToFoto(body)
                        _fotoList.value = fotoItems
                    } else {
                        Log.e("ViewModelOggetto", "Errore: risposta BODY NULL")
                    }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("ViewModelOggetto", "Errore FAILURE: ${t.message}")
            }
        })
    }

    private fun parseJsonToModelToFoto(jsonArray: JsonArray): ArrayList<ItemFoto> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val itemList = ArrayList<ItemFoto>()
        for (jsonElement in jsonArray) {
            val item = gson.fromJson(jsonElement, ItemFoto::class.java)
            itemList.add(item)
        }
        return itemList
    }

}
