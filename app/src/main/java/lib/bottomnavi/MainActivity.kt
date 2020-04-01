package lib.bottomnavi

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import lib.bottomnavi.extension.resColor

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botNav.addOnTabSelectedListener(object: BotNav.TabSelectedListener() {
            override fun onTabSelected(position: Int) {
                Log.d("TEST", "tab selected: $position")
            }
        })

        val anim = ValueAnimator()
        anim.setIntValues(this.resColor(R.color.color_tv_home), this.resColor(R.color.color_tv_favourite), this.resColor(R.color.color_tv_bookmark), this.resColor(R.color.color_tv_profile))
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            frame.setBackgroundColor(value)
        }
        anim.repeatMode = ValueAnimator.REVERSE
        anim.repeatCount = -1
        anim.duration = 3500
        anim.start()
    }


}