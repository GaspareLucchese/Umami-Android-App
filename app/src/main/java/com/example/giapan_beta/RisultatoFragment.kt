package com.example.giapan_beta

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.databinding.FragmentFiltratoBinding

class RisultatoFragment : Fragment()
{
    private lateinit var binding: FragmentFiltratoBinding
    private val viewModel: ViewModelOggetto by activityViewModels()

    private lateinit var customAdapter: AdapterHomePage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFiltratoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orientation = resources.configuration.orientation
        val gridCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            4
        } else {
            2
        }

        binding.rvRisultato.setHasFixedSize(true)
        binding.rvRisultato.layoutManager = GridLayoutManager(context, gridCount)
        customAdapter = AdapterHomePage(arrayListOf())


        when (viewModel.tipoLista)
        {
            "categoria" -> viewModel.getProdottiCategoria(viewModel.categoria).observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {

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
            "nuovi" -> viewModel.getNewsList().observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {
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
            "offerte" -> viewModel.getOffersList().observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {
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
            "bestseller" -> viewModel.getBestSellersList().observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {
                    setOnClickListener(object : AdapterHomePage.OnClickListener {
                        override fun onClick(position: Int, itemViewModel: ItemProdotto) {
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
            "restock" -> viewModel.getRestockList().observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {
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

            "ricerca" -> viewModel.getProdottiTramiteNome(viewModel.ricercaStr).observe(viewLifecycleOwner){List ->
                segnalaVuoto(List)
                binding.rvRisultato.adapter = AdapterHomePage(List).apply {
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

    }

    fun segnalaVuoto(list : List<ItemProdotto>)
    {
        if (list.size == 0)
        {

            binding.nessunRisultatoTxt.visibility = View.VISIBLE
        }
        else
        {
            binding.nessunRisultatoTxt.visibility = View.GONE
        }
    }
}
