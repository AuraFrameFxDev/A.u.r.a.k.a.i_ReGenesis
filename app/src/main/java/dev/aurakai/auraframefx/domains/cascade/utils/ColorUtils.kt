package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt

/**
 * Utility class for color-related operations.
 */
object ColorUtils {
    private const val TAG = "ColorUtils"

    /**
     * Parse a color string and return the corresponding color-int.
     * If the string cannot be parsed, returns the default color.
     *
     * @param colorString The color string (e.g., "#RRGGBB" or "#AARRGGBB").
     * @param defaultColor The default color to return if parsing fails.
     * @return The parsed color or the default color.
     */
    @ColorInt
    fun parseColor(colorString: String?, @ColorInt defaultColor: Int): Int {
        return try {
            (colorString ?: return defaultColor).toColorInt()
        } catch (e: IllegalArgumentException) {
            defaultColor
        }
    }

    /**
     * Adjust the alpha component of a color.
     *
     * @param color The original color.
     * @param alpha The alpha value (0-255).
     * @return The color with the new alpha value.
     */
    @ColorInt
    fun withAlpha(@ColorInt color: Int, alpha: Int): Int {
        return Color.argb(
            alpha.coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    /**
     * Adjust the alpha component of a color by a factor.
     *
     * @param color The original color.
     * @param factor The alpha factor (0.0f - 1.0f).
     * @return The color with adjusted alpha.
     */
    @ColorInt
    fun withAlphaFactor(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor.coerceIn(0f, 1f)).toInt()
        return withAlpha(color, alpha)
    }

    /**
     * Create a gradient drawable with the given colors and corner radius.
     *
     * @param colors The colors for the gradient.
     * @param cornerRadius The corner radius in pixels.
     * @param orientation The orientation of the gradient.
     * @return A GradientDrawable with the specified properties.
     */
    fun createGradientDrawable(
        @ColorInt colors: IntArray,
        cornerRadius: Float = 0f,
        orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT,
    ): GradientDrawable {
        return GradientDrawable(orientation, colors).apply {
            this.cornerRadius = cornerRadius
        }
    }

    /**
     * Create a ripple drawable with the given content and ripple color.
     *
     * @param content The content drawable.
     * @param rippleColor The ripple color.
     * @param mask The mask drawable for the ripple.
     * @return A RippleDrawable with the specified properties.
     */
    fun createRippleDrawable(
        content: Drawable?,
        @ColorInt rippleColor: Int,
        mask: Drawable? = null,
    ): android.graphics.drawable.RippleDrawable {
        return android.graphics.drawable.RippleDrawable(
            android.content.res.ColorStateList.valueOf(rippleColor),
            content,
            mask
        )
    }

    /**
     * Create a selectable item background with the given color and corner radius.
     *
     * @param context The context.
     * @param color The background color.
     * @param cornerRadius The corner radius in pixels.
     * @param rippleColor The ripple color. If null, a default will be used.
     * @return A drawable that can be used as a background.
     */
    fun createSelectableBackground(
        context: Context,
        @ColorInt color: Int,
        cornerRadius: Float = 0f,
        @ColorInt rippleColor: Int? = null,
    ): Drawable {
        val bgColor = withAlpha(color, (Color.alpha(color) * 0.5f).toInt())
        val defaultRipple = Color.parseColor("#29FFFFFF") // Default highlight color

        val content = GradientDrawable().apply {
            setColor(bgColor)
            this.cornerRadius = cornerRadius
        }

        return createRippleDrawable(
            content = content,
            rippleColor = rippleColor ?: defaultRipple,
            mask = GradientDrawable().apply {
                setColor(Color.WHITE)
                this.cornerRadius = cornerRadius
            }
        )
    }

    /**
     * Set the background of a view with optional insets and corner radius.
     *
     * @param view The view to set the background on.
     * @param drawable The drawable to use as background.
     * @param insetLeft Left inset in pixels.
     * @param insetTop Top inset in pixels.
     * @param insetRight Right inset in pixels.
     * @param insetBottom Bottom inset in pixels.
     */
    fun setBackgroundWithInset(
        view: View,
        drawable: Drawable?,
        insetLeft: Int = 0,
        insetTop: Int = 0,
        insetRight: Int = 0,
        insetBottom: Int = 0,
    ) {
        view.background =
            if (insetLeft != 0 || insetTop != 0 || insetRight != 0 || insetBottom != 0) {
                InsetDrawable(drawable, insetLeft, insetTop, insetRight, insetBottom)
            } else {
                drawable
            }
    }

    /**
     * Check if a color is light (suitable for dark text).
     *
     * @param color The color to check.
     * @return True if the color is light, false otherwise.
     */
    fun isColorLight(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) > 0.5
    }

    /**
     * Get an appropriate text color for the given background color.
     *
     * @param backgroundColor The background color.
     * @param lightColor The text color to use for light backgrounds.
     * @param darkColor The text color to use for dark backgrounds.
     * @return The appropriate text color.
     */
    @ColorInt
    fun getTextColorForBackground(
        @ColorInt backgroundColor: Int,
        @ColorInt lightColor: Int = Color.BLACK,
        @ColorInt darkColor: Int = Color.WHITE,
    ): Int {
        return if (isColorLight(backgroundColor)) lightColor else darkColor
    }

    /**
     * Get a color with the given resource ID, or a default color if not found.
     *
     * @param context The context.
     * @param colorRes The color resource ID.
     * @param defaultColor The default color to return if the resource is not found.
     * @return The color value.
     */
    @ColorInt
    fun getColorSafe(
        context: Context,
        @ColorRes colorRes: Int,
        @ColorInt defaultColor: Int = Color.BLACK,
    ): Int {
        return try {
            ContextCompat.getColor(context, colorRes)
        } catch (e: Exception) {
            defaultColor
        }
    }

    /**
     * Get a drawable with the given resource ID, or null if not found.
     *
     * @param context The context.
     * @param drawableRes The drawable resource ID.
     * @return The drawable, or null if not found.
     */
    fun getDrawableSafe(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return try {
            ContextCompat.getDrawable(context, drawableRes)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Create a color state list with the given colors for different states.
     *
     * @param defaultColor The default color.
     * @param pressedColor The color when pressed.
     * @param checkedColor The color when checked.
     * @param disabledColor The color when disabled.
     * @return A ColorStateList with the specified colors.
     */
    fun createColorStateList(
        @ColorInt defaultColor: Int,
        @ColorInt pressedColor: Int = withAlpha(defaultColor, 0xCC),
        @ColorInt checkedColor: Int = defaultColor,
        @ColorInt disabledColor: Int = withAlpha(defaultColor, 0x4D),
    ): android.content.res.ColorStateList {
        return android.content.res.ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked, android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf()
            ),
            intArrayOf(
                checkedColor,
                pressedColor,
                disabledColor,
                defaultColor
            )
        )
    }
}

