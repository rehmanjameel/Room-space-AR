package com.codebase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.codebase.aroom.R

class ColorAdapter(private val context: Context, private val colors: Array<Pair<String, Int>>) : BaseAdapter() {
    private val selectedItems = HashSet<Int>()

    override fun getCount(): Int {
        return colors.size
    }

    override fun getItem(position: Int): Pair<String, Int> {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_color, parent, false)

        val colorNameTextView = view.findViewById<TextView>(R.id.colorNameTextView)
        val colorCheckBox = view.findViewById<CheckBox>(R.id.colorCheckBox)

        val color = getItem(position)
        colorNameTextView.text = color.first
        colorCheckBox.isChecked = selectedItems.contains(position)

        colorCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(position)
            } else {
                selectedItems.remove(position)
            }
        }

        return view
    }

    fun getSelectedColorCodes(): List<Int> {
        val selectedColorCodes = ArrayList<Int>()
        for (position in selectedItems) {
            selectedColorCodes.add(getItem(position).second)
        }
        return selectedColorCodes
    }
}

