package com.quantumde1.anilibriayou

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
Lived in buried memory
With fear-ridden self-consciousness
I'm just a lone prayer

Keep running on the cold track
The hate crazed thoughts just don't stop
As if a dream awake from the dark
The world deletes all I said to you

Overwrite this pain tearing me apart
toka de yuku
Frozen mind, ray of light
Ready for a trip to nowhere

Overwrite this pain tearing me apart
yasura ida
My last pray with no aim
My last, let me feel alive
*/

@OptIn(UnstableApi::class)
@Composable
fun LonePlayer(enuri: String) {
    val uri = Uri.decode(enuri)
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    delay(5000) // 5-second delay
                    activity?.window?.let { window ->
                        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                            controller.hide(WindowInsetsCompat.Type.systemBars())
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.window?.let { window ->
                WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        { context ->
            PlayerView(context).apply {
                player = exoPlayer
                setBackgroundColor(Color.Black.toArgb())
                keepScreenOn = true
            }
        }, modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}