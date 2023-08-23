package com.comet.freetester.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.comet.freetester.R
import com.comet.freetester.data.Delivery
import com.comet.freetester.databinding.DeliveryListItemBinding
import com.comet.freetester.util.Utils
import com.comet.freetester.util.toFormattedStr

class DeliveryListAdapter(
    private val context: Context,
    private var objects: ArrayList<Delivery>
): ArrayAdapter<Delivery>(context, 0, objects) {

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): Delivery {
        return objects[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) DeliveryListItemBinding.bind(convertView) else
            DeliveryListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = getItem(position)
        binding.storeAddress.text = item.store.formatted_address
        binding.dropAddress.text = item.dropoff.formatted_address

        binding.date.text = Utils.getDateTimeString(item.deadline)
        binding.itemCount.text = String.format(context.getString(R.string.item_count), item.items.size)

        binding.price.text = String.format("$ %s", item.price.toFormattedStr())
        return binding.root
    }

    fun changeData(update: ArrayList<Delivery>) {
        objects = update
        notifyDataSetChanged()
    }
}