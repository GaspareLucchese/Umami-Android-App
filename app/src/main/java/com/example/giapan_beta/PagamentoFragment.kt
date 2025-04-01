package com.example.giapan_beta

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giapan_beta.databinding.FragmentPagamentoBinding
import android.content.SharedPreferences
import android.text.Html
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels

class PagamentoFragment : Fragment() {

    private lateinit var binding: FragmentPagamentoBinding
    private lateinit var viewModel : ViewModelOggetto
    private val viewModelUtente: SharedViewModel by activityViewModels()
    private lateinit var pagamentoAdapter : AdapterPagamento
    private lateinit var sharedPref: SharedPreferences

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonStandard: RadioButton
    private lateinit var radioButtonExpress: RadioButton

    private var spesa : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPagamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //???
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelOggetto::class.java)

        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")

        if (email != null) {
            Log.d("ModificaProfiloFragment", "Email trovata: $email")
            viewModelUtente.getUserData(email)
        }

        binding.resocontoTitle.setText(Html.fromHtml("<u>DATI DI SPEDIZIONE E PAGAMENTO</u>"))

        viewModelUtente.datiRegistrazione.observe(viewLifecycleOwner) { userData ->
            binding.destinatario.setText("Per: ${userData.nome} ${userData.cognome}")
            binding.indirizzoSpedizione.setText(userData.indirizzo)
            binding.carta.setText(mascheraCarta(userData.carta))
        }

        radioGroup = binding.sceltaSpedizione
        radioButtonStandard = binding.spedizioneStandard
        radioButtonExpress = binding.spedizioneExpress

        radioButtonStandard.isChecked = true
        spesa  = viewModel.prezzoTotale.value ?: 0.0
        binding.resocontoPrezzo.text = String.format("€ %.2f", spesa)

        // Gestisci il cambiamento di selezione tra i RadioButton
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.spedizione_standard -> {
                    spesa = viewModel.prezzoTotale.value ?: 0.0
                }
                R.id.spedizione_express -> {
                    spesa = viewModel.prezzoTotale.value?.plus(5.0) ?: 0.0
                }
            }
            binding.resocontoPrezzo.text = String.format("€ %.2f", spesa)
        }

        initRecyclerView()

        viewModel.prodottiSelezionati.observe(viewLifecycleOwner) { selectedItems ->
            pagamentoAdapter.submitList(selectedItems)
        }

        val fragmentManager = parentFragmentManager
       binding.annullamentoBtn.setOnClickListener()
        {
            val pagamentoFragment = PagamentoFragment()
            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentManager.popBackStack()
            fragmentTransaction.remove(pagamentoFragment)
            fragmentTransaction.commit()
        }

        binding.confermaBtn.setOnClickListener()
        {
            Toast.makeText(context, "Il pagamento è stato effettuato", Toast.LENGTH_SHORT).show()

            if (email != null) {
                viewModelUtente.datiRegistrazione.value?.let { it1 -> viewModel.insertSelectedItems(email, it1.indirizzo) }
            }
            else
                Toast.makeText(context, "Errore", Toast.LENGTH_SHORT).show()
            viewModel.rimuoviSelezionati()

            val ordiniFragment = OrdiniFragment()
            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentManager.popBackStack()
            fragmentManager.popBackStack()
            fragmentTransaction.replace(R.id.container_homepage, ordiniFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun initRecyclerView()
    {
        pagamentoAdapter = AdapterPagamento(emptyList())

        binding.rvPagamento.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pagamentoAdapter
            setHasFixedSize(true)
        }

        /*
        viewModel.prezzoTotale.observe(viewLifecycleOwner) { prezzoTotale ->
            binding.resocontoPrezzo.text = String.format("€ %.2f", prezzoTotale)
        }

         */
    }

    fun mascheraCarta(carta : ULong) : String
    {
        val cartaStr = carta.toString()
        val last_four = cartaStr.takeLast(4)
        return "************${last_four}"
    }
}