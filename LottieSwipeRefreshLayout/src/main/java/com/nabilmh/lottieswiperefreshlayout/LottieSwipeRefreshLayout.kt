package com.nabilmh.lottieswiperefreshlayout
import SimpleSwipeRefreshLayout
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback

class LottieSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SimpleSwipeRefreshLayout(context, attrs, defStyle) {

    private var animationFile: Int = -1
    private val lottieAnimationView by lazy {
        LottieAnimationView(context).apply {
            if (animationFile == -1) {
                throw IllegalStateException("Could not resolve an animation for your pull to refresh layout")
            }

            setAnimation(animationFile)
            repeatCount = LottieDrawable.INFINITE
            val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, context.resources.displayMetrics).toInt()

            layoutParams = LayoutParams(ViewGroup.LayoutParams(size, size))

            val density = context.resources.displayMetrics.density
            val circle = ShapeDrawable(OvalShape())
            circle.paint.color = Color.WHITE
            ViewCompat.setElevation(this, ELEVATION * density)

            background = circle
        }
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LottieSwipeRefreshLayout, defStyle, 0).let { style ->
            animationFile = style.getResourceId(R.styleable.LottieSwipeRefreshLayout_lottie_rawRes, -1)
            addView(lottieAnimationView)
            style.recycle()
        }

        addProgressListener {
            lottieAnimationView.progress = it
        }

        addTriggerListener {
            lottieAnimationView.resumeAnimation()
        }
    }

    fun setColorSchemeResources(color: Int) {
        val filter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
        lottieAnimationView.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER, callback)
    }

    override fun stopRefreshing() {
        super.stopRefreshing()
        lottieAnimationView.pauseAnimation()
    }

    override fun startRefreshing() {
        super.startRefreshing()
        lottieAnimationView.resumeAnimation()
    }
}
