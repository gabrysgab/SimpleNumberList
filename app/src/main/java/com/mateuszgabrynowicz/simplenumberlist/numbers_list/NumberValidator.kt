package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import com.mateuszgabrynowicz.simplenumberlist.common.IAlreadyAddedChecker
import com.mateuszgabrynowicz.simplenumberlist.common.ValidationResult
import javax.inject.Inject

/**
 * Created by Mateusz on 18.05.2019.
 */


class NumberValidator @Inject constructor() {

    companion object {
        const val MAXIMUM_NUMBER = 10_000
        const val MINIMUM_NUMBER = 0
    }

    fun validateNumber(number: Int?, alreadyAddedChecker: IAlreadyAddedChecker): ValidationResult {
        return if(number == null) ValidationResult.EMPTY_VALUE else validateNumber(number, alreadyAddedChecker)
    }

    private fun validateNumber(number: Int, alreadyAddedChecker: IAlreadyAddedChecker): ValidationResult {
        return when {
            (number < MINIMUM_NUMBER || number > MAXIMUM_NUMBER) -> ValidationResult.WRONG_VALUE
            alreadyAddedChecker.isAlreadyAdded(number) -> ValidationResult.ALREADY_ADDED
            else -> ValidationResult.CORRECT_VALUE
        }
    }
}