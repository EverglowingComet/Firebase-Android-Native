package com.comet.freetester.ui.gallery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.comet.freetester.R
import com.comet.freetester.data.GalleryItem
import com.comet.freetester.databinding.ActivityGalleryScreenBinding
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.ui.view.GalleryListAdapter
import com.comet.freetester.util.Utils
import com.google.firebase.database.DatabaseError

class GalleryScreen : ArenaActivity() {
    private lateinit var binding: ActivityGalleryScreenBinding
    private lateinit var adapter: GalleryListAdapter
    private var contentList: ArrayList<GalleryItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        loadContents()
    }

    override fun initViews() {
        super.initViews()

        adapter = GalleryListAdapter(this, contentList)
        binding.listView.adapter = adapter
        binding.listView.emptyView = binding.empty
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val item = adapter.getItem(position)

            if (item.uid == dataModel.user.uid) {
                AlertDialog.Builder(this)
                    .setItems(R.array.gallery_action) { _, which ->
                        if (which == 0) {
                            openEdit(item)
                        } else {
                            openDetails(item)
                        }
                    }.create().show()
            } else {
                openDetails(item)
            }
        }
        binding.btnAdd.setOnClickListener {
            openEdit(null)
        }
    }

    override fun loadContents() {
        uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
        dataModel.galleryQuery(object : FirebaseDatabaseListener {
            override fun onSuccess() {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                uiHandler.sendEmptyMessage(UIHandler.MSG_UPDATE_CONTENTS)
            }

            override fun onFailure(error: DatabaseError?) {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                uiHandler.sendEmptyMessage(UIHandler.MSG_UPDATE_CONTENTS)
            }
        })
    }

    override fun updateContents() {
        super.updateContents()
        contentList = ArrayList()

        for (item in dataModel.galleryList) {
            contentList.add(item)
        }

        contentList.sortWith(Comparator { o1, o2 -> Utils.compareLongValue(o1.createdAt, o2.createdAt) })
        adapter.changeData(contentList)
    }

    private fun openDetails(item: GalleryItem) {
        val intent = Intent(this, GalleryDetails::class.java)
        intent.putExtra(Utils.EXTRA, item.id)
        startActivity(intent)
    }

    private fun openEdit(item: GalleryItem?) {
        val intent = Intent(this, GalleryItemEdit::class.java)
        intent.putExtra(Utils.EXTRA, item?.id)
        startActivity(intent)
    }
}