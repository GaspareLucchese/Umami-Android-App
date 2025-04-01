package com.example.giapan_beta

import android.content.Context
import android.content.SharedPreferences
import android.widget.Button
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giapan_beta.databinding.FragmentProfiloBinding

class ProfileFragment : Fragment()
{
    private lateinit var binding: FragmentProfiloBinding
    private val viewModel: ViewModelOggetto by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    private lateinit var rv1: RecyclerView
    private lateinit var rv2: RecyclerView

    private lateinit var customAdapter : AdapterHomePage
    private lateinit var lista: ArrayList<ItemProdotto>
    private lateinit var adapterLista: AdapterLista

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val profilo = sharedPref.getString("profilo", "")

        if (email != null) {
            Log.d("ProfileFragment", "Email found: $email")
            viewModel.getListaDesideri(email).observe(viewLifecycleOwner) { lista ->
                if (lista != null && lista.isNotEmpty()) {
                    adapterLista.updateList(lista)
                    binding.nessunRisultatoTxt.visibility = View.GONE
                    binding.recicleLista.visibility = View.VISIBLE
                } else {
                    binding.nessunRisultatoTxt.visibility = View.VISIBLE
                    binding.recicleLista.visibility = View.GONE
                }
            }
        } else {
            Log.d("ProfileFragment", "Email is null or empty")
        }

        binding.name.text = profilo


        binding.iMieiOrdiniBtn.setOnClickListener()
        {
            val ordiniFragment = OrdiniFragment()

            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.container_homepage, ordiniFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

        binding.impostazioniBtn.setOnClickListener()
        {
            val impostazioniFragment = ImpostazioniFragment()

            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.container_homepage, impostazioniFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        if (email != null) {
            init(view, email)
        }

        customAdapter.setOnClickListener(object : AdapterHomePage.OnClickListener {
            override fun onClick(position: Int, itemViewModel: ItemProdotto) {
                val prodottoFragment = ProdottoFragment()


                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()


                fragmentTransaction.replace(R.id.container_homepage, prodottoFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        })

    }

    fun init(view: View, email : String)
    {
        binding.recicleOrdini.setHasFixedSize(true)
        binding.recicleOrdini.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        customAdapter = AdapterHomePage(arrayListOf())

        viewModel.getProdottiAcquistati(email).observe(viewLifecycleOwner) { List ->
            if (List.isEmpty())
            {
                binding.recicleOrdini.visibility = View.GONE
                binding.acquistaDiNuovoTxt.visibility = View.GONE
            }
            else
            {
                binding.recicleOrdini.visibility = View.VISIBLE
                binding.acquistaDiNuovoTxt.visibility = View.VISIBLE

                binding.recicleOrdini.adapter = AdapterHomePage(List).apply {
                    setOnClickListener(object : AdapterHomePage.OnClickListener
                    {
                        override fun onClick(position: Int, itemViewModel: ItemProdotto)
                        {
                            viewModel.prodottoSelezionato.value = itemViewModel
                            viewModel.posizione.value = position
                            val prodottoFragment = ProdottoFragment()

                            val fragmentManager = parentFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()

                            fragmentTransaction.replace(R.id.container_homepage, prodottoFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                    })
                }
            }
        }



        rv2 = view.findViewById<RecyclerView>(R.id.recicleLista)
        rv2.setHasFixedSize(true)
        rv2.layoutManager = LinearLayoutManager(context)
        lista = ArrayList()



        adapterLista = AdapterLista(lista, viewModel, email, parentFragmentManager)
        rv2.adapter = adapterLista
    }
}