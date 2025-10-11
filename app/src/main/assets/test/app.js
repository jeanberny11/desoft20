// ============================================
// CONSOLE LOGGING
// ============================================

function log(message, type = 'info') {
    const consoletxt = document.getElementById('console');
    const line = document.createElement('div');
    line.className = `console-line console-${type}`;
    const timestamp = new Date().toLocaleTimeString();
    line.textContent = `[${timestamp}] ${message}`;
    consoletxt.appendChild(line);
    consoletxt.scrollTop = consoletxt.scrollHeight;
    console.log(message);
}

function clearConsole() {
    const console = document.getElementById('console');
    console.innerHTML = '<div class="console-line console-info">Console cleared.</div>';
}

// ============================================
// CATEGORY MANAGEMENT
// ============================================

function toggleCategory(id) {
    const category = document.getElementById(id);
    category.classList.toggle('active');
}

function expandAll() {
    document.querySelectorAll('.category').forEach(cat => cat.classList.add('active'));
}

function collapseAll() {
    document.querySelectorAll('.category').forEach(cat => cat.classList.remove('active'));
}

// ============================================
// ANDROID INTERFACE DETECTION
// ============================================

function checkInterface() {
    if(typeof AndroidInterfaces !== 'undefined'){
        document.getElementById('statusIndicator').className = 'status-indicator status-success';
        return true;
    }else{
        log('⚠️ AndroidInterfaces not found! Are you running in the Android app?', 'warning');
        document.getElementById('statusIndicator').className = 'status-indicator status-error';
        return false;
    }
}

// ============================================
// ANDROID CALLBACKS SETUP
// ============================================

if (typeof AndroidInterfaces !== 'undefined') {
    AndroidInterfaces.onPrintResult = function(result) {
        log('📄 Print Result: ' + result, 'success');
    };

    AndroidInterfaces.onLocationResult = function(result) {
        log('📍 Location Result: ' + result, 'success');
        try {
            const data = JSON.parse(result);
            if (data.resultCode === 1) {
                log(`Coordinates: ${data.latitude}, ${data.longitude} (±${data.accuracy}m)`, 'info');
            }
        } catch(e) {
            log('Error decoding location result: ' + e.message, 'error');
        }
    };

    AndroidInterfaces.onBarcodeResult = function(result) {
        log('🔍 Barcode Result: ' + result, 'success');
    };

    AndroidInterfaces.onDownloadProgress = function(result) {
        log('⬇️ Download Progress: ' + JSON.parse(result).message, 'info');
    };

    AndroidInterfaces.onDownloadComplete = function(result) {
        log('✅ Download Complete: ' + result, 'success');
    };

    AndroidInterfaces.onDownloadError = function(result) {
        log('❌ Download Error: ' + JSON.parse(result).message, 'error');
    };

    AndroidInterfaces.onShareResult = function(result) {
        //log('✅ Share Result: ' + JSON.parse(result).message, 'error');
        log('✅ Share Result: ' + result, 'success');
    };

    log('✅ AndroidInterfaces connected and ready!', 'success');
} else {
    log('⚠️ Running in browser - AndroidInterfaces not available', 'warning');
}

// ============================================
// TEST FUNCTIONS - PRINTING
// ============================================

