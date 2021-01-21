package com.tolgahankilic.firebasefotografpaylas.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tolgahankilic.firebasefotografpaylas.databinding.ActivityKullaniciBinding

class KullaniciActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKullaniciBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKullaniciBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null) {
            //Kullanıcı önceden giriş yaptıysa direk login ol
            val intent = Intent(this, HaberlerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun girisYap (view: View){

        auth.signInWithEmailAndPassword(binding.emailText.text.toString(),binding.passwordText.text.toString()).addOnCompleteListener { task ->

            if (task.isSuccessful){
                //Girilen veriler doğruysa
                val guncelKullanici = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin: ${guncelKullanici}",Toast.LENGTH_LONG).show()

                val intent = Intent(this, HaberlerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            //Girilen veriler yanlışsa
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }

    fun kayitOl (view: View){

        val email = binding.emailText.text.toString();
        val sifre = binding.passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->
            //asenkron
            if (task.isSuccessful) {
                //Veriler uygun ve kayıt gerçekleştiyse otomatik login ol
                val intent = Intent(this, HaberlerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            //Hata varsa
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }
}