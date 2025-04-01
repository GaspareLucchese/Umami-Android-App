package com.example.giapan_beta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView

class AdapterListaCategorie(
    private val context: Context,
    private val primari :List <String>,
    private val secondari : Map<String, List<String>>,
    private val iconeCategorie : Map<String, Int>
) : BaseExpandableListAdapter()
{
    override fun getGroupCount(): Int {
        return  this.primari.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.secondari[this.primari[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.primari[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.secondari[this.primari[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val titolo = getGroup(groupPosition) as String
        if(convertView == null)
        {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.categoria_primaria, null)
        }
        val TextViewPrimaria = convertView!!.findViewById<TextView>(R.id.CategoriaPrimaria)
        val iconaImageView = convertView.findViewById<ImageView>(R.id.categoria_img)

        TextViewPrimaria.text = titolo
        val iconID = iconeCategorie[titolo]
        if(iconID != null)
        {
            iconaImageView.setImageResource(iconID)
        }
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val titolo = getChild(groupPosition, childPosition) as String
        if(convertView == null)
        {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.categoria_secondaria, null)
        }
        val TextViewSecondaria = convertView!!.findViewById<TextView>(R.id.CategoriaSecondaria)

        TextViewSecondaria.text = titolo
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}