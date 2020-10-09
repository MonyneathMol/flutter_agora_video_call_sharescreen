//
//  SampleHandler.swift
//  Agora-Broadcast
//
//  Created by Monyneath Mol on 10/9/20.
//

import ReplayKit

class SampleHandler: RPBroadcastSampleHandler {

    override func broadcastStarted(withSetupInfo setupInfo: [String : NSObject]?) {
//       if let setupInfo = setupInfo, let channel = setupInfo["channelName"] as? String {
//           AgoraUploader.startBroadcast(to: "a")
//       } else {
           AgoraUploader.startBroadcast(to: "a")
//       }
    }
    
    override func broadcastPaused() {
        // User has requested to pause the broadcast. Samples will stop being delivered.
    }
    
    override func broadcastResumed() {
        // User has requested to resume the broadcast. Samples delivery will resume.
    }
    
    override func broadcastFinished() {
        // User has requested to finish the broadcast.
        AgoraUploader.stopBroadcast()
    }
    
    override func processSampleBuffer(_ sampleBuffer: CMSampleBuffer, with sampleBufferType: RPSampleBufferType) {
        switch sampleBufferType {
        case RPSampleBufferType.video:
            // Handle video sample buffer
            AgoraUploader.sendVideoBuffer(sampleBuffer)
            break
        case RPSampleBufferType.audioApp:
            // Handle audio sample buffer for app audio
            AgoraUploader.sendAudioAppBuffer(sampleBuffer)
            break
        case RPSampleBufferType.audioMic:
            // Handle audio sample buffer for mic audio
            AgoraUploader.sendAudioMicBuffer(sampleBuffer)
            break
        @unknown default:
            // Handle other sample buffer types
            fatalError("Unknown type of sample buffer")
        }
    }
}
