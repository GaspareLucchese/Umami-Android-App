package com.example.giapan_beta

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giapan_beta.databinding.FragmentCarrelloBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCarrelloBinding

    private lateinit var viewModel : ViewModelOggetto
    private lateinit var carrelloCustomAdapter : AdapterCarrello
    private lateinit var suggerimentoAdapter : AdapterHomePage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarrelloBinding.inflate(inflater, container, false)
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

        //riv
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelOggetto::class.java)

        initRecyclerView(gridCount)

        binding.checkout.setOnClickListener()
        {
            val totale = viewModel.prezzoTotale.value ?: 0.0
            if(totale > 0.0)
            {
                val pagamentoFragment = PagamentoFragment()

                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                fragmentTransaction.replace(R.id.container_homepage, pagamentoFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            else
            {
                Toast.makeText(context, "Selezionare almeno un prodotto per procedere", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.listaCarrello.observe(viewLifecycleOwner) { listaCarrello ->
            carrelloCustomAdapter.submitList(listaCarrello)
            viewModel.updateSuggestedProducts()
            if (listaCarrello.isEmpty())
            {
                binding.consigliatiText.visibility = View.GONE
                binding.nessunRisultatoTxt.visibility = View.VISIBLE
                binding.recycleCarrelloPotrebberoPiacerti.visibility = View.GONE
            }
            else
            {
                binding.consigliatiText.visibility = View.VISIBLE
                binding.nessunRisultatoTxt.visibility = View.GONE
            }
        }


        viewModel.suggestedProducts.observe(viewLifecycleOwner) { suggestedProducts ->
            suggerimentoAdapter.submitList(suggestedProducts)
            if (suggestedProducts.isEmpty())
            {
                binding.consigliatiText.visibility = View.GONE
                binding.recycleCarrelloPotrebberoPiacerti.visibility = View.GONE
            }
            else
            {
                binding.consigliatiText.visibility = View.VISIBLE
                binding.recycleCarrelloPotrebberoPiacerti.visibility = View.VISIBLE
                binding.nessunRisultatoTxt.visibility = View.GONE
            }
        }



        binding.selezioneTotale.setOnCheckedChangeListener { _, isChecked ->
            val listaCarrello = viewModel.listaCarrello.value ?: listOf()
            if (isChecked) {
                viewModel.selezionaTutto(listaCarrello)
            } else {
                viewModel.deselezionaTutto()
            }
            carrelloCustomAdapter.notifyDataSetChanged()
        }

        viewModel.listaCarrello.observe(viewLifecycleOwner) { listaCarrello ->
            carrelloCustomAdapter.notifyDataSetChanged()
            binding.selezioneTotale.setOnClickListener(null)
            binding.selezioneTotale.isChecked = listaCarrello.size == viewModel.listaCarrello.value?.size
            binding.selezioneTotale.setOnCheckedChangeListener { _, isChecked ->
                val itemList = viewModel.listaCarrello.value ?: listOf()
                if(isChecked)
                {
                    viewModel.selezionaTutto(itemList)
                }
                else{
                    viewModel.deselezionaTutto()
                }
                carrelloCustomAdapter.notifyDataSetChanged()
            }
        }

        viewModel.prezzoTotale.observe(viewLifecycleOwner) { prezzoTotale ->
            binding.euroTotal.text = String.format("€ %.2f", prezzoTotale)
        }


        suggerimentoAdapter.setOnClickListener(object : AdapterHomePage.OnClickListener {
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

    private fun initRecyclerView(gridCount : Int)
    {
        carrelloCustomAdapter = AdapterCarrello(emptyList(), viewModel)

        binding.recycleCarrello.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carrelloCustomAdapter
            setHasFixedSize(true)
        }

        suggerimentoAdapter = AdapterHomePage(emptyList())

        binding.recycleCarrelloPotrebberoPiacerti.apply {
            layoutManager = GridLayoutManager(context, gridCount)
            adapter = suggerimentoAdapter
            setHasFixedSize(true)
        }
    }
}