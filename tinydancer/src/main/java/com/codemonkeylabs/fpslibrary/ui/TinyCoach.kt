package com.codemonkeylabs.fpslibrary.ui

import android.animation.Animator
import android.app.Application
import android.app.Service
import android.graphics.PixelFormat
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.TextView
import com.codemonkeylabs.fpslibrary.Calculation
import com.codemonkeylabs.fpslibrary.Calculation.Metric
import com.codemonkeylabs.fpslibrary.FPSConfig
import com.codemonkeylabs.fpslibrary.R

class TinyCoach(context: Application?, private val fpsConfig: FPSConfig) {
    private val meterView: TextView
    private val windowManager: WindowManager
    private val shortAnimationDuration = 200
    private val longAnimationDuration = 700

    // detect double tap so we can hide tinyDancer
    private val simpleOnGestureListener: SimpleOnGestureListener =
        object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // hide but don't remove view
                hide(false)
                return super.onDoubleTap(e)
            }
        }

    private fun addViewToWindow(view: View) {
        val permissionFlag = PermissionCompat.getFlag()
        val paramsF = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            permissionFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // configure starting coordinates
        if (fpsConfig.xOrYSpecified) {
            paramsF.x = fpsConfig.startingXPosition
            paramsF.y = fpsConfig.startingYPosition
            paramsF.gravity = FPSConfig.DEFAULT_GRAVITY
        } else if (fpsConfig.gravitySpecified) {
            paramsF.x = 0
            paramsF.y = 0
            paramsF.gravity = fpsConfig.startingGravity
        } else {
            paramsF.gravity = FPSConfig.DEFAULT_GRAVITY
            paramsF.x = fpsConfig.startingXPosition
            paramsF.y = fpsConfig.startingYPosition
        }

        // add view to the window
        windowManager.addView(view, paramsF)

        // create gesture detector to listen for double taps
        val gestureDetector =
            GestureDetector(view.context, simpleOnGestureListener)

        // attach touch listener
        view.setOnTouchListener(DancerTouchListener(paramsF, windowManager, gestureDetector))

        // disable haptic feedback
        view.isHapticFeedbackEnabled = false

        // show the meter
        show()
    }

    fun showData(
        fpsConfig: FPSConfig?,
        dataSet: List<Long?>?
    ) {
        val droppedSet = Calculation.getDroppedSet(fpsConfig, dataSet)
        val answer =
            Calculation.calculateMetric(fpsConfig, dataSet, droppedSet)
        if (answer.key == Metric.BAD) {
            meterView.setBackgroundResource(R.drawable.fpsmeterring_bad)
        } else if (answer.key == Metric.MEDIUM) {
            meterView.setBackgroundResource(R.drawable.fpsmeterring_medium)
        } else {
            meterView.setBackgroundResource(R.drawable.fpsmeterring_good)
        }
        meterView.text = "${answer.value.toInt()}"
    }

    fun destroy() {
        meterView.setOnTouchListener(null)
        hide(true)
    }

    fun show() {
        meterView.alpha = 0f
        meterView.visibility = View.VISIBLE
        meterView.animate()
            .alpha(1f)
            .setDuration(longAnimationDuration.toLong())
            .setListener(null)
    }

    fun hide(remove: Boolean) {
        meterView.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    meterView.visibility = View.GONE
                    if (remove) {
                        windowManager.removeView(meterView)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }

    init {
        //create meter view
        meterView = LayoutInflater.from(context).inflate(R.layout.meter_view, null) as TextView

        //set initial fps value....might change...
        meterView.text = "${fpsConfig.refreshRate.toInt()}"

        // grab window manager and add view to the window
        windowManager = meterView.context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

        val minWidth = (meterView.lineHeight
                + meterView.totalPaddingTop
                + meterView.totalPaddingBottom
                + meterView.paint.fontMetrics.bottom.toInt())

        meterView.minWidth = minWidth

        addViewToWindow(meterView)
    }
}