package com.example.giapan_beta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giapan_beta.databinding.FragmentNotificheBinding

class NotificheFragment : Fragment(){

    private lateinit var binding: FragmentNotificheBinding
    private lateinit var customAdapter: AdapterNotifiche
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificheBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")

        if (email != null) {
            Log.d("NotificheFragment", "Email found: $email")
            viewModel.getNotifiche(email).observe(viewLifecycleOwner) { lista ->
                if (lista != null) {
                    customAdapter.updateData(lista)
                } else {
                    Log.d("NotificheFragment", "Lista notifiche è null")
                }
            }
        } else {
            Log.d("NotificheFragment", "Email is null or empty")
        }

        init(view)
    }

    private fun init(view: View) {
        binding.recyclerNotifiche.setHasFixedSize(true)
        binding.recyclerNotifiche.layoutManager = LinearLayoutManager(context)

        customAdapter = AdapterNotifiche(ArrayList())
        binding.recyclerNotifiche.adapter = customAdapter
    }
}

