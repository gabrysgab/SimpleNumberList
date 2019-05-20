package com.mateuszgabrynowicz.simplenumberlist.repository

import com.mateuszgabrynowicz.simplenumberlist.common.ISchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.model.NumbersListResponse
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

/**
 * Created by Mateusz on 20.05.2019.
 */

class NumbersRepositoryTest {

    private val schedulersProvider: ISchedulersProvider = mock()
    lateinit var numbersRepository: NumbersRepository

    @Before
    fun setup() {
        whenever(schedulersProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulersProvider.mainThread()).thenReturn(Schedulers.trampoline())

        numbersRepository = NumbersRepository(schedulersProvider)
    }

    @Test
    fun `should return page with list of size 30`() {
        val pageSize = 30
        numbersRepository.loadMoreNumbers(0).test()
            .assertValue { numbersListResponse: NumbersListResponse ->
                numbersListResponse.numbers.size == pageSize
            }
    }

    @Test
    fun `should return error (IndexOutOfBoundException) when requesting wrong page`() {
        numbersRepository.loadMoreNumbers(0)
            .flatMap { numbersListResponse: NumbersListResponse ->
                numbersRepository.loadMoreNumbers(numbersListResponse.totalPages)
            }
            .test()
            .assertError { throwable ->
                throwable.cause is IndexOutOfBoundsException
            }
    }
}