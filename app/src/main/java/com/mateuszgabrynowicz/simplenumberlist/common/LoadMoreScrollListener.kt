package com.mateuszgabrynowicz.simplenumberlist.common

/**
 * Created by Mateusz on 18.05.2019.
 */

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class LoadMoreScrollListener(private var layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    var isLoading: Boolean = false
    private var previousTotalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false
            previousTotalItemCount = totalItemCount
        }
        if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            loadMoreItems()
            isLoading = true
        }
    }

    abstract fun loadMoreItems()
}
