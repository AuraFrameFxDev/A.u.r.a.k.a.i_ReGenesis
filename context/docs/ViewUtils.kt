package dev.aurakai.auraframefx.system.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.system.utils.ViewUtils.setViewTag

/**
 * Utility class for view-related operations.
 */
object ViewUtils {
    private const val TAG = "ViewUtils"

    // Standard interpolators
    val FAST_OUT_SLOW_IN: Interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
    val FAST_OUT_LINEAR_IN: Interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
    val LINEAR_OUT_SLOW_IN: Interpolator = PathInterpolator(0f, 0f, 0.2f, 1f)

    /**
     * Set the visibility of a view with optional animation.
     *
     * @param view The view to modify.
     * @param visible True to make the view visible, false to hide it.
     * @param animate Whether to animate the transition.
     * @param duration The duration of the animation in milliseconds.
     * @param onEnd Optional callback when the animation ends.
     */
    fun setVisibility(
        view: View,
        visible: Boolean,
        animate: Boolean = true,
        duration: Long = 200,
        onEnd: (() -> Unit)? = null,
    ) {
        if (view.isVisible == visible) {
            onEnd?.invoke()
            return
        }

        if (!animate) {
            view.visibility = if (visible) View.VISIBLE else View.GONE
            onEnd?.invoke()
            return
        }

        if (visible) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(LINEAR_OUT_SLOW_IN)
                .withEndAction {
                    onEnd?.invoke()
                }
                .start()
        } else {
            view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(FAST_OUT_LINEAR_IN)
                .withEndAction {
                    view.visibility = View.GONE
                    view.alpha = 1f
                    onEnd?.invoke()
                }
                .start()
        }
    }

    /**
     * Recursively find all views of a specific type in a view hierarchy.
     *
     * @param root The root view to search from.
     * @param type The class of the view type to find.
     * @param includeInvisible Whether to include invisible views in the results.
     * @return A list of matching views.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> findViewsByType(
        root: View,
        type: Class<T>,
        includeInvisible: Boolean = false,
    ): List<T> {
        val result = mutableListOf<T>()

        if (type.isInstance(root) && (includeInvisible || root.visibility == View.VISIBLE)) {
            result.add(root as T)
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                result.addAll(findViewsByType(child, type, includeInvisible))
            }
        }

        return result
    }

    /**
     * Recursively find the first view of a specific type in a view hierarchy.
     *
     * @param root The root view to search from.
     * @param type The class of the view type to find.
     * @param includeInvisible Whether to include invisible views in the search.
     * @return The first matching view, or null if not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> findFirstViewByType(
        root: View,
        type: Class<T>,
        includeInvisible: Boolean = false,
    ): T? {
        if (type.isInstance(root) && (includeInvisible || root.visibility == View.VISIBLE)) {
            return root as T
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                val result = findFirstViewByType(child, type, includeInvisible)
                if (result != null) {
                    return result
                }
            }
        }

        return null
    }

    /**
     * Recursively find views with a specific tag in a view hierarchy.
     *
     * @param root The root view to search from.
     * @param tag The tag to search for.
     * @param includeInvisible Whether to include invisible views in the results.
     * @return A list of views with the specified tag.
     */
    fun findViewsWithTag(
        root: View,
        tag: String,
        includeInvisible: Boolean = false,
    ): List<View> {
        val result = mutableListOf<View>()

        if (root.getTag(R.id.quick_settings_tag) == tag && (includeInvisible || root.visibility == View.VISIBLE)) {
            result.add(root)
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                result.addAll(findViewsWithTag(child, tag, includeInvisible))
            }
        }

        return result
    }

    /**
     * Set a tag on a view using a resource ID to avoid tag conflicts.
     *
     * @param view The view to set the tag on.
     * @param tag The tag value.
     */
    fun setViewTag(view: View, tag: String) {
        view.setTag(R.id.quick_settings_tag, tag)
    }

    /**
     * Get a tag from a view that was set with [setViewTag].
     *
     * @param view The view to get the tag from.
     * @return The tag value, or null if not set.
     */
    fun getViewTag(view: View): String? {
        return view.getTag(R.id.quick_settings_tag) as? String
    }

    /**
     * Remove all child views from a ViewGroup.
     *
     * @param viewGroup The ViewGroup to clear.
     */
    fun removeAllViews(viewGroup: ViewGroup) {
        viewGroup.removeAllViews()
    }

    /**
     * Recursively enable or disable all child views in a view hierarchy.
     *
     * @param view The root view.
     * @param enabled Whether to enable or disable the views.
     */
    fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    /**
     * Wait for the view to be laid out and then execute a callback.
     *
     * @param view The view to wait for.
     * @param callback The callback to execute when the view is laid out.
     */
    fun doOnLayout(view: View, callback: (View) -> Unit) {
        if (view.isLaidOut) {
            callback(view)
            return
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (view.isLaidOut) {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback(view)
                }
            }
        })
    }

    /**
     * Set a click listener that prevents rapid double-clicks.
     *
     * @param view The view to set the click listener on.
     * @param interval The minimum interval between clicks in milliseconds.
     * @param onClick The click listener.
     */
    fun setOnSingleClickListener(view: View, interval: Long = 500, onClick: (View) -> Unit) {
        var lastClickTime: Long = 0

        view.setOnClickListener { v ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= interval) {
                lastClickTime = currentTime
                onClick(v)
            }
        }
    }

    /**
     * Measure the view and its content to determine the measured width and height.
     *
     * @param view The view to measure.
     * @return A Pair containing the measured width and height.
     */
    fun measureView(view: View): Pair<Int, Int> {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return Pair(view.measuredWidth, view.measuredHeight)
    }

    /**
     * Set the view's width and height in pixels.
     *
     * @param view The view to resize.
     * @param width The width in pixels.
     * @param height The height in pixels.
     */
    fun setViewSize(view: View, width: Int, height: Int) {
        val params = view.layoutParams ?: ViewGroup.LayoutParams(width, height)
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    /**
     * Set the view's width in pixels.
     *
     * @param view The view to resize.
     * @param width The width in pixels.
     */
    fun setViewWidth(view: View, width: Int) {
        val params =
            view.layoutParams ?: ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.width = width
        view.layoutParams = params
    }

    /**
     * Set the view's height in pixels.
     *
     * @param view The view to resize.
     * @param height The height in pixels.
     */
    fun setViewHeight(view: View, height: Int) {
        val params =
            view.layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height)
        params.height = height
        view.layoutParams = params
    }

    /**
     * Set the view's padding in pixels.
     *
     * @param view The view to set padding on.
     * @param padding The padding in pixels.
     */
    fun setViewPadding(view: View, padding: Int) {
        view.setPadding(padding, padding, padding, padding)
    }

    /**
     * Set the view's padding in pixels.
     *
     * @param view The view to set padding on.
     * @param left The left padding in pixels.
     * @param top The top padding in pixels.
     * @param right The right padding in pixels.
     * @param bottom The bottom padding in pixels.
     */
    fun setViewPadding(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        view.setPadding(left, top, right, bottom)
    }

    /**
     * Set the view's background with a drawable resource.
     *
     * @param view The view to set the background on.
     * @param drawableRes The drawable resource ID.
     */
    fun setBackgroundResource(view: View, @DrawableRes drawableRes: Int) {
        view.background = ContextCompat.getDrawable(view.context, drawableRes)
    }

    /**
     * Set the view's background with a drawable.
     *
     * @param view The view to set the background on.
     * @param drawable The drawable to set as background.
     */
    fun setBackgroundDrawable(view: View, drawable: Drawable?) {
        ViewCompat.setBackground(view, drawable)
    }

    /**
     * Set the view's background color with a color resource.
     *
     * @param view The view to set the background color on.
     * @param colorRes The color resource ID.
     */
    fun setBackgroundColorResource(view: View, @androidx.annotation.ColorRes colorRes: Int) {
        view.setBackgroundColor(ContextCompat.getColor(view.context, colorRes))
    }

    /**
     * Set the view's background color.
     *
     * @param view The view to set the background color on.
     * @param color The color to set.
     */
    fun setBackgroundColor(view: View, @androidx.annotation.ColorInt color: Int) {
        view.setBackgroundColor(color)
    }

    /**
     * Set the view's elevation in pixels.
     *
     * @param view The view to set elevation on.
     * @param elevation The elevation in pixels.
     */
    fun setElevation(view: View, elevation: Float) {
        ViewCompat.setElevation(view, elevation)
    }
}
