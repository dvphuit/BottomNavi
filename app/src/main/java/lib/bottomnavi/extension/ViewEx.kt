package lib.bottomnavi.extension

import android.content.res.Resources
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

inline val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ViewGroup.overShotTransition(){
    val transition = ChangeBounds()
    transition.interpolator = DecelerateInterpolator()
    transition.duration = 300
    TransitionManager.beginDelayedTransition(this, transition)
}