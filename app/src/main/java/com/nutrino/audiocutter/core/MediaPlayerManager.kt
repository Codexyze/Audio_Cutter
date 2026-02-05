package com.nutrino.audiocutter.core

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

class MediaPlayerManager @Inject constructor(private val exoPlayer: ExoPlayer) {

    fun getPlayer(): ExoPlayer {
        return exoPlayer
    }

    fun initializePlayer(uri: Uri) {
        exoPlayer.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false
        }
    }
    fun releasePlayer() {
        exoPlayer.apply {
            this.stop()
            this.release()
        }
    }


}