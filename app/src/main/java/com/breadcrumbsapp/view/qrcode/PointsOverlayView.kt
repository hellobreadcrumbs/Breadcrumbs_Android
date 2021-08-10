package com.breadcrumbsapp.view.qrcode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View


class PointsOverlayView : View {
    var points: Array<PointF>? = null
    private var paint: Paint? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.color = Color.YELLOW
        paint!!.style = Paint.Style.FILL
    }

    @JvmName("setPoints1")
    fun setPoints(points: Array<PointF>?) {
        this.points = points
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (points != null) {
            for (pointF in points!!) {
                canvas.drawCircle(pointF.x, pointF.y, 10f, paint!!)
            }
        }
    }
}