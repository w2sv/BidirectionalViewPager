package com.w2sv.bidirectionalviewpager

import androidx.annotation.VisibleForTesting
import com.w2sv.bidirectionalviewpager.livedata.MutableListLiveData
import com.w2sv.bidirectionalviewpager.recyclerview.BidirectionalRecyclerViewAdapter
import com.w2sv.kotlinutils.asInt
import java.util.Collections

open class BidirectionalViewPagerDataSet<T>(dataSet: MutableList<T>) :
    MutableListLiveData<T>(dataSet) {

    fun initialViewPosition(dataSetStartPosition: Int): Int =
        (BidirectionalRecyclerViewAdapter.N_VIEWS / 2).let {
            it - getCorrespondingPosition(it) + dataSetStartPosition
        }

    /**
     * For keeping track of actual order
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var tailPosition: Int = lastIndex

    private val headPosition: Int
        get() = (tailPosition + 1) % size

    // ----------------Position Conversion

    fun getCorrespondingPosition(viewPosition: Int): Int =
        viewPosition % size

    fun atCorrespondingPosition(viewPosition: Int): T =
        get(getCorrespondingPosition(viewPosition))

    // ----------------Element Removal

    /**
     * @return if removing at tail -> preceding view, otherwise subsequent one
     */
    fun viewPositionIncrement(removePosition: Int): Int =
        when (tailPosition == removePosition) {
            true -> -1
            false -> 1
        }

    /**
     * - Removes element at [removePosition]
     * - Rotates collection, such as to make fixed [viewPosition] henceforth correspond to
     *   same element as pre-removal
     * - Resets [tailPosition]
     */
    fun removeAndRealign(removePosition: Int, viewPosition: Int) {
        val positionPostRemoval = getCorrespondingPosition(viewPosition).run {
            if (removePosition < this)
                minus(1)
            else
                this
        }

        removeAt(removePosition)

        val rotationDistance = getCorrespondingPosition(viewPosition) - positionPostRemoval

        Collections.rotate(this, rotationDistance)
        tailPosition = getRotatedIndex(
            tailPosition - (tailPosition >= removePosition).asInt,
            rotationDistance
        )
    }

    // --------------Page Index Retrieval

    fun pageIndex(position: Int): Int =
        if (position > tailPosition)
            position - headPosition
        else
            position + lastIndex - tailPosition
}

fun Collection<*>.getRotatedIndex(index: Int, distance: Int): Int =
    (index + distance).let {
        if (it < 0)
            size + it
        else
            it
    } % size
