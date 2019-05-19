package com.mateuszgabrynowicz.simplenumberlist.repository

import com.mateuszgabrynowicz.simplenumberlist.common.SchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.model.NumbersListResponse
import io.reactivex.Single
import javax.inject.Inject


/**
 * Created by Mateusz on 19.05.2019.
 */

class NumbersRepository @Inject constructor(private val schedulersProvider: SchedulersProvider) : INumbersRepository {

    companion object {
        const val PAGE_SIZE = 30
    }

    private val numbersList = generateList()

    private fun generateList(): List<List<Int>> {
        val list = mutableListOf<Int>()
        for (i in 2..10_000 step 2) {
            list.add(i)
        }
        return getPaginatedNumbers(list, PAGE_SIZE)
    }

    private fun getPaginatedNumbers(allNumbers: List<Int>, pageSize: Int): List<List<Int>> {
        var mutablePageSize = pageSize
        val list = ArrayList(allNumbers)
        if (mutablePageSize <= 0 || mutablePageSize > list.size)
            mutablePageSize = list.size
        val numberOfPages = Math.ceil(list.size.toDouble() / pageSize.toDouble()).toInt()
        val pages = ArrayList<List<Int>>(numberOfPages)
        var pageNumber = 0
        while (pageNumber < numberOfPages)
            pages.add(
                list.subList(
                    pageNumber.times(mutablePageSize),
                    Math.min((++pageNumber).times(mutablePageSize), list.size)
                )
            )
        return pages
    }

    override fun loadMoreNumbers(page: Int): Single<NumbersListResponse> {
        val currentPageNumbers = numbersList[page]

        return Single.just(NumbersListResponse(currentPageNumbers, numbersList.size))
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())
    }
}