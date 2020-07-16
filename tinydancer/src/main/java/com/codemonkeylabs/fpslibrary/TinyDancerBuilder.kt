package com.codemonkeylabs.fpslibrary

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Choreographer
import android.view.WindowManager
import com.codemonkeylabs.fpslibrary.ui.TinyCoach

/**
 * Created by brianplummer on 8/29/15.
 */
@Deprecated("Use TinyDancer.Builder")
class TinyDancerBuilder {
    /**
     * configures the fpsConfig to the device's hardware
     * refreshRate ex. 60fps and deviceRefreshRateInMs ex. 16.6ms
     * @param context
     */
    private fun setFrameRate(context: Context) {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        fpsConfig!!.deviceRefreshRateInMs =
            1000f / display.refreshRate
        fpsConfig!!.refreshRate =
            display.refreshRate
    }
    // PUBLIC BUILDER METHODS
    /**
     * show fps meter, this regisers the frame callback that
     * collects the fps info and pushes it to the ui
     * @param context
     */
    fun show(context: Context) {
        if (overlayPermRequest(context)) {
            //once permission is granted then you must call show() again
            return
        }

        //are we running?  if so, call tinyCoach.show() and return
        if (tinyCoach != null) {
            tinyCoach!!.show()
            return
        }

        // set device's frame rate info into the config
        setFrameRate(context)

        // create the presenter that updates the view
        tinyCoach = TinyCoach(
            context.applicationContext as Application,
            fpsConfig
        )

        // create our choreographer callback and register it
        fpsFrameCallback =
            FPSFrameCallback(
                fpsConfig,
                tinyCoach
            )
        Choreographer.getInstance()
            .postFrameCallback(fpsFrameCallback)

        //set activity background/foreground listener
        Foreground.init(context.applicationContext as Application)
            .addListener(foregroundListener)
    }

    /**
     * this adds a frame callback that the library will invoke on the
     * each time the choreographer calls us, we will send you the frame times
     * and number of dropped frames.
     * @param callback
     * @return
     */
    fun addFrameDataCallback(callback: FrameDataCallback?): TinyDancerBuilder {
        fpsConfig!!.frameDataCallback =
            callback
        return this
    }

    /**
     * set red flag percent, default is 20%
     *
     * @param percentage
     * @return
     */
    fun redFlagPercentage(percentage: Float): TinyDancerBuilder {
        fpsConfig!!.redFlagPercentage =
            percentage
        return this
    }

    /**
     * set red flag percent, default is 5%
     * @param percentage
     * @return
     */
    fun yellowFlagPercentage(percentage: Float): TinyDancerBuilder {
        fpsConfig!!.yellowFlagPercentage =
            percentage
        return this
    }

    /**
     * starting x position of fps meter default is 200px
     * @param xPosition
     * @return
     */
    fun startingXPosition(xPosition: Int): TinyDancerBuilder {
        fpsConfig!!.startingXPosition =
            xPosition
        fpsConfig!!.xOrYSpecified = true
        return this
    }

    /**
     * starting y positon of fps meter default is 600px
     * @param yPosition
     * @return
     */
    fun startingYPosition(yPosition: Int): TinyDancerBuilder {
        fpsConfig!!.startingYPosition =
            yPosition
        fpsConfig!!.xOrYSpecified = true
        return this
    }

    /**
     * starting gravity of fps meter default is Gravity.TOP | Gravity.START;
     * @param gravity
     * @return
     */
    fun startingGravity(gravity: Int): TinyDancerBuilder {
        fpsConfig!!.startingGravity =
            gravity
        fpsConfig!!.gravitySpecified = true
        return this
    }

    /**
     * request overlay permission when api >= 23
     * @param context
     * @return
     */
    private fun overlayPermRequest(context: Context): Boolean {
        var permNeeded = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                permNeeded = true
            }
        }
        return permNeeded
    }

    companion object {
        private var fpsConfig: FPSConfig? = null
        private var fpsFrameCallback: FPSFrameCallback? = null
        private var tinyCoach: TinyCoach? = null
        private val foregroundListener: Foreground.Listener = object : Foreground.Listener {
            override fun onBecameForeground() {
                tinyCoach!!.show()
            }

            override fun onBecameBackground() {
                tinyCoach!!.hide(false)
            }
        }

        /**
         * stops the frame callback and foreground listener
         * nulls out static variables
         * called from FPSLibrary in a static context
         * @param context
         */
        @JvmStatic
        fun hide(context: Context?) {
            // tell callback to stop registering itself
            fpsFrameCallback!!.setEnabled(
                false
            )
            Foreground.get(context)
                .removeListener(foregroundListener)
            // remove the view from the window
            tinyCoach!!.destroy()

            // null it all out
            tinyCoach = null
            fpsFrameCallback = null
            fpsConfig = null
        }
    }

    init {
        fpsConfig = FPSConfig()
    }
}