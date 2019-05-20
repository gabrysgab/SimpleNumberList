package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mateuszgabrynowicz.simplenumberlist.common.ViewState
import com.mateuszgabrynowicz.simplenumberlist.model.NumbersListResponse
import com.mateuszgabrynowicz.simplenumberlist.repository.INumbersRepository
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Mateusz on 20.05.2019.
 */

class NumbersListViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val numbersRepository: INumbersRepository = mock()
    private val compositeDisposable: CompositeDisposable = mock()
    private val observer: Observer<ViewState<List<Int>>> = mock()
    private val numbersList = listOf(2, 5, 7)

    private lateinit var viewModel: NumbersListViewModel

    @Before
    fun setup() {
        viewModel = NumbersListViewModel(numbersRepository, compositeDisposable)
        viewModel.numberListLiveData.observeForever(observer)
    }

    @Test
    fun `should return loading state when loading`() {
        val singleResponse = prepareSingleResponse()
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()

        verify(observer).onChanged(ViewState.Loading())
    }

    @Test
    fun `should return populated state on success initial load`() {
        val singleResponse = prepareSingleResponse()
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.initialLoad()

        verify(observer).onChanged(ViewState.Loading())
        verify(observer).onChanged(ViewState.Populated(numbersList))
    }

    @Test
    fun `should return populated state on success load more`() {
        val singleResponse = prepareSingleResponse()
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()

        verify(observer).onChanged(ViewState.Populated(numbersList))
    }

    @Test
    fun `should return empty state on empty list response`() {
        val numberOfPages = 0
        val repoResponse = NumbersListResponse(emptyList(), numberOfPages)
        val singleResponse = Single.just(repoResponse)
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()

        verify(observer).onChanged(ViewState.Empty())
    }

    @Test
    fun `should return error state on error response`() {
        val throwable = Throwable()
        val singleResponse = Single.error<NumbersListResponse>(throwable)
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()

        verify(observer).onChanged(ViewState.Error(throwable))
    }

    @Test
    fun `shouldn't make call on initial load when list is already initiated`() {
        val singleResponse = prepareSingleResponse()
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()
        viewModel.initialLoad()

        verify(numbersRepository, times(1)).loadMoreNumbers(any())
    }

    @Test
    fun `shouldn't make call on load more when all pages are loaded`() {
        val numberOfPages = 2
        val numbersList = listOf(2, 5, 7)
        val repoResponse = NumbersListResponse(numbersList, numberOfPages)
        val singleResponse = Single.just(repoResponse)
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        for (i in 0..numberOfPages + 1) {
            viewModel.loadMoreNumbers()
        }

        verify(numbersRepository, times(numberOfPages)).loadMoreNumbers(any())
    }

    @Test
    fun `should call add on CompositeDisposable when making loadMoreNumbers call`() {
        val numbersList = listOf(2, 5, 7)
        val repoResponse = NumbersListResponse(numbersList, 2)
        val singleResponse = Single.just(repoResponse)
        whenever(numbersRepository.loadMoreNumbers(any())).thenReturn(singleResponse)

        viewModel.loadMoreNumbers()

        verify(compositeDisposable).add(any())
    }

    @Test
    fun `should add number to the list`() {
        val firstNumberToAdd = 1
        val secondNumberToAdd = 2

        viewModel.addNumber(firstNumberToAdd)

        val viewState = viewModel.numberListLiveData.value
        assert(viewState is ViewState.Populated)
        assert((viewState as ViewState.Populated).data.contains(firstNumberToAdd))
        assert(!viewState.data.contains(secondNumberToAdd))

        viewModel.addNumber(secondNumberToAdd)

        assert(viewState.data.contains(secondNumberToAdd))
    }

    @Test
    fun `should not add number to the list if it already exists`() {
        val firstNumberToAdd = 1
        val secondNumberToAdd = 2

        viewModel.addNumber(firstNumberToAdd)

        val viewState = viewModel.numberListLiveData.value
        assert((viewState as ViewState.Populated).data.size == 1)

        viewModel.addNumber(firstNumberToAdd)

        assert(viewState.data.size == 1)

        viewModel.addNumber(secondNumberToAdd)

        assert(viewState.data.size == 2)
    }

    private fun prepareSingleResponse(): Single<NumbersListResponse> {
        val numberOfPages = 2
        val repoResponse = NumbersListResponse(numbersList, numberOfPages)
        return Single.just(repoResponse)
    }
}
