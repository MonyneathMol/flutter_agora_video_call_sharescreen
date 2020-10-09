package com.example.agora_flutter_share

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {

    val CHANNEL : String = "test_activity"
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor, CHANNEL).setMethodCallHandler{ call, result -> // here is the error
            if (call.method == "shareScreen") {
//                val greetings = helloFromNativeCode()
//                result.success(greetings)
                startHelloActivity(result)
            }
        }
    }

    private fun startHelloActivity(result: MethodChannel.Result) {
        val intent = Intent(this, ShareScreenActivity::class.java)
//        intent.putExtra("hash", sdkData)
//        result = result
//        startActivityForResult(intent, REQUESTCODE)
        startActivity(intent)
    }

}
