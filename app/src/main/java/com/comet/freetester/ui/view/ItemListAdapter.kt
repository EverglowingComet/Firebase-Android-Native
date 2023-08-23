package com.comet.freetester.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.comet.freetester.R
import com.comet.freetester.data.Item
import com.comet.freetester.databinding.ItemListViewBinding
import com.comet.freetester.util.toValueStr

class ItemListAdapter(
    private val context: Context,
    private var objects: ArrayList<Item>
): ArrayAdapter<Item>(context, 0, objects) {
    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): Item {
        return objects[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) ItemListViewBinding.bind(convertView) else
            ItemListViewBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = getItem(position)
        binding.title.text = item.title
        binding.iconAssembly.visibility = if (item.assemblyFull) View.VISIBLE else View.GONE
        binding.labelAssembly.visibility = if (item.assemblyFull) View.VISIBLE else View.GONE
        binding.iconBreakdown.visibility = if (item.breakdownRequired) View.VISIBLE else View.GONE
        binding.labelBreakdown.visibility = if (item.breakdownRequired) View.VISIBLE else View.GONE

        binding.sizeLabel.text = String.format(context.getString(R.string.size_string), item.width.toValueStr(), item.height.toValueStr(), item.depth.toValueStr())
        binding.weightLabel.text = String.format(context.getString(R.string.weight_string), item.weight.toValueStr())

        return binding.root
    }

    fun changeData(update: ArrayList<Item>) {
        objects = update
        notifyDataSetChanged()
    }
}