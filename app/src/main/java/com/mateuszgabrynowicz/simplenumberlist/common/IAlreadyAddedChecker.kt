package com.mateuszgabrynowicz.simplenumberlist.common

/**
 * Created by Mateusz on 19.05.2019.
 */

interface IAlreadyAddedChecker {
    fun isAlreadyAdded(number: Int): Boolean
}