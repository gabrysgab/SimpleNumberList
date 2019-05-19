package com.mateuszgabrynowicz.simplenumberlist.repository

import com.mateuszgabrynowicz.simplenumberlist.model.NumbersListResponse
import io.reactivex.Single

/**
 * Created by Mateusz on 19.05.2019.
 */

interface INumbersRepository {
    fun loadMoreNumbers(page: Int): Single<NumbersListResponse>
}