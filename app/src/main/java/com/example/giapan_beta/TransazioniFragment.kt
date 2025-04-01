package com.example.giapan_beta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.databinding.FragmentDettagliTransazioniBinding

class TransazioniFragment : Fragment() {

    private lateinit var binding: FragmentDettagliTransazioniBinding
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDettagliTransazioniBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        binding.pagamentiTransazioniTitolo.setText(Html.fromHtml("<u>PAGAMENTI E TRANSAZIONI</u>"))

        if (email != null) {
            Log.d("TransazioniFragment", "Email trovata: $email")
            viewModel.getUserData(email)
        } else {
            Toast.makeText(requireContext(), "Email non trovata", Toast.LENGTH_SHORT).show()
        }


        viewModel.datiRegistrazione.observe(viewLifecycleOwner) { userData ->
            val scadenza = String.format("%02d/%02d", userData.meseScadenza, userData.annoScadenza)
            binding.datiCartaText.setText("${userData.intestatario.toUpperCase()}\n\n${mascheraCarta(userData.carta)}")
            binding.scadenzaCartaText.setText("SCADENZA ${scadenza}")
        }

        binding.modificaCartaButton.setOnClickListener()
        {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val modificaTransazioniFragment = ModificaTransazioniFragment()

            fragmentTransaction.replace(R.id.container_homepage, modificaTransazioniFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()

        }
    }

    fun mascheraCarta(carta : ULong) : String
    {
        val cartaStr = carta.toString()
        val last_four = cartaStr.takeLast(4)
        return "**** **** **** ${last_four}"
    }
}