package com.comet.freetester.ui

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.comet.freetester.data.Delivery
import com.comet.freetester.data.Item
import com.comet.freetester.databinding.ActivityDeliveryDetailsBinding
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.model.FirebaseStorageListener
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.ui.view.ItemListAdapter
import com.comet.freetester.util.Utils
import com.comet.freetester.util.toFormattedStr
import com.comet.freetester.R
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseError

class DeliveryDetails : ArenaActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDeliveryDetailsBinding
    private lateinit var adapter: ItemListAdapter
    private var contentList: ArrayList<Item> = ArrayList()
    private lateinit var delivery: Delivery

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initViews()
        updateContents()
    }

    override fun initViews() {
        super.initViews()

        delivery = dataModel.getDelivery(intent.getStringExtra(Utils.EXTRA))!!

        adapter = ItemListAdapter(this, contentList)
        binding.listView.adapter = adapter
        binding.listView.emptyView = binding.empty
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.takePhoto.setOnClickListener {
            takePhoto()
        }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()

    }

    override fun updateContents() {
        super.updateContents()
        if (TextUtils.isEmpty(delivery.customer.firstName) && TextUtils.isEmpty(delivery.customer.lastName)) {
            binding.nameLayout.visibility = View.GONE
        } else {
            binding.nameLayout.visibility = View.VISIBLE

            binding.name.text = delivery.getNameStr()
        }
        binding.email.text = delivery.customer.email
        binding.phone.text = delivery.customer.phone

        if (delivery.deadline < Utils.getCurTime()) {
            binding.deliveryTime.setText(R.string.delivery_by)
            binding.deliveryIn.text = Utils.getDateTimeString(delivery.deadline)
        } else {
            binding.deliveryTime.setText(R.string.delivery_by)
            binding.deliveryIn.text = Utils.getCountDown(delivery.deadline, Utils.getCurTime())
        }
        binding.price.text = String.format("$ %s", delivery.price.toFormattedStr())

        if (delivery.deliveryPhotoUri != null) {
            Utils.setImageUri(this, binding.deliveryPhoto, delivery.deliveryPhotoUri, R.drawable.player_photo_default)
            binding.deliveryPhoto.visibility = View.VISIBLE
        } else {
            binding.deliveryPhoto.visibility = View.GONE
        }

        contentList = ArrayList()

        for (item in delivery.items) {
            contentList.add(item)
        }

        contentList.sortWith(Comparator { o1, o2 -> o1.title.compareTo(o2.title) })
        adapter.changeData(contentList)
    }

    private fun takePhoto() {
        val options = CropImageOptions()
        options.guidelines = Guidelines.ON

        cropImage.launch(CropImageContractOptions(null, options))
    }

    private var iconUri: Uri? = null
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            if (result.uriContent != null) {
                iconUri = result.uriContent

                submitPhoto(iconUri!!)
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    private fun submitPhoto(uri: Uri) {
        uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
        dataModel.uploadImageFileToStorage(delivery.id, "certificate", uri, object : FirebaseStorageListener {
            override fun onSuccess(uri: Uri?) {
                if (uri != null) {
                    dataModel.submitDeliveryPhoto(delivery, uri, object : FirebaseDatabaseListener {
                        override fun onSuccess() {
                            uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                            uiHandler.sendEmptyMessage(UIHandler.MSG_UPDATE_CONTENTS)
                        }

                        override fun onFailure(error: DatabaseError?) {
                            uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                        }
                    })
                } else {
                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                }
            }

            override fun onFailure(error: DatabaseError?) {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
            }
        })
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap.clear()

        if (delivery.store != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(delivery.store.lat, delivery.store.lng)))
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(delivery.store.lat, delivery.store.lng))
                    .title(delivery.store.name)
            )
        }
        if (delivery.dropoff != null) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(delivery.dropoff.lat, delivery.dropoff.lng))
                    .title(delivery.dropoff.name)
            )
        }
        googleMap.setMinZoomPreference(12f)
        googleMap.setIndoorEnabled(true)
        val uiSettings: UiSettings = googleMap.getUiSettings()
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
    }
}