package com.dreamsoft.desoft20.webview

object WebViewCallbacks {

    // Legacy compatibility callbacks
    const val SET_BARCODE_FIELD = "codeb" // codeb(fieldId, result)
    const val SET_BLUETOOTH_FIELD = "setBarcodeFieldValue" // Custom field setter

    // Modern callbacks - implement these in your web app
    const val ON_PRINT_RESULT = "AndroidInterface.onPrintResult"
    //const val ON_PRINT_ERROR = "AndroidInterface.onPrintError"

    const val ON_LOCATION_RESULT = "AndroidInterface.onLocationResult"

    const val ON_BARCODE_Result = "AndroidInterface.onBarcodeResult"
    const val ON_BARCODE_ERROR = "AndroidInterface.onBarcodeError"
    const val SET_BARCODE_FIELD_VALUE = "AndroidInterface.setBarcodeFieldValue"

    const val ON_BLUETOOTH_DEVICE_SELECTED = "AndroidInterface.onBluetoothDeviceSelected"
    const val ON_BLUETOOTH_ERROR = "AndroidInterface.onBluetoothError"
    const val SET_BLUETOOTH_FIELD_VALUE = "AndroidInterface.setBluetoothFieldValue"

    const val ON_IMAGE_SHARED = "AndroidInterface.onImageShared"
    const val ON_IMAGE_SHARE_ERROR = "AndroidInterface.onImageShareError"
    const val ON_IMAGE_SAVED = "AndroidInterface.onImageSaved"
    const val ON_IMAGE_SAVE_ERROR = "AndroidInterface.onImageSaveError"

    const val ON_CONFIGURATION_UPDATE_REQUESTED = "AndroidInterface.onConfigurationUpdateRequested"
    const val STATUS_BAR_COLOR_CHANGE = "AndroidInterface.statusBarColorChange"
}