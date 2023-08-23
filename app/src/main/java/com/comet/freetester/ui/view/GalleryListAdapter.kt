package com.comet.freetester.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.comet.freetester.R
import com.comet.freetester.data.GalleryItem
import com.comet.freetester.databinding.GalleryListItemBinding
import com.comet.freetester.model.DeliveryDataModel
import com.comet.freetester.util.Utils

class GalleryListAdapter(
    private val context: Context,
    private var objects: ArrayList<GalleryItem>
): ArrayAdapter<GalleryItem>(context, 0, objects) {

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): GalleryItem {
        return objects[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) GalleryListItemBinding.bind(convertView) else
            GalleryListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = getItem(position)
        Utils.setImageUri(context, binding.thumbnail, item.photoUri, R.drawable.placeholder)
        binding.title.text = item.title

        binding.date.text = Utils.getDateTimeString(item.createdAt)
        val dataModel = DeliveryDataModel.getInstance()
        dataModel.getUser(item.uid)?.let {
            binding.userName.text = it.nameStr
            Utils.setImageUri(context, binding.userIcon, it.photoUri, R.drawable.player_photo_default)
        }
        return binding.root
    }

    fun changeData(update: ArrayList<GalleryItem>) {
        objects = update
        notifyDataSetChanged()
    }
}