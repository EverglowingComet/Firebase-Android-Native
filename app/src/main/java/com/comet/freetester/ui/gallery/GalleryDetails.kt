package com.comet.freetester.ui.gallery

import android.os.Bundle
import com.comet.freetester.R
import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.databinding.ActivityGalleryDetailsBinding
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.util.Utils

class GalleryDetails : ArenaActivity() {
    private lateinit var binding: ActivityGalleryDetailsBinding
    private lateinit var gallery: GalleryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        updateContents()
    }

    override fun initViews() {
        super.initViews()

        gallery = dataModel.getGalleryItem(intent.getStringExtra(Utils.EXTRA))!!
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun updateContents() {
        Utils.setImageUri(this, binding.thumbnail, gallery.photoUri, R.drawable.placeholder)
        binding.titleText.text = gallery.title
        binding.storyText.text = gallery.note
    }

}