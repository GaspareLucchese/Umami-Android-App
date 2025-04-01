package com.example.giapan_beta

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.giapan_beta.databinding.ActivityHomepageBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding
    private lateinit var viewModel: ViewModelOggetto

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollPosition = 0

    private lateinit var banners: ArrayList<ItemBanner>
    private lateinit var adapterBanner: AdapterBanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.bannerCollections.visibility = View.GONE
        } else {
            binding.bannerCollections.visibility = View.VISIBLE
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        Log.d("HomePageActivity", "Retrieved email: $email")

        if (isLoggedIn && email != null) {
            Log.d("HomePageActivity", "Email: $email")
        } else {
            Log.e("HomePageActivity", "User is not logged in or email is null")
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(ViewModelOggetto::class.java)

        binding.homeBtn.setOnClickListener {
            supportFragmentManager.apply {
                for (fragment in fragments) {
                    beginTransaction().remove(fragment).commit()
                }
                popBackStack()
                viewModel.updateListeHomepage()
            }
        }

        binding.categoryBtn.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CategoriaFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.cartBtn.setOnClickListener {
            viewModel.updateSuggestedProducts()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CartFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.profileBtn.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ProfileFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.notifyBtn.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<NotificheFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.newsBtn.setOnClickListener {
            Toast.makeText(this, "Nuovi Prodotti", Toast.LENGTH_SHORT).show()
            viewModel.tipoLista = "nuovi"
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RisultatoFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }
        binding.offersBtn.setOnClickListener {
            Toast.makeText(this, "Prodotti in Offerta", Toast.LENGTH_SHORT).show()
            viewModel.tipoLista = "offerte"
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RisultatoFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.bestSellersBtn.setOnClickListener {
            Toast.makeText(this, "Prodotti più venduti", Toast.LENGTH_SHORT).show()
            viewModel.tipoLista = "bestseller"
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RisultatoFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        binding.restockBtn.setOnClickListener {
            Toast.makeText(this, "Prodotti nuovamente in vendita", Toast.LENGTH_SHORT).show()
            viewModel.tipoLista = "restock"
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RisultatoFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }

        //riv
        binding.ricercaText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                eseguiRicerca(binding.ricercaText.text.toString())
                true
            } else {
                false
            }
        }

        init()

        startAutoScroll()

        observeViewModel()
    }

    private fun init() {
        banners = ArrayList()

        binding.bannerCollections.setHasFixedSize(true)
        binding.bannerCollections.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.bannerCollections)
        addDataToList()

        adapterBanner = AdapterBanner(banners)
        binding.bannerCollections.adapter = adapterBanner

        binding.newsRv.setHasFixedSize(true)
        binding.newsRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.offersRv.setHasFixedSize(true)
        binding.offersRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.restockRv.setHasFixedSize(true)
        binding.restockRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.bestSellersRv.setHasFixedSize(true)
        binding.bestSellersRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }


    private fun observeViewModel() {
        viewModel.getNewsList().observe(this) { newsList ->
            val limitedNewsList = newsList.take(10)
            binding.newsRv.adapter = AdapterHomePage(limitedNewsList).apply {
                setOnClickListener(object : AdapterHomePage.OnClickListener {
                    override fun onClick(position: Int, itemViewModel: ItemProdotto) {
                        viewModel.prodottoSelezionato.value = itemViewModel
                        viewModel.posizione.value = position
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<ProdottoFragment>(R.id.container_homepage)
                            addToBackStack("main")
                        }
                    }
                })
            }
        }


        viewModel.getOffersList().observe(this) { offersList ->
            val limitedOffersList = offersList.take(10)
            binding.offersRv.adapter = AdapterHomePage(limitedOffersList).apply {
                setOnClickListener(object : AdapterHomePage.OnClickListener {
                    override fun onClick(position: Int, itemViewModel: ItemProdotto) {
                        viewModel.prodottoSelezionato.value = itemViewModel
                        viewModel.posizione.value = position
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<ProdottoFragment>(R.id.container_homepage)
                            addToBackStack("main")
                        }
                    }
                })
            }
        }

        viewModel.getRestockList().observe(this) { restockList ->
            val limitedRestockList = restockList.take(10)
            binding.restockRv.adapter = AdapterHomePage(limitedRestockList).apply {
                setOnClickListener(object : AdapterHomePage.OnClickListener {
                    override fun onClick(position: Int, itemViewModel: ItemProdotto) {
                        viewModel.prodottoSelezionato.value = itemViewModel
                        viewModel.posizione.value = position
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<ProdottoFragment>(R.id.container_homepage)
                            addToBackStack("main")
                        }
                    }
                })
            }
        }

        viewModel.getBestSellersList().observe(this) { bestSellersList ->
            val limitedBestSellersList = bestSellersList.take(10)
            binding.bestSellersRv.adapter = AdapterHomePage(limitedBestSellersList).apply {
                setOnClickListener(object : AdapterHomePage.OnClickListener {
                    override fun onClick(position: Int, itemViewModel: ItemProdotto) {
                        viewModel.prodottoSelezionato.value = itemViewModel
                        viewModel.posizione.value = position
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<ProdottoFragment>(R.id.container_homepage)
                            addToBackStack("main")
                        }
                    }
                })
            }
        }
    }

    private fun eseguiRicerca(valore : String)
    {
        viewModel.ricercaStr = valore
        if(viewModel.ricercaStr.isNotEmpty())
        {
            viewModel.tipoLista = "ricerca"
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RisultatoFragment>(R.id.container_homepage)
                addToBackStack("main")
            }
        }
    }

    private fun addDataToList() {
        banners.add(ItemBanner(R.drawable.banner))
        banners.add(ItemBanner(R.drawable.banner2))
        banners.add(ItemBanner(R.drawable.banner3))
        banners.add(ItemBanner(R.drawable.banner4))
        banners.add(ItemBanner(R.drawable.banner5))
    }

    private fun startAutoScroll() {
        val scrollRunnable = object : Runnable {
            override fun run() {
                if (scrollPosition < adapterBanner.itemCount) {
                    binding.bannerCollections.smoothScrollToPosition(scrollPosition)
                    scrollPosition++
                    scrollHandler.postDelayed(this, 6000) // Intervallo di 6 secondi
                } else {
                    scrollPosition = 0
                    scrollHandler.postDelayed(this, 6000) // Intervallo di 6 secondi
                }
            }
        }
        scrollHandler.post(scrollRunnable)
    }
}
