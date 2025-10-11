package com.dreamsoft.desoft20.webview

object WebViewCallbacks {

    // Legacy compatibility callbacks
    const val SET_BARCODE_FIELD = "codeb" // codeb(fieldId, result)
    const val SET_BLUETOOTH_FIELD = "setBarcodeFieldValue" // Custom field setter

    // Modern callbacks - implement these in your web app
    const val ON_PRINT_RESULT = "AndroidInterfaces.onPrintResult"
    //const val ON_PRINT_ERROR = "AndroidInterfaces.onPrintError"

    const val ON_LOCATION_RESULT = "AndroidInterfaces.onLocationResult"

    const val ON_DOWNLOAD_PROGRESS = "AndroidInterfaces.onDownloadProgress"
    const val ON_DOWNLOAD_COMPLETE = "AndroidInterfaces.onDownloadComplete"
    const val ON_DOWNLOAD_ERROR = "AndroidInterfaces.onDownloadError"

    const val ON_BARCODE_RESULT = "AndroidInterfaces.onBarcodeResult"

    const val ON_SHARE_RESULT = "AndroidInterfaces.onShareResult"


    const val ON_BARCODE_ERROR = "AndroidInterfaces.onBarcodeError"
    const val SET_BARCODE_FIELD_VALUE = "AndroidInterfaces.setBarcodeFieldValue"

    const val ON_BLUETOOTH_DEVICE_SELECTED = "AndroidInterfaces.onBluetoothDeviceSelected"
    const val ON_BLUETOOTH_ERROR = "AndroidInterfaces.onBluetoothError"
    const val SET_BLUETOOTH_FIELD_VALUE = "AndroidInterfaces.setBluetoothFieldValue"

    const val ON_IMAGE_SHARED = "AndroidInterfaces.onImageShared"
    const val ON_IMAGE_SHARE_ERROR = "AndroidInterfaces.onImageShareError"
    const val ON_IMAGE_SAVED = "AndroidInterfaces.onImageSaved"
    const val ON_IMAGE_SAVE_ERROR = "AndroidInterfaces.onImageSaveError"

    const val ON_CONFIGURATION_UPDATE_REQUESTED = "AndroidInterfaces.onConfigurationUpdateRequested"
    const val STATUS_BAR_COLOR_CHANGE = "AndroidInterfaces.statusBarColorChange"
}