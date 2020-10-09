package com.example.agora_flutter_share

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.ss.Constant
import io.agora.rtc.ss.ScreenSharingClient
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtm.*
import io.flutter.app.FlutterActivity

class ShareScreenActivity : FlutterActivity() {



    private var isClickReceiveCall = false
    private var mRtcEngine: RtcEngine? = null
    private var status_calling: String = ""
    private var isClickShareScreen = true
    private var mVEC: VideoEncoderConfiguration? = null
    private var mVECShareScreen: VideoEncoderConfiguration? = null
    private var mSSClient: ScreenSharingClient? = null
    private var isOpeningShareScreen: Boolean = false;

    private val mListener: ScreenSharingClient.IStateListener =
            object : ScreenSharingClient.IStateListener {
                override fun onError(error: Int) {
                }

                override fun onTokenWillExpire() {
                    mSSClient!!.renewToken(null)
                }
            }


    private var mRtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                if (uid == com.example.agora_flutter_share.utils.Constants.SCREEN_SHARE_UID) {
                    setupShareScreenRemoteView(uid)
                }
            }
        }
    }





    fun onClickScreenSharing(view: View) {

        if (isClickShareScreen) {
            startSharingScreen()
            isOpeningShareScreen = true
        } else {
            isOpeningShareScreen = false
            endSharingScreen()
        }
    }


    private fun initAgoraEngineAndJoinChannel() {

        setupVideoProfile()
        setupLocalVideo()



    }

    private fun startSharingScreen() {
        var channelName = "a"


        setupShareScreenVideoProfile()
        mSSClient!!.start(
                applicationContext,
                resources.getString(R.string.agora_app_id),
                null,
                channelName,
                0,
                mVECShareScreen!!
        )
//        sendPeerMessage("Start ShareScreen")
        isClickShareScreen = false
    }

    private fun endSharingScreen() {
        mSSClient!!.stop(applicationContext)
        isClickShareScreen = true
//        mainShare_preview.removeAllViews()
//        local_video_view_container.visibility = View.VISIBLE
//        remote_video_view_container.visibility = View.VISIBLE
        setupVideoProfile()
        setupLocalVideo()
    }

    private fun setupShareScreenVideoProfile() {
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine!!.enableVideo()
        mVECShareScreen = VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        )
        mRtcEngine!!.setVideoEncoderConfiguration(mVECShareScreen)
        mRtcEngine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
    }

    private fun setupShareScreenRemoteView(uid: Int) {
        val container = findViewById(R.id.mainShare_preview) as FrameLayout
        val ssV = RtcEngine.CreateRendererView(applicationContext)
        ssV.setZOrderOnTop(true)
        ssV.setZOrderMediaOverlay(true)
        container!!.addView(
                ssV,
                FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        )
        mRtcEngine!!.setupRemoteVideo(
                VideoCanvas(
                        ssV,
                        VideoCanvas.RENDER_MODE_FIT,
                        uid
                )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isOpeningShareScreen)
            endSharingScreen();
        RtcEngine.destroy()
        isClickReceiveCall = true
        mRtcEngine!!.leaveChannel()

    }

    fun onLocalVideoMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(
                    resources.getColor(R.color.colorPrimary),
                    PorterDuff.Mode.MULTIPLY
            )
        }

        mRtcEngine!!.muteLocalVideoStream(iv.isSelected)

        val container = findViewById(R.id.local_video_view_container) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!iv.isSelected)
        surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
    }

    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(
                    resources.getColor(R.color.colorPrimary),
                    PorterDuff.Mode.MULTIPLY
            )
        }

        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    fun onSwitchCameraClicked(view: View) {
        mRtcEngine!!.switchCamera()
    }

    fun onEncCallClicked(view: View) {
        isClickReceiveCall = true
        finish()
    }

    fun onAccCallClicked(view: View) {
        joinChannel()
        status_calling = "Accept Call"
        view.visibility = View.INVISIBLE
        isClickReceiveCall = true
//        local_video_view_container.visibility = View.VISIBLE
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine =
                    RtcEngine.create(
                            baseContext,
                            getString(R.string.agora_app_id),
                            mRtcEventHandler
                    )
        } catch (e: Exception) {

            throw RuntimeException(
                    "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(
                            e
                    )
            )
        }
    }

    private fun setupVideoProfile() {
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine!!.enableVideo()
        mVEC = VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        )
        mRtcEngine!!.setVideoEncoderConfiguration(mVEC)
        mRtcEngine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
    }

    private fun setupLocalVideo() {
        val container = findViewById<FrameLayout>(R.id.local_video_view_container)
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }


    private fun setupRemoteVideo(uid: Int) {
        val container = findViewById(R.id.remote_video_view_container) as FrameLayout

        if (container.childCount >= 1) {
            return
        }
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        container.addView(surfaceView)

        mRtcEngine!!.setupRemoteVideo(
                VideoCanvas(
                        surfaceView,
                        VideoCanvas.RENDER_MODE_FIT,
                        uid
                )
        )

        surfaceView.tag = uid
        val tipMsg = findViewById<TextView>(R.id.quick_tips_when_use_agora_sdk) // optional UI
        tipMsg.visibility = View.GONE
    }


    private fun joinChannel() {
        var channelName = "a"

        mRtcEngine!!.joinChannel(
                null,
                channelName,
                "Extra Optional Data",
                0
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_screen)
        mSSClient = ScreenSharingClient.getInstance()
        mSSClient!!.setListener(mListener)
//        initAgoraEngineAndJoinChannel()
        initializeAgoraEngine()
    }
}