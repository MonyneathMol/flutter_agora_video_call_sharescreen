import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    var navigationController: UINavigationController!
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
      ) -> Bool {
        
        let controller = self.window.rootViewController as! FlutterViewController
        linkNativeCode(controller: controller)
        GeneratedPluginRegistrant.register(with: self)
        
        self.navigationController = UINavigationController(rootViewController: controller)
        self.window.rootViewController = self.navigationController
        self.navigationController.setNavigationBarHidden(true, animated: false)
        self.window.makeKeyAndVisible()
        
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
        
    
    func linkNativeCode(controller: FlutterViewController) {
        setupMethodChannelForSurveyMonkey(controller: controller)
    }
    
    private func setupMethodChannelForSurveyMonkey(controller: FlutterViewController) {
        
        let surveyMonkeyChannel = FlutterMethodChannel.init(name: "test_activity", binaryMessenger: controller as! FlutterBinaryMessenger)
        surveyMonkeyChannel.setMethodCallHandler { (call, result) in
            if call.method == "shareScreen"{
                if #available(iOS 10.0, *) {
                    let vc = UIStoryboard.init(name: "Main", bundle: .main)
                        .instantiateViewController(withIdentifier: "ShareScreenViewController") as! ShareScreenViewController
                    
//                    guard let args = call.arguments else {
           //                    return
           ////                   "iOS could not recognize flutter arguments in method: (sendParams)")
           //                }
                           
           //                print("args : \(args)")
                    self.navigationController.modalPresentationStyle = .fullScreen
                    self.navigationController.pushViewController(vc, animated: true)
//                    self.navigationController.present(vc, animated: true, completion: nil)
                } else {
                    // Fallback on earlier versions
                }
//
//
               
//                   self.navigationController.pushViewController(vc, animated: true)
            }
        }
    }
}
