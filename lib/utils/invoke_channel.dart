

import 'package:flutter/services.dart';


class METHOD{
  static const String shareScreen = 'shareScreen';
  static const String openCall = 'openCall';
}

class InvokeChannel{
  static const String _channel = 'test_activity';

  static const platform = const MethodChannel(_channel);

  static invokeMethod({String methods,Map param}) async{
    try {
      await platform.invokeMethod(methods,param);
    } on PlatformException catch (e) {
      print(e.message);
    }
  }


}

