package com.example.agora_flutter_share

import android.os.Bundle
import android.util.Log
import android.view.View
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.ss.ScreenSharingClient
import io.agora.rtc.video.VideoEncoderConfiguration
import io.flutter.app.FlutterActivity

class ShareScreenActivity : FlutterActivity() {


    private var isClickReceiveCall = false
    private var mRtcEngine: RtcEngine? = null
    private var isClickShareScreen = true
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


    fun onClickScreenSharing(view: View) {

        if (isClickShareScreen) {
            mRtcEngine?.leaveChannel()
            startSharingScreen()
            isOpeningShareScreen = true
        } else {
            isOpeningShareScreen = false
            endSharingScreen()
        }
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
        isClickShareScreen = false
    }

    private fun endSharingScreen() {
        mSSClient!!.stop(applicationContext)
        isClickShareScreen = true
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


    override fun onDestroy() {
        super.onDestroy()
        if (isOpeningShareScreen)
            endSharingScreen();
        RtcEngine.destroy()
        isClickReceiveCall = true
        mRtcEngine!!.leaveChannel()

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
                    "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e)
            )
        }
    }

    private var mRtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
//                setupRemoteVideo(uid)
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                if (uid == 2) {
//                    setupShareScreenRemoteView(uid)
                }
            }
        }
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