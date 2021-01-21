package com.tolgahankilic.firebasefotografpaylas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tolgahankilic.firebasefotografpaylas.R
import com.tolgahankilic.firebasefotografpaylas.databinding.RecyclerRowBinding
import com.tolgahankilic.firebasefotografpaylas.model.Post

class HaberRecyclerAdapter(val postList : ArrayList<Post>) : RecyclerView.Adapter<HaberRecyclerAdapter.PostHolder>(){
    class PostHolder(private val itemBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind (kullaniciEmail : String, kullaniciYorum : String, kullaniciGorsel : String){
            itemBinding.recyclerRowKullaniciEmail.text = kullaniciEmail
            itemBinding.recyclerRowYorumText.text = kullaniciYorum
            Picasso.get().load(kullaniciGorsel).into(itemBinding.recyclerRowImageview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        /*
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
        */

        val itemBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val kullaniciEmail = postList[position].kullaniciEmail
        val kullaniciYorum = postList[position].kullaniciYorum
        val kullaniciGorsel = postList[position].gorselUrl
        holder.bind(kullaniciEmail, kullaniciYorum, kullaniciGorsel)
        /*
        holder.itemView.recycler_row_kullanici_email.text = postList[position].kullaniciEmail
        holder.itemView.recycler_row_yorum.text = postList[position].kullaniciYorum
        */
    }

    override fun getItemCount(): Int = postList.size
}