
package com.mapp.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private lateinit var appList: ListView
    private lateinit var statusText: TextView
    private lateinit var workspace: LinearLayout

    private var installedApps = listOf<ResolveInfo>()
    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createInterface()
        loadApplications()
    }

    private fun createInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 25, 24, 20)
            setBackgroundColor(Color.rgb(245, 246, 250))
        }

        val title = TextView(this).apply {
            text = "MApp"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(23, 23, 39))
        }

        val subtitle = TextView(this).apply {
            text = "Uygulama çalışma alanı"
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 15)
        }

        statusText = TextView(this).apply {
            textSize = 14f
            setPadding(0, 10, 0, 10)
        }

        appList = ListView(this).apply {
            choiceMode = ListView.CHOICE_MODE_SINGLE
        }

        val openButton = Button(this).apply {
            text = "Çalışma Alanına Ekle"
            isEnabled = false
            setOnClickListener {
                showWorkspace()
            }
        }

        val workspaceTitle = TextView(this).apply {
            text = "ÇALIŞMA ALANI"
            textSize = 18f
            setTextColor(Color.BLACK)
            setPadding(0, 20, 0, 10)
        }

        workspace = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.WHITE)
        }

        val workspaceScroll = ScrollView(this).apply {
            addView(workspace)
        }

        appList.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            openButton.isEnabled = true
            statusText.text =
                "${installedApps[position].loadLabel(packageManager)} seçildi."
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(statusText)

        root.addView(
            appList,
            LinearLayout.LayoutParams( 
                -1, 0, 1f
            )
        )

        root.addView(openButton)
        root.addView(workspaceTitle)

        root.addView(
            workspaceScroll,
            LinearLayout.LayoutParams(
                -1, 180
            )
        )

        setContentView(root)
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
                it.loadLabel(packageManager).toString().lowercase()
            }

        val appNames = installedApps.map {
            it.loadLabel(packageManager).toString()
        }

        appList.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_single_choice,
            appNames
        )

        statusText.text = "${installedApps.size} uygulama bulundu."
    }

    private fun showWorkspace() {
        if (selectedPosition !in installedApps.indices) return

        val selectedApp = installedApps[selectedPosition]
        val appName = selectedApp.loadLabel(packageManager).toString()
        val packageId = selectedApp.activityInfo.packageName

        workspace.removeAllViews()

        val appTitle = TextView(this).apply {
            text = appName
            textSize = 22f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        val packageText = TextView(this).apply {
            text = packageId
            textSize = 12f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 12)
        }

        val info = TextView(this).apply {
            text = "Uygulama çalışma alanına eklendi. " +
                "Gerçek uygulamayı bu alanın içinde çalıştırmak " +
                "için sanallaştırma altyapısı gerekiyor."
            textSize = 14f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        workspace.addView(appTitle)
        workspace.addView(packageText)
        workspace.addView(info)

        statusText.text = "$appName çalışma alanına eklendi."
    }
}
