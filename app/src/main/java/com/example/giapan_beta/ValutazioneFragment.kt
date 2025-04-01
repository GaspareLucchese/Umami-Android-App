package com.example.giapan_beta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.databinding.FragmentValutazioneBinding

class ValutazioneFragment : Fragment()
{
    private lateinit var binding: FragmentValutazioneBinding
    private val viewModel: ViewModelOggetto by activityViewModels()
    private lateinit var ordine : Ordine

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentValutazioneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordine = arguments?.getParcelable("ordine")!!
        Log.d("ValutazioneFragment", "Ordine ID ricevuto: ${ordine.idordine}")

        binding.valutazioneBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
        }

        binding.confermaBtn.setOnClickListener {
            val rating = binding.valutazioneBar.rating

            if(rating > 0)
            {
                viewModel.aggiungiValutazione(ordine.idordine, rating)
                viewModel.aggiornaValutazione(ordine.RefProdotto, rating)

                ordine.valutazioneOrdine = rating
                Log.d("ValutazioneFragment", "Ordine ID: ${ordine.idordine}, Rating: $rating")
                viewModel.updateOrdine(ordine)
                Toast.makeText(requireContext(), "Valutazione effettuata", Toast.LENGTH_SHORT).show()
            }

            //per aggiornare nel viewModel

            parentFragmentManager.popBackStack()
        }
    }
}