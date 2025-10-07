package com.dreamsoft.desoft20.features.printer

import android.content.Context
import android.graphics.Bitmap
import android.os.RemoteException
import android.widget.Toast
import com.dreamsoft.desoft20.features.printer.models.PrintType
import com.dreamsoft.desoft20.features.printer.models.PrinterLine
import com.dreamsoft.desoft20.features.printer.models.PrinterTextSize
import com.dreamsoft.desoft20.utils.extentions.ESCUtil
import com.dreamsoft.desoft20.utils.extentions.ImageHelper
import com.sunmi.peripheral.printer.InnerLcdCallback
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallback
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.WoyouConsts

/**
 * This class is used to demonstrate various printing effects
 * Developers need to repackage themselves, for details please refer to
 * http://sunmi-ota.oss-cn-hangzhou.aliyuncs.com/DOC/resource/re_cn/Sunmiprinter%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A31.1.191128.pdf
 *
 * @author kaltin
 * @since create at 2020-02-14
 */
@Suppress("unused")
class SunmiPrinterManager private constructor() {

    companion object {
        const val NO_SUNMI_PRINTER = 0x00000000
        const val CHECK_SUNMI_PRINTER = 0x00000001
        const val FOUND_SUNMI_PRINTER = 0x00000002
        const val LOST_SUNMI_PRINTER = 0x00000003

        @Volatile
        private var instance: SunmiPrinterManager? = null

        fun getInstance(): SunmiPrinterManager =
            instance ?: synchronized(this) {
                instance ?: SunmiPrinterManager().also { instance = it }
            }
    }

    /**
     * sunmiPrinter means checking the printer connection status
     */
    var sunmiPrinter = CHECK_SUNMI_PRINTER

    /**
     * SunmiPrinterService for API
     */
    private var sunmiPrinterService: SunmiPrinterService? = null

    private val innerPrinterCallback = object : InnerPrinterCallback() {
        override fun onConnected(service: SunmiPrinterService) {
            sunmiPrinterService = service
            checkSunmiPrinterService(service)
        }

        override fun onDisconnected() {
            sunmiPrinterService = null
            sunmiPrinter = LOST_SUNMI_PRINTER
        }
    }

