package com.breadcrumbsapp.util

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class CirclePagerIndicatorDecoration : RecyclerView.ItemDecoration() {
    // private val colorActive = 0xFF545455.toInt()
    // private val colorInactive = 0x33000000

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private val mIndicatorHeight = (DP * 32).toInt()

    /**
     * Indicator stroke width.
     */
    private val mIndicatorStrokeWidth = DP * 16

    /**
     * Indicator width.
     */
    private val mIndicatorItemLength = DP * 2

    /**
     * Padding between indicators.
     */
    private val mIndicatorItemPadding = DP * 26

    /**
     * Some more natural animation interpolation
     */
    private val mInterpolator = AccelerateDecelerateInterpolator()

    private val mPaint = Paint()

    init {
        mPaint.apply {
            strokeWidth = mIndicatorStrokeWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount = parent.adapter?.itemCount ?: 0

        // center horizontally, calculate width and subtract half from center
        val totalLength = mIndicatorItemLength * itemCount
        val paddingBetweenItems = max(0, itemCount - 1) * mIndicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f

        // center vertically in the allotted space
        val indicatorPosY = parent.height - mIndicatorHeight / 2f

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)

        // find active page (which should be highlighted)
        (parent.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
            val activePosition = layoutManager.findFirstVisibleItemPosition()
            if (activePosition == RecyclerView.NO_POSITION) {
                return
            }

            // find offset of active page (if the user is scrolling)
            layoutManager.findViewByPosition(activePosition)?.let { activeChild ->
                val left = activeChild.left
                val width = activeChild.width
                // on swipe the active item will be positioned from [-width, 0]
                // interpolate offset for smooth animation
                val progress = mInterpolator.getInterpolation(left * -1 / width.toFloat())

                drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress)
            }
        }

    }

    private fun drawInactiveIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        //   mPaint.color = colorInactive
        mPaint.color = Color.parseColor("#8E5A00")

        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding

        var start = indicatorStartX
        for (i in 0 until itemCount) {
            c.drawCircle(start, indicatorPosY, mIndicatorItemLength / 2f, mPaint)
            start += itemWidth
        }
    }

    private fun drawHighlights(
        c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
        highlightPosition: Int, progress: Float
    ) {
        //  mPaint.color = colorActive
        mPaint.color = Color.parseColor("#FD9F00")

        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding

        if (progress == 0f) {
            // no swipe, draw a normal indicator
            val highlightStart = indicatorStartX + itemWidth * highlightPosition

            c.drawCircle(highlightStart, indicatorPosY, mIndicatorItemLength / 2f, mPaint)

        } else {
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            // calculate partial highlight
            val partialLength = mIndicatorItemLength * progress + mIndicatorItemPadding * progress

            c.drawCircle(
                highlightStart + partialLength,
                indicatorPosY,
                mIndicatorItemLength / 2f,
                mPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mIndicatorHeight
    }

    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}
