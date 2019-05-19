package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.mateuszgabrynowicz.simplenumberlist.R
import com.mateuszgabrynowicz.simplenumberlist.common.IAlreadyAddedChecker
import kotlinx.android.synthetic.main.number_list_item.view.*

/**
 * Created by Mateusz on 18.05.2019.
 */

class NumbersListAdapter : RecyclerView.Adapter<NumbersListAdapter.NumberViewHolder>(), IAlreadyAddedChecker {

    private val numbersSortedList: SortedList<Int>

    init {
        numbersSortedList = SortedList(Int::class.javaObjectType, object : SortedListAdapterCallback<Int>(this) {
            override fun compare(o1: Int, o2: Int): Int = o1 - o2
            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
            override fun areItemsTheSame(item1: Int, item2: Int): Boolean = item1 == item2
        })
    }

    override fun getItemCount(): Int = numbersSortedList.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        return NumberViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.number_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numbersSortedList[position])
    }

    fun addNumbers(numbersToAdd: List<Int>) {
        numbersSortedList.addAll(numbersToAdd)
    }

    override fun isAlreadyAdded(number: Int) =
        numbersSortedList.indexOf(number) != SortedList.INVALID_POSITION

    inner class NumberViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(number: Int) {
            itemView.number_text.text = number.toString()
        }
    }
}