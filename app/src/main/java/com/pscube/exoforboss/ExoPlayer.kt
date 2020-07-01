package com.pscube.exoforboss

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
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

class ExoPlayer(context: Context,  attrs: AttributeSet?): RelativeLayout(context,  attrs) {
    private var url           : String? = null
    private var coverImageUrl : String? = null
    lateinit var player: SimpleExoPlayer
    lateinit var simpleExoPlayerView: PlayerView
    lateinit var coverImage:ImageView
    private fun init() {
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl: LoadControl = DefaultLoadControl()
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView)
        coverImage = findViewById(R.id.coverImage)
        simpleExoPlayerView.player=player

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Shaadoow"), defaultBandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(Uri.parse(url))
        //Load Cover Image
        Glide.with(context)
                .load(coverImageUrl)
                .into(coverImage)
        //
        coverImage.setOnClickListener {
            coverImage.visibility= View.GONE
            if (url!!.endsWith("mp4")) {
                val mediaSource = buildMediaSource(Uri.parse(url))
                player.prepare(mediaSource)
            } else {
                player.prepare(hlsMediaSource)
            }
            player.playWhenReady=true
        }
        simpleExoPlayerView.requestFocus()
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> Toast.makeText(context, "buffering", Toast.LENGTH_SHORT).show()
                    Player.STATE_READY -> Toast.makeText(context, "ready", Toast.LENGTH_SHORT).show()
                    Player.STATE_ENDED -> Toast.makeText(context, "Ended", Toast.LENGTH_SHORT).show()
                    Player.STATE_IDLE -> Toast.makeText(context, "Ideal", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, "Shaadoow")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.exo_player, this, true)
    }

    fun setLink(link:String,coverImagelink:String){
        this.url           = link
        this.coverImageUrl = coverImagelink
        init()
    }

}