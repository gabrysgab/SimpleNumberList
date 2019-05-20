package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import com.mateuszgabrynowicz.simplenumberlist.common.IAlreadyAddedChecker
import com.mateuszgabrynowicz.simplenumberlist.common.ValidationResult
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

/**
 * Created by Mateusz on 20.05.2019.
 */

class NumberValidatorTest {
    private val numberValidator = NumberValidator()
    private val alreadyAddedChecker: IAlreadyAddedChecker = mock()

    @Test
    fun `should return ValidationResultEmpty on null`() {
        val validationResult = numberValidator.validateNumber(null, alreadyAddedChecker)

        assert(validationResult == ValidationResult.EMPTY_VALUE)
    }

    @Test
    fun `should return correct value on number 0 to 10_000`() {
        val startNumber = 0
        val middleNumber = 5000
        val endNumber = 10_000

        val startNumberResult = numberValidator.validateNumber(startNumber, alreadyAddedChecker)
        val middleNumberResult = numberValidator.validateNumber(middleNumber, alreadyAddedChecker)
        val endNumberResult = numberValidator.validateNumber(endNumber, alreadyAddedChecker)

        assert(startNumberResult == ValidationResult.CORRECT_VALUE)
        assert(middleNumberResult == ValidationResult.CORRECT_VALUE)
        assert(endNumberResult == ValidationResult.CORRECT_VALUE)
    }

    @Test
    fun `should return wrong value on number less than 0 or more than 10_000`() {
        val beforeStartNumber = -1
        val afterEndNumber = 10_001

        val beforeStartResult = numberValidator.validateNumber(beforeStartNumber, alreadyAddedChecker)
        val afterEndResult = numberValidator.validateNumber(afterEndNumber, alreadyAddedChecker)

        assert(beforeStartResult == ValidationResult.WRONG_VALUE)
        assert(afterEndResult == ValidationResult.WRONG_VALUE)
    }

    @Test
    fun `should return already added when alreadyAddedChecker returns true`() {
        val number = 1
        whenever(alreadyAddedChecker.isAlreadyAdded(number)).thenReturn(true)

        val alreadyAddedResult = numberValidator.validateNumber(number, alreadyAddedChecker)

        assert(alreadyAddedResult == ValidationResult.ALREADY_ADDED)
    }
}