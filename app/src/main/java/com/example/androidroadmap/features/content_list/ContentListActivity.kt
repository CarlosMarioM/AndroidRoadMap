package com.example.androidroadmap.features.content_list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContentListRoute(
                onNavigate = { args ->
                    startActivity(
                        Intent(this, ContentListActivity::class.java).apply {
                            putExtra("level", args.level.name)
                            putExtra("parentId", args.parentId)
                            putExtra("title", args.title)
                        }
                    )
                }
            )
        }
    }
}
