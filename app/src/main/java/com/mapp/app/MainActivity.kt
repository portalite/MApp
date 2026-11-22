package com.mapp.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.ResolveInfo
import android.widget.*
import android.view.Gravity

class MainActivity : Activity() {

    private lateinit var appList: ListView
    private lateinit var openButton: Button
    private lateinit var statusText: TextView

    private var installedApps = listOf<ResolveInfo>()
    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createInterface()
        loadApplications()
    }

    private fun createInterface() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 20)
            setBackgroundColor(0xFFF5F6FA.toInt())
        }

        val title = TextView(this).apply {
            text = "MApp"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(0xFF171727.toInt())
        }

        val subtitle = TextView(this).apply {
            text = "Uygulamalarını seç ve aç."
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 25)
        }

        appList = ListView(this).apply {
            choiceMode = ListView.CHOICE_MODE_SINGLE
        }

        statusText = TextView(this).apply {
            text = "Uygulamalar yükleniyor..."
            textSize = 14f
            setPadding(0, 15, 0, 15)
        }

        openButton = Button(this).apply {
            text = "Üzerinde Aç"
            isEnabled = false
            setOnClickListener {
                openSelectedApp()
            }
        }

        appList.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            openButton.isEnabled = true
            statusText.text =
                "${installedApps[position].loadLabel(packageManager)} seçildi."
        }

        layout.addView(title)
        layout.addView(subtitle)

        layout.addView(
            statusText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        layout.addView(
            appList,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        layout.addView(
            openButton,
            LinearLayout.LayoutParams(
                -1,
                55
            )
        )

        setContentView(layout)
    }

    private fun loadApplications() {

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        installedApps = packageManager
            .queryIntentActivities(launcherIntent, 0)
            .filter {
                it.activityInfo.packageName != packageName
            }
            .distinctBy {
                it.activityInfo.packageName
            }
            .sortedBy {
                it.loadLabel(packageManager).toString()
                    .lowercase()
            }

        val appNames = installedApps.map {
            it.loadLabel(packageManager).toString()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_single_choice,
            appNames
        )

        appList.adapter = adapter

        statusText.text =
            "${installedApps.size} uygulama bulundu."
    }

    private fun openSelectedApp() {

        if (selectedPosition < 0 ||
            selectedPosition >= installedApps.size
        ) {
            return
        }

        val packageName =
            installedApps[selectedPosition].activityInfo.packageName

        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            try {
                startActivity(launchIntent)
            } catch (e: Exception) {
                statusText.text =
                    "Uygulama açılamadı: ${e.message}"
            }
        } else {
            statusText.text =
                "Bu uygulama başlatılamıyor."
        }
    }
}