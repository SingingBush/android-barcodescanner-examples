Android Barcode Scanner
=======================

[![Build Status](https://travis-ci.org/SingingBush/android-barcodescanner-examples.png)](https://travis-ci.org/SingingBush/android-barcodescanner-examples)

This project shows how to add barcode scanning to your app via the following libraries:

- [ZXing](https://github.com/zxing/zxing/) ([Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0.html))
- [Zbar](http://zbar.sourceforge.net/) ([LGPL 2.1](http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html) license)
- [Scandit SDK](http://scandit.com) (commercial license - [pricing](http://www.scandit.com/pricing/))
- [Red Laser](http://redlaser.com/developers/) (commercial licence - [pricing](http://redlaser.com/developers/pricing/))

It is recommended that you use Android Studio 1.0.1 and Android Build Tools 21.1.2 or above when building the project.

## Note the following ##
- ZXing has been implemented by way of their _IntentIntegrator_ which prompts the user to install their Barcode Scanner app if it's not already on the phone (in a future update I will add an example of how to do this a better way)
- a physical device is required to use the barcode scanner, the emulator is not sufficient.
- an API Key is required to use the Scandit SDK - it needs to be placed in _ScanditActivity_
