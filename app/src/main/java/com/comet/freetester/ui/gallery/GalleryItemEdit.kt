package com.comet.freetester.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.comet.freetester.R
import com.comet.freetester.data.GalleryItem
import com.comet.freetester.databinding.ActivityGalleryItemEditBinding
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.model.FirebaseStorageListener
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.util.Utils
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.database.DatabaseError

class GalleryItemEdit : ArenaActivity() {
    private lateinit var binding: ActivityGalleryItemEditBinding
    private lateinit var gallery: GalleryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryItemEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        updateContents()
    }

    override fun initViews() {
        super.initViews()

        gallery = dataModel.getGalleryItem(intent.getStringExtra(Utils.EXTRA)) ?: GalleryItem()
        binding.btnBack.setOnClickListener { finish() }
        binding.uploadBtn.setOnClickListener { takePhoto() }
        binding.submitBtn.setOnClickListener { submitResult() }
    }

    override fun updateContents() {
        if (iconUri != null) {
            binding.thumbnail.setImageURI(iconUri)
        } else {
            Utils.setImageUri(this, binding.thumbnail, gallery.photoUri, R.drawable.placeholder)
            binding.titleInput.setText(gallery.title)
            binding.storyInput.setText(gallery.note)
        }
    }

    private fun takePhoto() {
        val options = CropImageOptions()
        options.guidelines = CropImageView.Guidelines.ON

        cropImage.launch(CropImageContractOptions(null, options))
    }

    private var iconUri: Uri? = null
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            if (result.uriContent != null) {
                iconUri = result.uriContent

                updateContents()
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    private fun submitResult() {
        if (gallery.photoUri == null && iconUri == null) {
            Utils.showDialog(this, R.string.gallery_photo_prompt)
            return
        }
        gallery.title = binding.titleInput.text.toString()
        gallery.note = binding.storyInput.text.toString()
        gallery.uid = dataModel.user.uid

        if (gallery.id == null) {
            gallery.id = dataModel.generateId("gallery")
        }

        if (TextUtils.isEmpty(gallery.title)) {
            Utils.showDialog(this, R.string.gallery_title_prompt)
            return
        }
        if (TextUtils.isEmpty(gallery.note)) {
            Utils.showDialog(this, R.string.gallery_story_prompt)
            return
        }
        uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
        if (iconUri != null) {
            dataModel.uploadImageFileToStorage(gallery.id, "gallery", iconUri, object : FirebaseStorageListener {
                override fun onSuccess(uri: Uri?) {
                    if (uri != null) {
                        gallery.photoUri = uri.toString()
                        saveResult()
                    } else {
                        uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                    }
                }

                override fun onFailure(error: DatabaseError?) {
                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                }
            })
        } else {
            saveResult()
        }
    }

    private fun saveResult() {
        dataModel.submitGallery(gallery, object : FirebaseDatabaseListener {
            override fun onSuccess() {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                uiHandler.sendEmptyMessage(UIHandler.MSG_FINISH)
            }

            override fun onFailure(error: DatabaseError?) {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
            }
        })
    }
}