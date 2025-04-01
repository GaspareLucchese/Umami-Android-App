package com.example.giapan_beta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.giapan_beta.databinding.FragmentFiltratoBinding
import com.example.giapan_beta.databinding.FragmentOrdiniBinding

class OrdiniFragment : Fragment(), AdapterConsegnato.OnItemClickListener {

    private lateinit var binding: FragmentOrdiniBinding
    private val viewModel: ViewModelOggetto by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    private lateinit var adapter: AdapterNonConsegnato
    private lateinit var adapter2: AdapterConsegnato

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdiniBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.consegnatiTxt.visibility = View.GONE
        binding.daConsegnareTxt.visibility = View.GONE
        binding.nessunRisultatoTxt.visibility = View.VISIBLE

        initRecycler()
    }

    private fun initRecycler()
    {
        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        binding.rvDaConsegnare.setHasFixedSize(true)
        binding.rvDaConsegnare.layoutManager = LinearLayoutManager(context)
        adapter = AdapterNonConsegnato(arrayListOf())

        binding.rvConsegnati.setHasFixedSize(true)
        binding.rvConsegnati.layoutManager = LinearLayoutManager(context)
        adapter2 = AdapterConsegnato(arrayListOf(), this)


        if (email != null) {
            viewModel.getProdottiConsegnati(email).observe(viewLifecycleOwner) { List ->
                if(List.isNotEmpty())
                {
                    binding.consegnatiTxt.visibility = View.VISIBLE
                    binding.rvConsegnati.visibility = View.VISIBLE
                    binding.rvConsegnati.adapter = AdapterConsegnato(List, this)
                    binding.nessunRisultatoTxt.visibility = View.GONE
                }
            }
            viewModel.getProdottiNonConsegnati(email).observe(viewLifecycleOwner){List ->
                if(List.isNotEmpty())
                {
                    binding.daConsegnareTxt.visibility = View.VISIBLE
                    binding.rvDaConsegnare.visibility = View.VISIBLE
                    binding.rvDaConsegnare.adapter = AdapterNonConsegnato(List)
                    binding.nessunRisultatoTxt.visibility = View.GONE
                }
            }
        }

    }

    override fun valutaOnClick(ordine : Ordine)
    {
        Log.d("OrdiniFragment", "Ordine ID da passare: ${ordine.idordine}")
        val valutazioneFragment = ValutazioneFragment()
        val bundle = Bundle()
        bundle.putParcelable("ordine", ordine)
        valutazioneFragment.arguments = bundle

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container_homepage, valutazioneFragment)
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()
    }

}