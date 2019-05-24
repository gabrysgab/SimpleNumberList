package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mateuszgabrynowicz.simplenumberlist.common.DialogState
import com.mateuszgabrynowicz.simplenumberlist.common.ViewState
import com.mateuszgabrynowicz.simplenumberlist.model.NumbersListResponse
import com.mateuszgabrynowicz.simplenumberlist.repository.INumbersRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Mateusz on 18.05.2019.
 */

class NumbersListViewModel @Inject constructor(
    private val numbersRepository: INumbersRepository,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {
    var scrollPosition = 0
    var errorDialogState: DialogState? = null
    var inputDialogState: DialogState? = null
    private val loadedList = mutableListOf<Int>()
    private var currentPage = 0

    private val numberListMutableLiveData = MutableLiveData<ViewState<List<Int>>>()
    val numberListLiveData: LiveData<ViewState<List<Int>>> = numberListMutableLiveData

    companion object {
        private const val ALL_PAGES_LOADED = -1
    }

    fun initialLoad() {
        if (currentPage != 0) return
        loadMoreNumbers()
    }

    fun loadMoreNumbers() {
        if (currentPage == ALL_PAGES_LOADED) return
        numberListMutableLiveData.value = ViewState.Loading()
        compositeDisposable.add(
            numbersRepository.loadMoreNumbers(currentPage)
                .subscribe({ numbersListResponse ->
                    if (numbersListResponse.numbers.isEmpty()) onEmptyResponse()
                    else onSuccessResponse(numbersListResponse)
                },
                    { throwable ->
                        onError(throwable)
                    })
        )
    }

    private fun onEmptyResponse() {
        numberListMutableLiveData.value = ViewState.Empty()
    }

    private fun onError(throwable: Throwable) {
        numberListMutableLiveData.value = ViewState.Error(throwable)
    }

    private fun onSuccessResponse(numbersListResponse: NumbersListResponse) {
        if (++currentPage == numbersListResponse.totalPages) currentPage = ALL_PAGES_LOADED
        when {
            numbersListResponse.numbers.isEmpty() -> ViewState.Empty<List<Int>>()
            else -> {
                loadedList.addAll(numbersListResponse.numbers)
                numberListMutableLiveData.value = ViewState.Populated(loadedList)
            }
        }
    }

    fun addNumber(number: Int) {
        if (!loadedList.contains(number)) {
            loadedList.add(number)
            numberListMutableLiveData.value = ViewState.Populated(loadedList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}