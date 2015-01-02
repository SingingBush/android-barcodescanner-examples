Android Barcode Scanner
=======================

This project shows how to make use of both the [ZXing](https://github.com/zxing/zxing/) library and the [Scandit SDK](http://scandit.com) to read bar codes on an Android device.

It is recommended that you use Android Studio 1.0.1 and Android Build Tools 21.1.2 or above when building the project.

## Note the following ##
- ZXing has been implemented by way of their _IntentIntegrator_ which prompts the user to install their Barcode Scanner app if it's not already on the phone (in a future update I will add an example of how to do this a better way)
- a physical device is required to use the barcode scanner, the emulator is not sufficient.
- an API Key is required to use the Scandit SDK - it needs to be placed in _ScanditActivity_