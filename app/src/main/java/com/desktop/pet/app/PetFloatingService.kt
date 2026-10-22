package com.desktop.pet.app

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class PetFloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private val dialogs = listOf("記得喝水喔！", "今天辛苦了～加油！", "不要忘記伸懶腰休息一下！", "今天也是美好的一天☀️", "我有小禮物給你！", "一起加油吧！", "保持好心情喔！", "沒關係他開心就好！", "我好興奮啊啊啊！")

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_pet, null)

        val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        val txtBadge = floatingView.findViewById<TextView>(R.id.txtPetBadge)
        txtBadge.text = "puma"

        // 自由拖拽與點擊互動
        floatingView.setOnTouchListener(object : View.OnTouchListener {
            private var isClick = true

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isClick = true
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                            isClick = false
                        }
                        
                        params.x = initialX + dx
                        params.y = initialY + dy
                        windowManager.updateViewLayout(floatingView, params)
                        
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            showRandomDialog()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun showRandomDialog() {
        val randomText = dialogs[Random.nextInt(dialogs.size)]
        Toast.makeText(this, "puma: $randomText", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}