package com.mateuszgabrynowicz.simplenumberlist.common

/**
 * Created by Mateusz on 19.05.2019.
 */

sealed class ViewState<T> {

    data class Populated<T>(val data: T) : ViewState<T>()
    data class Error<T>(val throwable: Throwable) : ViewState<T>()
    class Loading<T> : ViewState<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
    class Empty<T> : ViewState<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }
        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
