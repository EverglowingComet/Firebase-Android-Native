package com.comet.freetester.ui

import android.content.Intent
import android.os.Bundle
import com.comet.freetester.data.Delivery
import com.comet.freetester.databinding.ActivityTodayDeliveryBinding
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.ui.view.DeliveryListAdapter
import com.comet.freetester.util.Utils
import com.google.firebase.database.DatabaseError

class TodayDelivery : ArenaActivity() {
    private lateinit var binding: ActivityTodayDeliveryBinding
    private lateinit var adapter: DeliveryListAdapter
    private var contentList: ArrayList<Delivery> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        loadContents()
    }

    override fun initViews() {
        super.initViews()

        adapter = DeliveryListAdapter(this, contentList)
        binding.listView.adapter = adapter
        binding.listView.emptyView = binding.empty
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val item = adapter.getItem(position)
            val intent = Intent(this, DeliveryDetails::class.java)
            intent.putExtra(Utils.EXTRA, item.id)
            startActivity(intent)
        }
    }

    override fun loadContents() {
        uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
        dataModel.deliveryQuery(object : FirebaseDatabaseListener {
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

        for (item in dataModel.deliveryList) {
            if (item.paid) {
                contentList.add(item)
            }
        }

        contentList.sortWith(Comparator { o1, o2 -> Utils.compareLongValue(o1.deadline, o2.deadline) })
        adapter.changeData(contentList)
    }
}