    /**
     * Init sunmi print service
     */
    fun initSunmiPrinterService(context: Context) {
        try {
            val ret = InnerPrinterManager.getInstance().bindService(context, innerPrinterCallback)
            if (!ret) {
                sunmiPrinter = NO_SUNMI_PRINTER
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /**
     * DeInit sunmi print service
     */
    fun deInitSunmiPrinterService(context: Context) {
        try {
            sunmiPrinterService?.let {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback)
                sunmiPrinterService = null
                sunmiPrinter = LOST_SUNMI_PRINTER
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private fun checkSunmiPrinterService(service: SunmiPrinterService) {
        var ret = false
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
        sunmiPrinter = if (ret) FOUND_SUNMI_PRINTER else NO_SUNMI_PRINTER
    }

    /**
     * Some conditions can cause interface calls to fail
     * For example: the version is too low、device does not support
     * You can see [com.sunmi.peripheral.printer.ExceptionConst]
     * So you have to handle these exceptions
     */
    private fun handleRemoteException() {
        // TODO process when get one exception
    }

    /**
     * Send esc cmd
     */
    fun sendRawData(data: ByteArray) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.sendRAWData(data, null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Printer cuts paper and throws exception on machines without a cutter
     */
    fun cutpaper() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.cutPaper(null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Initialize the printer
     * All style settings will be restored to default
     */
    fun initPrinter() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.printerInit(null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    fun printLine(line: PrinterLine){
        val service = sunmiPrinterService ?: throw Exception("Printer not connected")
        when(line.type){
            PrintType.TEXT -> {
                setAlign(line.alignment.value)
                val fontSize = when(line.size){
                    PrinterTextSize.SMALLTEXT -> 24
                    PrinterTextSize.NORMALTEXT -> 36
                    PrinterTextSize.BIGTEXT -> 48
                    PrinterTextSize.EXTRABIGTEXT -> 60
                }
                printText(line.text, fontSize.toFloat(), line.bold, line.underline, "default")
            }
            PrintType.FEED -> {
                feedPaper()
            }
            PrintType.BARCODE -> {
                service.sendRAWData(ESCUtil.alignLeft(),null)
                val symbology =when(line.size){
                    PrinterTextSize.SMALLTEXT -> 2
                    PrinterTextSize.NORMALTEXT -> 3
                    PrinterTextSize.BIGTEXT -> 7
                    PrinterTextSize.EXTRABIGTEXT -> 8
                }
                printBarCode(line.text,symbology,162,2,2)
            }
            PrintType.IMAGE -> {
                val image = ImageHelper.base64ToBitmapOptimized(line.text)
                if (image != null) {
                    printBitmap(image,0)
                }else{
                    printText("Error al decodificar la imagen")
                }
            }
            PrintType.COLUMN -> {
                printText("Esta funcionalidad no esta implementada en la version actual")
            }
            PrintType.SEPARATOR -> {
                printText("Esta funcionalidad no esta implementada en la version actual")
            }
            PrintType.CUTTER -> {
                cutpaper()
            }
        }
    }

    /**
     * Paper feed three lines
     * Not disabled when line spacing is set to 0
     */
    fun print3Line() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.lineWrap(3, null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    fun printColumnsString(
        columntexts: Array<String>,
        widths: IntArray,
        aligns: IntArray,
        isBold: Boolean
    ) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            try {
                service.setPrinterStyle(
                    WoyouConsts.ENABLE_BOLD,
                    if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE
                )
            } catch (e: RemoteException) {
                if (isBold) {
                    service.sendRAWData(ESCUtil.boldOn(), null)
                } else {
                    service.sendRAWData(ESCUtil.boldOff(), null)
                }
            }
            service.printColumnsString(columntexts, widths, aligns, null)
            try {
                service.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE)
            } catch (e: RemoteException) {
                service.sendRAWData(ESCUtil.boldOff(), null)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Get printer serial number
     */
    fun getPrinterSerialNo(): String {
        val service = sunmiPrinterService ?: return "" // TODO Service disconnection processing
        return try {
            service.printerSerialNo
        } catch (e: RemoteException) {
            handleRemoteException()
            ""
        }
    }

    /**
     * Get device model
     */
    fun getDeviceModel(): String {
        val service = sunmiPrinterService ?: return "" // TODO Service disconnection processing
        return try {
            service.printerModal
        } catch (e: RemoteException) {
            handleRemoteException()
            ""
        }
    }

    /**
     * Get firmware version
     */
    fun getPrinterVersion(): String {
        val service = sunmiPrinterService ?: return "" // TODO Service disconnection processing
        return try {
            service.printerVersion
        } catch (e: RemoteException) {
            handleRemoteException()
            ""
        }
    }

    /**
     * Get paper specifications
     */
    fun getPrinterPaper(): String {
        val service = sunmiPrinterService ?: return "" // TODO Service disconnection processing
        return try {
            if (service.printerPaper == 1) "58mm" else "80mm"
        } catch (e: RemoteException) {
            handleRemoteException()
            ""
        }
    }

    /**
     * Get paper specifications
     */
    fun getPrinterHead(callback: InnerResultCallback) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.getPrinterFactory(callback)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Get printing distance since boot
     * Get printing distance through interface callback since 1.0.8(printerlibrary)
     */
    fun getPrinterDistance(callback: InnerResultCallback) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.getPrintedLength(callback)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Set printer alignment
     */
    fun setAlign(align: Int) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.setAlignment(align, null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Due to the distance between the paper hatch and the print head,
     * the paper needs to be fed out automatically
     * But if the Api does not support it, it will be replaced by printing three lines
     */
    fun feedPaper() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.autoOutPaper(null)
        } catch (e: RemoteException) {
            print3Line()
        }
    }

    fun feedPaper(lines: Int) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.lineWrap(lines, null)
        } catch (e: RemoteException) {
            print3Line()
        }
    }

    /**
     * Print text
     * setPrinterStyle:Api require V4.2.22 or later, So use esc cmd instead when not supported
     * More settings reference documentation [WoyouConsts]
     * printTextWithFont:
     * Custom fonts require V4.14.0 or later!
     * You can put the custom font in the 'assets' directory and Specify the font name parameters
     * in the Api.
     */
    fun printText(
        content: String,
        size: Float,
        isBold: Boolean,
        isUnderLine: Boolean,
        typeface: String
    ) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            try {
                service.setPrinterStyle(
                    WoyouConsts.ENABLE_BOLD,
                    if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE
                )
            } catch (e: RemoteException) {
                if (isBold) {
                    service.sendRAWData(ESCUtil.boldOn(), null)
                } else {
                    service.sendRAWData(ESCUtil.boldOff(), null)
                }
            }
            try {
                service.setPrinterStyle(
                    WoyouConsts.ENABLE_UNDERLINE,
                    if (isUnderLine) WoyouConsts.ENABLE else WoyouConsts.DISABLE
                )
            } catch (e: RemoteException) {
                if (isUnderLine) {
                    service.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
                } else {
                    service.sendRAWData(ESCUtil.underlineOff(), null)
                }
            }
            service.printTextWithFont(content, typeface, size, null)
            service.sendRAWData(ESCUtil.alignLeft(), null)
            service.sendRAWData(ESCUtil.boldOff(), null)
            service.sendRAWData(ESCUtil.underlineOff(), null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printText(content: String) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.printText(content, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print Bar Code
     */
    fun printBarCode(
        data: String,
        symbology: Int,
        height: Int,
        width: Int,
        textposition: Int
    ) {
        val service = sunmiPrinterService ?: return
        try {
            service.printBarCode(data, symbology, height, width, textposition, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print Qr Code
     */
    fun printQr(data: String, modulesize: Int, errorlevel: Int) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.printQRCode(data, modulesize, errorlevel, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print a row of a table
     */
    fun printTable(txts: Array<String>, width: IntArray, align: IntArray) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.printColumnsString(txts, width, align, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print pictures and text in the specified order
     * After the picture is printed,
     * the line feed output needs to be called,
     * otherwise it will be saved in the cache
     * In this example, the image will be printed because the print text content is added
     */
    fun printBitmap(bitmap: Bitmap, orientation: Int) {
        val service = sunmiPrinterService ?: return
        try {
            service.printBitmap(bitmap, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Gets whether the current printer is in black mark mode
     */
    fun isBlackLabelMode(): Boolean {
        val service = sunmiPrinterService ?: return false // TODO Service disconnection processing
        return try {
            service.printerMode == 1
        } catch (e: RemoteException) {
            false
        }
    }

    /**
     * Gets whether the current printer is in label-printing mode
     */
    fun isLabelMode(): Boolean {
        val service = sunmiPrinterService ?: return false // TODO Service disconnection processing
        return try {
            service.printerMode == 2
        } catch (e: RemoteException) {
            false
        }
    }

    /**
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care result)
     */
    fun printTrans(context: Context, callback: InnerResultCallback) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.enterPrinterBuffer(true)
            printExample(context)
            service.exitPrinterBufferWithCallback(true, callback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Open cash box
     * This method can be used on Sunmi devices with a cash drawer interface
     * If there is no cash box (such as V1、P1) or the call fails, an exception will be thrown
     *
     * Reference to https://docs.sunmi.com/general-function-modules/external-device-debug/cash-box-driver/
     */
    fun openCashBox() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.openDrawer(null)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * LCD screen control
     * @param flag 1 —— Initialization
     *             2 —— Light up screen
     *             3 —— Extinguish screen
     *             4 —— Clear screen contents
     */
    fun controlLcd(flag: Int) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.sendLCDCommand(flag)
        } catch (e: RemoteException) {
            handleRemoteException()
        }
    }

    /**
     * Display text SUNMI,font size is 16 and format is fill
     * sendLCDFillString(txt, size, fill, callback)
     * Since the screen pixel height is 40, the font should not exceed 40
     */
    fun sendTextToLcd() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.sendLCDFillString("SUNMI", 16, true, object : InnerLcdCallback() {
                override fun onRunResult(show: Boolean) {
                    // TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Display two lines and one empty line in the middle
     */
    fun sendTextsToLcd() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            val texts = arrayOf("SUNMI", null, "SUNMI")
            val align = intArrayOf(2, 1, 2)
            service.sendLCDMultiString(texts, align, object : InnerLcdCallback() {
                override fun onRunResult(show: Boolean) {
                    // TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Display one 128x40 pixels and opaque picture
     */
    fun sendPicToLcd(pic: Bitmap) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.sendLCDBitmap(pic, object : InnerLcdCallback() {
                override fun onRunResult(show: Boolean) {
                    // TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Sample print receipt
     */
    fun printExample(context: Context) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            val paper = service.printerPaper

            service.printerInit(null)
            service.setAlignment(1, null)
            service.printText("测试样张\n", null)
//            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.applogo)
//            service.printBitmap(bitmap, null)
            service.lineWrap(1, null)
            service.setAlignment(0, null)
            try {
                service.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0)
            } catch (e: RemoteException) {
                service.sendRAWData(byteArrayOf(0x1B, 0x33, 0x00), null)
            }
            service.printTextWithFont(
                "说明：这是一个自定义的小票样式例子,开发者可以仿照此进行自己的构建\n",
                null, 12f, null
            )
            if (paper == 1) {
                service.printText("--------------------------------\n", null)
            } else {
                service.printText("------------------------------------------------\n", null)
            }
            try {
                service.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE)
            } catch (e: RemoteException) {
                service.sendRAWData(ESCUtil.boldOn(), null)
            }
            val txts = arrayOf("商品", "价格")
            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            service.printColumnsString(txts, width, align, null)
            try {
                service.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE)
            } catch (e: RemoteException) {
                service.sendRAWData(ESCUtil.boldOff(), null)
            }
            if (paper == 1) {
                service.printText("--------------------------------\n", null)
            } else {
                service.printText("------------------------------------------------\n", null)
            }
            txts[0] = "汉堡"
            txts[1] = "17¥"
            service.printColumnsString(txts, width, align, null)
            txts[0] = "可乐"
            txts[1] = "10¥"
            service.printColumnsString(txts, width, align, null)
            txts[0] = "薯条"
            txts[1] = "11¥"
            service.printColumnsString(txts, width, align, null)
            txts[0] = "炸鸡"
            txts[1] = "11¥"
            service.printColumnsString(txts, width, align, null)
            txts[0] = "圣代"
            txts[1] = "10¥"
            service.printColumnsString(txts, width, align, null)
            if (paper == 1) {
                service.printText("--------------------------------\n", null)
            } else {
                service.printText("------------------------------------------------\n", null)
            }
            service.printTextWithFont("总计:          59¥\b", null, 40f, null)
            service.setAlignment(1, null)
            service.printQRCode("谢谢惠顾", 10, 0, null)
            service.setFontSize(36f, null)
            service.printText("谢谢惠顾", null)
            service.autoOutPaper(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Used to report the real-time query status of the printer, which can be used before each printing
     */
    fun showPrinterStatus(context: Context) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        var result = "Interface is too low to implement interface"
        try {
            val res = service.updatePrinterState()
            result = when (res) {
                1 -> "printer is running"
                2 -> "printer found but still initializing"
                3 -> "printer hardware interface is abnormal and needs to be reprinted"
                4 -> "printer is out of paper"
                5 -> "printer is overheating"
                6 -> "printer's cover is not closed"
                7 -> "printer's cutter is abnormal"
                8 -> "printer's cutter is normal"
                9 -> "not found black mark paper"
                505 -> "printer does not exist"
                else -> result
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
    }

    /**
     * Demo printing a label
     * After printing one label, in order to facilitate the user to tear the paper, call
     * labelOutput to push the label paper out of the paper hatch
     * 演示打印一张标签
     * 打印单张标签后为了方便用户撕纸可调用labelOutput,将标签纸推出纸舱口
     */
    fun printOneLabel() {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            service.labelLocate()
            printLabelContent()
            service.labelOutput()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Demo printing multi label
     *
     * After printing multiple labels, choose whether to push the label paper to the paper hatch according to the needs
     * 演示打印多张标签
     * 打印多张标签后根据需求选择是否推出标签纸到纸舱口
     */
    fun printMultiLabel(num: Int) {
        val service = sunmiPrinterService ?: return // TODO Service disconnection processing
        try {
            repeat(num) {
                service.labelLocate()
                printLabelContent()
            }
            service.labelOutput()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Custom label ticket content
     * In the example, not all labels can be applied. In actual use, please pay attention to adapting the size of the label. You can adjust the font size and content position.
     * 自定义的标签小票内容
     * 例子中并不能适用所有标签纸，实际使用时注意要自适配标签纸大小，可通过调节字体大小，内容位置等方式
     */
    private fun printLabelContent() {
        val service = sunmiPrinterService ?: return
        try {
            service.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE)
            service.lineWrap(1, null)
            service.setAlignment(0, null)
            service.printText("商品         豆浆\n", null)
            service.printText("到期时间         12-13  14时\n", null)
            service.printBarCode("{C1234567890123456", 8, 90, 2, 2, null)
            service.lineWrap(1, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}