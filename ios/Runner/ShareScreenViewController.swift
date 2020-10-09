//
//  ShareScreenViewController.swift
//  Runner
//
//  Created by Monyneath Mol on 10/8/20.
//

import UIKit
import ReplayKit
import SpriteKit

@available(iOS 10.0, *)
class ShareScreenViewController: UIViewController {
    fileprivate weak var broadcastActivityVC : RPBroadcastActivityViewController?
    fileprivate weak var broadcastController : RPBroadcastController?
    fileprivate weak var cameraPreview       : UIView?

    private lazy var broadcastButton: UIView! = {
        if #available(iOS 12.0, *) {
            let frame = CGRect(x: 0,
                               y:view.frame.size.height - 60,
                               width: 60,
                               height: 60)

            let systemBroadcastPicker = RPSystemBroadcastPickerView(frame: frame)
            systemBroadcastPicker.autoresizingMask = [.flexibleTopMargin, .flexibleRightMargin]
            
            if let url = Bundle.main.url(forResource: "AgoraApp-Broadcast", withExtension: "appex", subdirectory: "PlugIns") {
                if let bundle = Bundle(url: url) {
                    let button = UIButton()
                    systemBroadcastPicker.preferredExtension = bundle.bundleIdentifier
                }
            }
            return systemBroadcastPicker
        }else {
            return UIButton()
        }
    }()

    
    

    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.addSubview(broadcastButton)
        // Do any additional setup after loading the view.
    }
    
    @IBAction func dismiss(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
