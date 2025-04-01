package com.example.giapan_beta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.databinding.FragmentOggettoBinding

class ProdottoFragment : Fragment()
{
    private lateinit var binding: FragmentOggettoBinding
    private lateinit var sharedPref: SharedPreferences

    private lateinit var fotoAdapter: AdapterFoto

    private val viewModel: ViewModelOggetto by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOggettoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val prodotto = viewModel.prodottoSelezionato
        val idProdotto = viewModel.prodottoSelezionato.value?.idProdotto

        if (idProdotto != null) {
            viewModel.getFoto(idProdotto)
        }

        if(email != null && prodotto.value != null)
        {
            viewModel.verificaPresenzaLista(email, prodotto.value!!.idProdotto)
        }

        viewModel.success.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                binding.aggiungiAllaListaBtn.visibility = View.GONE
                binding.aggiuntoAllaListaBtn.visibility = View.VISIBLE
            } else {
                binding.aggiungiAllaListaBtn.visibility = View.VISIBLE
                binding.aggiuntoAllaListaBtn.visibility = View.GONE

            }
        }

        binding.addCartBtn.setOnClickListener {
            Log.v("HomeFragment", "Button clicked")
            val prodotto = viewModel.prodottoSelezionato.value
            if (prodotto != null) {
                val quantitaAttuale = viewModel.quantitaProdotti[prodotto.idProdotto] ?: 0
                if (quantitaAttuale < prodotto.nPezzi) {
                    viewModel.aggiungiAlCarrello(prodotto)
                    Toast.makeText(context, "Prodotto aggiunto al Carrello", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Quantità massima raggiunta per questo prodotto", Toast.LENGTH_SHORT).show()
                }
            }
        }



        viewModel.prodottoSelezionato.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.nomeOggetto.setText(it.name)
                binding.prezzoOggetto.setText("€ ${it.price}")
                binding.valutazione.setRating(it.starImage)
                binding.descrizioneOggetto.setText(it.description)
                binding.stock.setText("${it.nPezzi} pezzi rimasti")
                Log.d("UpdateFragment", "onViewCreated: $it")
            }
        }

        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        binding.aggiungiAllaListaBtn.setOnClickListener {
            var email = sharedPref.getString("email", "Email non trovata")
            val prodotto = viewModel.prodottoSelezionato.value
            if (prodotto != null && !email.isNullOrEmpty()) {

                viewModel.aggiungiAllaListaDesideri(email, prodotto.idProdotto)
                Toast.makeText(context, "Prodotto aggiunto alla lista dei desideri", Toast.LENGTH_SHORT).show()
                binding.aggiungiAllaListaBtn.visibility = View.GONE
                binding.aggiuntoAllaListaBtn.visibility = View.VISIBLE

            } else {
                Toast.makeText(context, "Errore: Prodotto o email non disponibile", Toast.LENGTH_SHORT).show()
            }


        }

        binding.aggiuntoAllaListaBtn.setOnClickListener {
            var email = sharedPref.getString("email", "Email non trovata")
            val prodotto = viewModel.prodottoSelezionato.value
            try {
                if (email != null && prodotto != null) {
                    viewModel.rimuoviDallaListaDesideri(email, prodotto.idProdotto)
                    Toast.makeText(context, "Prodotto rimosso dalla lista dei desideri", Toast.LENGTH_SHORT).show()
                    binding.aggiungiAllaListaBtn.visibility = View.VISIBLE
                    binding.aggiuntoAllaListaBtn.visibility = View.GONE

                    Log.d("AdapterLista", "Prodotto rimosso: ${prodotto.name}")
                }
            } catch (e: Exception) {
                Log.e("AdapterLista", "Errore durante la rimozione del prodotto", e)
            }
        }

        viewModel.prodottoSelezionato.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.nomeOggetto.setText(it.name)
                binding.prezzoOggetto.setText("€ ${it.price}")
                binding.valutazione.setRating(it.starImage)
                binding.descrizioneOggetto.setText(it.description)
                binding.stock.setText("${it.nPezzi} pezzi disponibili")
            }
        }

        initRecyclerView()

        viewModel.fotoList.observe(viewLifecycleOwner) { fotoList ->
            fotoAdapter.updateData(fotoList)
        }
    }

    private fun initRecyclerView() {
        fotoAdapter = AdapterFoto(emptyList(), viewModel)

        binding.rvFoto.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = fotoAdapter
            setHasFixedSize(true)

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
    }
}