function testPrintGeneric() {
    if (!checkInterface()) return;
    try {
        const data = document.getElementById('genericPrintData').value;
        const parsed = JSON.parse(data);
        const printerName = document.getElementById('genericPrinterName').value;
        if (printerName) parsed.printerName = printerName;
        
        log('🖨️ Calling printGenericData...', 'info');
        const result = AndroidInterfaces.printGenericData(JSON.stringify(parsed));
        log('Response: ' + result, 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

function testPrintSunmi() {
    if (!checkInterface()) return;
    try {
        const data = document.getElementById('sunmiPrintData').value;
        const parsed = JSON.parse(data);
        const printerName = document.getElementById('sunmiPrinterName').value;
        if (printerName) parsed.printerName = printerName;
        
        log('🖨️ Calling printSunmiData...', 'info');
        const result = AndroidInterfaces.printSunmiData(JSON.stringify(parsed));
        log('Response: ' + result, 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

// ============================================
// TEST FUNCTIONS - DEVICE FEATURES
// ============================================

function testGetDeviceId() {
    if (!checkInterface()) return;
    log('📱 Calling getDeviceId...', 'info');
    const deviceId = AndroidInterfaces.getDeviceId();
    log('Device ID: ' + deviceId, 'success');
}

function testGetLocation() {
    if (!checkInterface()) return;
    log('📍 Calling getLastLocation...', 'info');
    const result = AndroidInterfaces.getLastLocation();
    log('Response: ' + result, 'info');
}

function testReadBarcode() {
    if (!checkInterface()) return;
    log('🔍 Calling readBarcode...', 'info');
    AndroidInterfaces.readBarcode();
    log('Barcode scanner launched', 'success');
}

// ============================================
// TEST FUNCTIONS - FILE OPERATIONS
// ============================================

function testDownloadFile() {
    if (!checkInterface()) return;
    try {
        const downloadRequest = {
            url: document.getElementById('downloadUrl').value,
            filename: document.getElementById('downloadFilename').value || undefined,
            mimeType: document.getElementById('downloadMime').value || undefined
        };
        
        log('⬇️ Calling downloadFile...', 'info');
        const result = AndroidInterfaces.downloadFile(JSON.stringify(downloadRequest));
        log('Response: ' + result, 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

function testOpenFile() {
    if (!checkInterface()) return;
    const filename = document.getElementById('openFilename').value;
    log('📂 Calling openDownloadedFile: ' + filename, 'info');
    const result = AndroidInterfaces.openDownloadedFile(filename);
    log('Response: ' + result, 'success');
}

function testGetImages() {
    if (!checkInterface()) return;
    log('🖼️ Calling getDesoftinfImages...', 'info');
    const result = AndroidInterfaces.getDesoftinfImages();
    log('Response: ' + result, 'success');
    try {
        const images = JSON.parse(result);
        log(`Found ${images.length} images`, 'info');
        images.forEach(img => log('  - ' + img, 'info'));
    } catch(e) {}
}

// ============================================
// TEST FUNCTIONS - SHARING
// ============================================

function testSendWhatsApp() {
    if (!checkInterface()) return;
    const imageName = document.getElementById('whatsappImageName').value;
    const message = document.getElementById('whatsappMessage').value;
    log('💬 Calling sendWhatsAppImage...', 'info');
    const result = AndroidInterfaces.sendWhatsAppImage(imageName, message);
    log('Response: ' + result, 'success');
}

function testShareImage() {
    if (!checkInterface()) return;
    try {
        const shareRequest = {
            imageName: document.getElementById('shareImageName').value,
            message: document.getElementById('shareMessage').value,
            packageName: document.getElementById('sharePackage').value
        };
        
        log('📤 Calling shareImage...', 'info');
        const result = AndroidInterfaces.shareImage(JSON.stringify(shareRequest));
        log('Response: ' + result, 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

function testShareMultiple() {
    if (!checkInterface()) return;
    try {
        const imagesStr = document.getElementById('multipleImages').value;
        const images = imagesStr.split(',').map(s => s.trim());
        const message = document.getElementById('multipleMessage').value;
        const packageName = document.getElementById('multiplePackage').value;
        
        log('📤 Calling shareMultipleImages...', 'info');
        const result = AndroidInterfaces.shareMultipleImages(JSON.stringify(images), message, packageName);
        log('Response: ' + result, 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

// ============================================
// TEST FUNCTIONS - UTILITIES
// ============================================

function testClearHistory() {
    if (!checkInterface()) return;
    log('🧹 Calling clearWebViewHistory...', 'info');
    AndroidInterfaces.clearWebViewHistory();
    log('WebView history cleared', 'success');
}

function testHideKeyboard() {
    if (!checkInterface()) return;
    log('⌨️ Calling hideKeyboard...', 'info');
    AndroidInterfaces.hideKeyboard();
    log('Keyboard hidden', 'success');
}

function testUpdateConfig() {
    if (!checkInterface()) return;
    try {
        const remoteUrl = document.getElementById('configRemoteUrl').value;
        const localUrl = document.getElementById('configLocalUrl').value;
        const useLocal = document.getElementById('configUseLocal').value === 'true';
        const printerName = document.getElementById('configPrinterName').value;
        const webNav = document.getElementById('configWebNav').value === 'true';
        
        log('⚙️ Calling updateAppConfiguration...', 'info');
        AndroidInterfaces.updateAppConfiguration(remoteUrl, localUrl, useLocal, printerName, webNav);
        log('Configuration updated', 'success');
    } catch (e) {
        log('Error: ' + e.message, 'error');
    }
}

function testOpenBrowser() {
    if (!checkInterface()) return;
    const url = document.getElementById('browserUrl').value;
    log('🌐 Calling openInBrowser: ' + url, 'info');
    AndroidInterfaces.openInBrowser(url);
    log('Browser opened', 'success');
}

// ============================================
// HELPER FUNCTIONS
// ============================================

function fillSampleReceipt(type) {
    const sampleReceipt = {
        printerName: "",
        lines: [
            {type: "TEXT", text: "MY STORE", alignment: 1, size: 2, bold: true},
            {type: "TEXT", text: "123 Main Street", alignment: 1, size: 0},
            {type: "TEXT", text: "Tel: 555-1234", alignment: 1, size: 0},
            {type: "FEED"},
            {type: "TEXT", text: "--------------------------------", alignment: 0},
            {type: "TEXT", text: "RECEIPT", alignment: 1, size: 1, bold: true},
            {type: "TEXT", text: "--------------------------------", alignment: 0},
            {type: "FEED"},
            {type: "TEXT", text: "Item 1              $10.00", alignment: 0},
            {type: "TEXT", text: "Item 2              $15.00", alignment: 0},
            {type: "TEXT", text: "Item 3               $7.50", alignment: 0},
            {type: "TEXT", text: "--------------------------------", alignment: 0},
            {type: "TEXT", text: "SUBTOTAL:           $32.50", alignment: 0, bold: true},
            {type: "TEXT", text: "TAX (10%):           $3.25", alignment: 0},
            {type: "TEXT", text: "--------------------------------", alignment: 0},
            {type: "TEXT", text: "TOTAL:              $35.75", alignment: 0, size: 1, bold: true},
            {type: "FEED"},
            {type: "BARCODE", text: "123456789012", alignment: 1, size: 1},
            {type: "FEED"},
            {type: "TEXT", text: "Thank you for your business!", alignment: 1, size: 0},
            {type: "TEXT", text: new Date().toLocaleString(), alignment: 1, size: 0},
            {type: "FEED"},
            {type: "FEED"},
            {type: "CUTTER"}
        ]
    };
    
    const targetId = type === 'sunmi' ? 'sunmiPrintData' : 'genericPrintData';
    document.getElementById(targetId).value = JSON.stringify(sampleReceipt, null, 2);
    log('✅ Sample receipt loaded', 'success');
}

function testAll() {
    log('🚀 Running all tests...', 'info');
    log('═══════════════════════════════════', 'info');
    
    setTimeout(() => {
        log('\n--- Device Features ---', 'info');
        testGetDeviceId();
    }, 500);
    
    setTimeout(() => {
        log('\n--- File Operations ---', 'info');
        testGetImages();
    }, 1000);
    
    setTimeout(() => {
        log('\n--- Utilities ---', 'info');
        log('⚙️ Skipping updateAppConfiguration (would change settings)', 'warning');
        log('🌐 Skipping openInBrowser (would leave app)', 'warning');
        log('🧹 Skipping clearWebViewHistory (would clear history)', 'warning');
    }, 1500);
    
    setTimeout(() => {
        log('\n--- Interactive Tests (require user action) ---', 'warning');
        log('🖨️ Printing - requires printer connection', 'warning');
        log('📍 Location - requires permission', 'warning');
        log('🔍 Barcode - opens scanner', 'warning');
        log('⬇️ Download - starts file download', 'warning');
        log('📤 Sharing - opens share dialog', 'warning');
        log('\n═══════════════════════════════════', 'info');
        log('✅ Automated tests complete!', 'success');
    }, 2000);
}

// ============================================
// INITIALIZATION
// ============================================

window.addEventListener('load', function() {
    log('🎉 Test Suite Loaded', 'success');
    log('📱 Tap category headers to expand/collapse', 'info');
    
    // Auto-expand first category
    setTimeout(() => {
        document.getElementById('cat-printing').classList.add('active');
    }, 100);
});