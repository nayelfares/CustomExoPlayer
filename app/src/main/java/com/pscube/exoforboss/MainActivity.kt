package com.pscube.exoforboss

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {
    private val hlsVideoUri = "https://shaadoow.net/media/video/EkTJ62wLCBAgiZyZxxGA2AuaBpQ6dCWHPJfiko2T.mp4"
    //private val hlsVideoUri = "https://videodelivery.net/d5f5eb5c7b8e8355a1378883b4ab3baf/manifest/video.m3u8"
    lateinit var player: SimpleExoPlayer
    lateinit var simpleExoPlayerView: PlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var exoPlayer= findViewById<ExoPlayer>(R.id.exoPlayer)
        exoPlayer.setLink(hlsVideoUri,"https://homepages.cae.wisc.edu/~ece533/images/arctichare.png")
       // init()
    }

    override fun onPause() {
        super.onPause()
            player.playWhenReady = false //to pause a video because now our video player is not in focus
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }

    fun init(){
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        // 2. Create a default LoadControl
        val loadControl: LoadControl = DefaultLoadControl()
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView)
        simpleExoPlayerView.setPlayer(player)
        // Measures bandwidth during playback. Can be null if not required.
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Shaadoow"), defaultBandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(Uri.parse(hlsVideoUri))
        if (hlsVideoUri.endsWith("mp4")) {
            val mediaSource = buildMediaSource(Uri.parse(hlsVideoUri))
            player.prepare(mediaSource)
        } else {
            player.prepare(hlsMediaSource)
        }
        simpleExoPlayerView.requestFocus()
        player.setPlayWhenReady(true)
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> Toast.makeText(this@MainActivity, "buffering", Toast.LENGTH_SHORT).show()
                    Player.STATE_READY -> Toast.makeText(this@MainActivity, "ready", Toast.LENGTH_SHORT).show()
                    Player.STATE_ENDED -> Toast.makeText(this@MainActivity, "Ended", Toast.LENGTH_SHORT).show()
                    Player.STATE_IDLE -> Toast.makeText(this@MainActivity, "Ideal", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}