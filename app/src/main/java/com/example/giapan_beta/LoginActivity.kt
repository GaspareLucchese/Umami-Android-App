package com.example.giapan_beta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.giapan_beta.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setText(Html.fromHtml("<u>ACCEDI</u>"))

        binding.registraBtn.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RegistrazioneFragment>(R.id.container_login)
                addToBackStack("main")
            }
        }

        binding.LoginButton.setOnClickListener {
            val email = binding.emailInserita.text.toString()
            val password = binding.passwordInserita.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            }
        }

        viewModel.success.observe(this) { isSuccess ->
            if (isSuccess == true) {
                val profilo = "${viewModel.datiRegistrazione.value?.nome} ${viewModel.datiRegistrazione.value?.cognome}"
                val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("email", viewModel.datiRegistrazione.value?.email)
                    putString("profilo", profilo)
                    apply()
                }

                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenziali non valide", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
