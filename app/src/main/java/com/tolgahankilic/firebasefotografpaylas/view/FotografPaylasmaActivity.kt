package com.tolgahankilic.firebasefotografpaylas.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tolgahankilic.firebasefotografpaylas.databinding.ActivityFotografPaylasmaBinding
import java.util.*

class FotografPaylasmaActivity : AppCompatActivity() {
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var binding: ActivityFotografPaylasmaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotografPaylasmaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun paylas(view: View) {
        //depo işlemleri
        //UUID -> universal unique id
        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"

        val reference = storage.reference

        val gorselReference = reference.child("images").child(gorselIsmi)

        if (secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot ->
                val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val guncelKullaniciEmaili = auth.currentUser!!.email.toString()
                    val kullaniciYorumu = binding.yorumText.text.toString()
                    val tarih = Timestamp.now()
                    //Veritabanı işlemleri

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("gorselurl",downloadUrl)
                    postHashMap.put("kullaniciemail",guncelKullaniciEmaili)
                    postHashMap.put("kullaniciyorum",kullaniciYorumu)
                    postHashMap.put("tarih",tarih)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun gorselSec(view: View) {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin alınmamış
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            //izin zaten varsa
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izin verildikten sonra
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data

            if (secilenGorsel != null){
                if(Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageView.setImageBitmap(secilenBitmap)
                } else {
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    binding.imageView.setImageBitmap(secilenBitmap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}