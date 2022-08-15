package com.example.zingmp3phake.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.zingmp3phake.R
import de.hdodenhof.circleimageview.CircleImageView

fun ImageView.loadByGlide(context: Context, uri: Uri) {
    Glide
        .with(context)
        .load(uri)
        .placeholder(R.drawable.imgzingmp3logo)
        .error(R.drawable.imgzingmp3logo)
        .into(this)
}

fun CircleImageView.loadByGlide(context: Context, uri: Uri) {
    Glide
        .with(context)
        .load(uri)
        .placeholder(R.drawable.imgzingmp3logo)
        .error(R.drawable.imgzingmp3logo)
        .into(this)
}
