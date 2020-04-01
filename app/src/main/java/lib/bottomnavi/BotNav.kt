package lib.bottomnavi

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import lib.bottomnavi.extension.*


class BotNav(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    View.OnClickListener {

    private var selectedPosition = 0
    private var prePosition = -1
    private val menus: List<Menu>

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BotNav)
        val menu = a.getResourceId(R.styleable.BotNav_menu, -1)
        menus = MenuParser(context, menu).menus
        initMenu()
        a.recycle()
    }

    private fun initMenu() {
        menus.forEach {
            addChild(it.title, it.icon, it.color)
        }
        alignCenterVertical()
        alignStartEndViews()
        setConstraintEachChild()
        onClick(this[0])
    }

    private fun alignStartEndViews() {
        val set1 = ConstraintSet()
        set1.clone(this)
        val firstViewId = this[0].id
        set1.connect(firstViewId, START, this.id, START, 0)
        set1.setHorizontalChainStyle(firstViewId, CHAIN_PACKED)
        set1.applyTo(this)

        val set2 = ConstraintSet()
        set2.clone(this)
        val lastViewId = this[this.childCount - 1].id
        set2.connect(lastViewId, END, this.id, END, 0)
        set2.applyTo(this)
    }

    private fun alignCenterVertical() {
        for (i in menus.indices) {
            val set = ConstraintSet()
            val viewId = this[i].id
            set.clone(this)
            set.centerVertically(viewId, this.id)
            set.connect(viewId, BOTTOM, this.id, BOTTOM, 0)
            set.connect(viewId, TOP, this.id, TOP, 0)
            set.applyTo(this)
        }
    }

    private fun setConstraintEachChild() {
        for (i in 0 until menus.lastIndex) {
            val set = ConstraintSet()
            val curViewId = this[i].id
            val nextViewId = this[i + 1].id
            set.clone(this)
            set.connect(curViewId, END, nextViewId, START, 8.dp)
            set.connect(nextViewId, START, curViewId, END, 8.dp)
            set.applyTo(this)
        }
    }

    private fun addChild(title: CharSequence, icon: Int, color: Int) {
        val group = LinearLayout(context)
        group.id = View.generateViewId()
        group.gravity = Gravity.CENTER
        group.orientation = LinearLayout.HORIZONTAL
        group.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        (group.layoutParams as LinearLayout.LayoutParams).setMargins(4.dp, 0, 4.dp, 0)
        group.setPadding(8.dp, 8.dp, 8.dp, 8.dp)
        group.background = ContextCompat.getDrawable(context, R.drawable.bg_bot_navi_item)
        group.setOnClickListener(this)

        val img = ImageView(context)
        img.setImageResource(icon)
        img.setColorFilter(Color.DKGRAY)
        val imgParams = LinearLayout.LayoutParams(20.dp, 20.dp)
        imgParams.setMargins(8.dp, 0, 8.dp, 0)
        img.layoutParams = imgParams

        val tv = TextView(context)
        tv.gone()
        tv.text = title
        tv.setTextColor(color)
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        val tvParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        tvParams.setMargins(0.dp, 0, 8.dp, 0)
        tv.layoutParams = tvParams

        group.addView(img)
        group.addView(tv)

        addView(group)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    private fun unHighlight(index: Int) {
        if (index == -1) return
        val parent = this[index] as ViewGroup
        (parent.background as GradientDrawable).color = null
        parent[1].gone()
        (parent[0] as ImageView).setColorFilter(Color.DKGRAY)
    }

    private fun highlight(index: Int) {
        val parent = this[index] as ViewGroup
        val tv = parent[1] as TextView
        tv.visible()
        val anim = ValueAnimator()
        anim.setIntValues(Color.DKGRAY, menus[index].color)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            (parent.background as GradientDrawable).setColor(value.lighten)
            tv.setTextColor(value)
            (parent[0] as ImageView).setColorFilter(value)
        }
        anim.duration = 0
        anim.start()
    }

    override fun onClick(v: View?) {
        selectedPosition = v!!.id - 1
        listener?.onPreviousTabSelected(prePosition)
        unHighlight(prePosition)
        if (prePosition == selectedPosition) {
            listener?.onTabReselected()
            highlight(selectedPosition)
        }
        if (prePosition != selectedPosition) {
            prePosition = selectedPosition
            listener?.onTabSelected(selectedPosition)
            highlight(selectedPosition)
            this.overShotTransition()
        }
    }

    private var listener: TabSelectedListener? = null

    fun addOnTabSelectedListener(listener: TabSelectedListener) {
        this.listener = listener
    }

    abstract class TabSelectedListener {
        open fun onTabSelected(position: Int) {}
        open fun onTabReselected() {}
        open fun onPreviousTabSelected(position: Int) {}
    }
}