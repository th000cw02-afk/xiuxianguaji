package com.idle.wenzixiuxian

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import kotlin.math.max

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val REQUEST_PERMISSION_CODE = 1001
    private var pickFileSuccessCallback: String? = null
    private var pickFileErrorCallback: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 显示状态栏，设置黑色背景和白色图标
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        
        // 设置状态栏黑色背景
        window.statusBarColor = android.graphics.Color.BLACK
        
        // Android 6.0+ 设置状态栏图标为浅色（白色）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (
                window.decorView.systemUiVisibility and
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            )
        }
        
        // 设置导航栏透明
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        
        // 支持挖孔屏（刘海屏）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            layoutParams.layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = layoutParams
        }
        
        // 设置软键盘模式，让界面整体上移
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        
        // 设置屏幕常亮 - 游戏运行时始终保持屏幕常亮，防止手机锁屏
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        webView = WebView(this)
        webView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
        
        // 创建FrameLayout作为根布局
        val rootLayout = FrameLayout(this)
        
        // 设置WebView布局参数，让它延伸到状态栏下方
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        webView.layoutParams = layoutParams
        
        rootLayout.addView(webView)
        setContentView(rootLayout)
        
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 处理blob URL
                if (url?.startsWith("blob:") == true) {
                    // 通过JavaScript获取Blob内容并处理
                    val script = """
                        (function() {
                            try {
                                var xhr = new XMLHttpRequest();
                                xhr.open('GET', '$url', true);
                                xhr.responseType = 'blob';
                                
                                xhr.onload = function() {
                                    if (xhr.status === 200) {
                                        var blob = xhr.response;
                                        var reader = new FileReader();
                                        reader.onload = function() {
                                            var base64Data = reader.result.split(',')[1];
                                            var mimeType = blob.type || 'application/octet-stream';
                                            var fileName = 'download_file';
                                            
                                            // 尝试从blob URL中提取文件名
                                            var urlParts = '$url'.split('/');
                                            if (urlParts.length > 0) {
                                                fileName = urlParts[urlParts.length - 1];
                                            }
                                            
                                            // 调用Android接口处理下载
                                            AndroidInterface.downloadFile(base64Data, fileName, mimeType);
                                        };
                                        reader.readAsDataURL(blob);
                                    } else {
                                        AndroidInterface.showToast("下载失败: 网络错误");
                                    }
                                };
                                
                                xhr.onerror = function() {
                                    AndroidInterface.showToast("下载失败: 网络请求错误");
                                };
                                
                                xhr.send();
                            } catch (e) {
                                AndroidInterface.showToast("处理下载失败: " + e.message);
                            }
                        })();
                    """.trimIndent()
                    view?.evaluateJavascript(script, null)
                    return true
                }
                
                return false
            }
            
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                this@MainActivity.filePathCallback?.onReceiveValue(null)
                this@MainActivity.filePathCallback = filePathCallback

                openFileChooser()
                return true
            }
        }
        
        // 设置下载监听器，支持HTML5 download属性
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            handleDownload(url, contentDisposition, mimetype)
        }
        
        // 注入文件下载和上传的JavaScript接口
        webView.addJavascriptInterface(WebAppInterface(this, this), "AndroidInterface")
        
        // 处理系统窗口insets和软键盘以避免内容被遮挡
        ViewCompat.setOnApplyWindowInsetsListener(webView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            
            view.updateLayoutParams<FrameLayout.LayoutParams> {
                leftMargin = systemBars.left
                topMargin = systemBars.top
                rightMargin = systemBars.right
                bottomMargin = maxOf(systemBars.bottom, ime.bottom)
            }
            insets
        }
        
        webView.loadUrl("file:///android_asset/index.html")
    }
    
    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        webView.onResume()
    }
    
    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        }
        
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        }
        
        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            REQUEST_PERMISSION_CODE
        )
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        
        val chooserIntent = Intent.createChooser(intent, "选择文件")
        fileChooserLauncher.launch(chooserIntent)
    }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val results: Array<Uri>?
            
            if (data == null) {
                results = null
            } else {
                val dataString = data.dataString
                val clipData = data.clipData
                
                if (clipData != null) {
                    results = Array(clipData.itemCount) {
                        clipData.getItemAt(it).uri
                    }
                } else if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                } else {
                    results = null
                }
            }
            
            filePathCallback?.onReceiveValue(results)
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val content = inputStream?.bufferedReader()?.use { it.readText() }
                    inputStream?.close()
                    
                    if (content != null) {
                        val escapedContent = content
                            .replace("\\", "\\\\")
                            .replace("'", "\\'")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                        val script = "$pickFileSuccessCallback('$escapedContent')"
                        webView.evaluateJavascript(script, null)
                    } else {
                        val script = "$pickFileErrorCallback('无法读取文件内容')"
                        webView.evaluateJavascript(script, null)
                    }
                } catch (e: Exception) {
                    val script = "$pickFileErrorCallback('${e.message}')"
                    webView.evaluateJavascript(script, null)
                }
            } else {
                val script = "$pickFileErrorCallback('未选择文件')"
                webView.evaluateJavascript(script, null)
            }
        } else {
            val script = "$pickFileErrorCallback('取消选择')"
            webView.evaluateJavascript(script, null)
        }
        pickFileSuccessCallback = null
        pickFileErrorCallback = null
    }

    fun pickFile(mimeType: String, onSuccess: String, onError: String) {
        pickFileSuccessCallback = onSuccess
        pickFileErrorCallback = onError

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = if (mimeType == "text") "*/*" else mimeType
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            pickFileLauncher.launch(intent)
        } catch (e: Exception) {
            val script = "$onError('${e.message}')"
            webView.evaluateJavascript(script, null)
            pickFileSuccessCallback = null
            pickFileErrorCallback = null
        }
    }

    private var pickImageSuccessCallback: String? = null
    private var pickImageErrorCallback: String? = null

    fun pickImage(onSuccess: String, onError: String) {
        pickImageSuccessCallback = onSuccess
        pickImageErrorCallback = onError

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            pickImageLauncher.launch(intent)
        } catch (e: Exception) {
            val script = "$onError('${e.message}')"
            webView.evaluateJavascript(script, null)
            pickImageSuccessCallback = null
            pickImageErrorCallback = null
        }
    }

    private var pickMultipleImagesSuccessCallback: String? = null
    private var pickMultipleImagesErrorCallback: String? = null

    fun pickMultipleImages(onSuccess: String, onError: String) {
        pickMultipleImagesSuccessCallback = onSuccess
        pickMultipleImagesErrorCallback = onError

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        try {
            pickMultipleImagesLauncher.launch(intent)
        } catch (e: Exception) {
            val script = "$onError('${e.message}')"
            webView.evaluateJavascript(script, null)
            pickMultipleImagesSuccessCallback = null
            pickMultipleImagesErrorCallback = null
        }
    }

    private val pickMultipleImagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val clipData = result.data?.clipData
            if (clipData != null && clipData.itemCount > 0) {
                val base64List = mutableListOf<String>()
                var processedCount = 0
                val totalCount = clipData.itemCount

                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        val bytes = inputStream?.use { it.readBytes() }
                        inputStream?.close()

                        if (bytes != null) {
                            val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                            base64List.add(base64)
                        }
                    } catch (e: Exception) {
                        // Skip failed images
                    }
                    processedCount++
                }

                if (base64List.isNotEmpty()) {
                    val script = "$pickMultipleImagesSuccessCallback(${org.json.JSONArray(base64List).toString()})"
                    webView.evaluateJavascript(script, null)
                } else {
                    val script = "$pickMultipleImagesErrorCallback('无法读取任何图片')"
                    webView.evaluateJavascript(script, null)
                }
            } else {
                val script = "$pickMultipleImagesErrorCallback('未选择图片')"
                webView.evaluateJavascript(script, null)
            }
        } else {
            pickMultipleImagesErrorCallback?.let {
                val script = "$it('取消选择')"
                webView.evaluateJavascript(script, null)
            }
        }
        pickMultipleImagesSuccessCallback = null
        pickMultipleImagesErrorCallback = null
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bytes = inputStream?.use { it.readBytes() }
                    inputStream?.close()

                    if (bytes != null) {
                        val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                        val script = "$pickImageSuccessCallback('$base64')"
                        webView.evaluateJavascript(script, null)
                    } else {
                        val script = "$pickImageErrorCallback('无法读取图片内容')"
                        webView.evaluateJavascript(script, null)
                    }
                } catch (e: Exception) {
                    val script = "$pickImageErrorCallback('${e.message}')"
                    webView.evaluateJavascript(script, null)
                }
            } else {
                val script = "$pickImageErrorCallback('未选择图片')"
                webView.evaluateJavascript(script, null)
            }
        } else {
            pickImageErrorCallback?.let {
                val script = "$it('取消选择')"
                webView.evaluateJavascript(script, null)
            }
        }
        pickImageSuccessCallback = null
        pickImageErrorCallback = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openFileChooser()
            } else {
                Toast.makeText(this, "需要文件访问权限", Toast.LENGTH_SHORT).show()
                filePathCallback?.onReceiveValue(null)
                filePathCallback = null
            }
        }
    }

    fun downloadFile(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("正在下载...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        
        Toast.makeText(this, "开始下载: $fileName", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleDownload(url: String, contentDisposition: String?, mimeType: String?) {
        try {
            // 解析文件名
            var fileName = "download_file"
            if (contentDisposition != null) {
                val regex = Regex("filename[*]?=(?:UTF-8'')?([^;]+)", RegexOption.IGNORE_CASE)
                val match = regex.find(contentDisposition)
                if (match != null) {
                    fileName = match.groupValues[1].trim().removeSurrounding("\"")
                } else {
                    // 尝试另一种格式
                    val simpleRegex = Regex("filename=([^;]+)", RegexOption.IGNORE_CASE)
                    val simpleMatch = simpleRegex.find(contentDisposition)
                    if (simpleMatch != null) {
                        fileName = simpleMatch.groupValues[1].trim().removeSurrounding("\"")
                    }
                }
            }
            
            // 如果是blob URL，使用WebAppInterface处理
            if (url.startsWith("blob:")) {
                // 通过JavaScript获取Blob内容并处理
                val script = """
                    (function() {
                        try {
                            var xhr = new XMLHttpRequest();
                            xhr.open('GET', '$url', true);
                            xhr.responseType = 'blob';
                            
                            xhr.onload = function() {
                                if (xhr.status === 200) {
                                    var blob = xhr.response;
                                    var reader = new FileReader();
                                    reader.onload = function() {
                                        var base64Data = reader.result.split(',')[1];
                                        var mimeType = blob.type || 'application/octet-stream';
                                        var fileName = 'download_file';
                                        
                                        // 尝试从blob URL中提取文件名
                                        var urlParts = '$url'.split('/');
                                        if (urlParts.length > 0) {
                                            fileName = urlParts[urlParts.length - 1];
                                        }
                                        
                                        // 调用Android接口处理下载
                                        AndroidInterface.downloadFile(base64Data, fileName, mimeType);
                                    };
                                    reader.readAsDataURL(blob);
                                } else {
                                    AndroidInterface.showToast("下载失败: 网络错误");
                                }
                            };
                            
                            xhr.onerror = function() {
                                AndroidInterface.showToast("下载失败: 网络请求错误");
                            };
                            
                            xhr.send();
                        } catch (e) {
                            AndroidInterface.showToast("处理下载失败: " + e.message);
                        }
                    })();
                """.trimIndent()
                webView.evaluateJavascript(script, null)
            }
            // 如果是data URL，使用WebAppInterface处理
            else if (url.startsWith("data:")) {
                val base64Data = url.substring(url.indexOf(",") + 1)
                val extension = when {
                    mimeType?.contains("image/png") == true -> ".png"
                    mimeType?.contains("image/jpeg") == true -> ".jpg"
                    mimeType?.contains("image/svg") == true -> ".svg"
                    mimeType?.contains("text/plain") == true -> ".txt"
                    mimeType?.contains("application/pdf") == true -> ".pdf"
                    else -> ".bin"
                }
                
                if (!fileName.contains(".")) {
                    fileName += extension
                }
                
                // 使用现有的WebAppInterface处理base64数据
                WebAppInterface(this, this).downloadFile(base64Data, fileName, mimeType ?: "application/octet-stream")
            } else {
                // 普通URL下载
                val finalFileName = if (fileName.contains(".")) fileName else {
                    val extension = when {
                        mimeType?.contains("image/png") == true -> ".png"
                        mimeType?.contains("image/jpeg") == true -> ".jpg"
                        mimeType?.contains("image/svg") == true -> ".svg"
                        mimeType?.contains("text/plain") == true -> ".txt"
                        mimeType?.contains("application/pdf") == true -> ".pdf"
                        else -> ".bin"
                    }
                    fileName + extension
                }
                
                downloadFile(url, finalFileName)
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "下载失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareCurrentPage() {
        // 获取当前页面URL
        val currentUrl = webView.url
        
        // 创建分享Intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "修仙挂机")
            putExtra(Intent.EXTRA_TEXT, "我正在玩修仙挂机：$currentUrl")
        }
        
        // 启动分享选择器
        val chooserIntent = Intent.createChooser(shareIntent, "分享应用")
        startActivity(chooserIntent)
    }

    // 新增方法：分享PDF文件
    fun sharePDFFile(filePath: String, fileName: String) {
        try {
            val file = java.io.File(filePath)
            if (file.exists()) {
                val fileUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    "$packageName.fileprovider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_SUBJECT, "PDF文件分享")
                    putExtra(Intent.EXTRA_TEXT, "通过修仙挂机导出的 PDF 文件")
                }
                
                val chooserIntent = Intent.createChooser(shareIntent, "分享PDF")
                startActivity(chooserIntent)
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "分享失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 双次返回退出相关变量
    private var backPressedTime: Long = 0
    private var backPressCount = 0
    private val BACK_PRESS_INTERVAL = 2000 // 2秒内双击返回键退出
    private var toast: android.widget.Toast? = null

    // 支持双次侧滑返回
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackPress()
        super.onBackPressed()
    }

    private fun handleBackPress() {
        val currentTime = System.currentTimeMillis()
        
        if (webView.canGoBack()) {
            // 如果有WebView历史记录，先处理WebView返回
            webView.goBack()
            backPressCount = 0 // 重置计数器
            return
        }
        
        // 检查是否在短时间内连续按了两次返回键
        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            backPressCount++
        } else {
            backPressCount = 1 // 第一次按下
        }
        
        backPressedTime = currentTime
        
        if (backPressCount >= 2) {
            // 双次返回，退出应用
            toast?.cancel()
            super.onBackPressed()
        } else {
            // 单次返回，显示提示
            toast?.cancel()
            toast = android.widget.Toast.makeText(
                this, 
                "再按一次退出应用", 
                android.widget.Toast.LENGTH_SHORT
            )
            toast?.show()
        }
    }

    // 处理所有返回事件
    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            handleBackPress()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    // 处理手势返回 - 通过onBackPressed处理

    // 禁用预测性返回手势（Android 13+）
    override fun onUserLeaveHint() {
        // 空实现，禁用预测性返回手势
    }
    
    // 添加公共方法供WebAppInterface调用
    fun evaluateJavaScript(script: String) {
        webView.evaluateJavascript(script, null)
    }
}

/**
 * WebAppInterface类用于提供JavaScript与Android之间的交互接口
 */
class WebAppInterface(private val context: Context, private val mainActivity: MainActivity?) {
    
    /**
     * 控制屏幕常亮功能，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun setKeepScreenOn(keepScreenOn: Boolean) {
        if (mainActivity != null) {
            mainActivity.runOnUiThread {
                if (keepScreenOn) {
                    mainActivity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    mainActivity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }
    
    /**
     * 下载文件方法，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun downloadFile(base64Data: String, fileName: String, mimeType: String) {
        try {
            // 直接写入base64字符串，不解码
            val fileData = base64Data.toByteArray(Charsets.UTF_8)
            
            // 创建文件
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            // 解析文件名中的路径
            val filePath = fileName.replace("/", java.io.File.separator)
            val targetFile = java.io.File(downloadsDir, filePath)
            
            // 确保父目录存在
            val parentDir = targetFile.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            
            // 写入文件
            java.io.FileOutputStream(targetFile).use { fos ->
                fos.write(fileData)
            }
            
            // 通知下载完成
            android.widget.Toast.makeText(
                context, 
                "存档保存成功: ${targetFile.absolutePath}", 
                android.widget.Toast.LENGTH_LONG
            ).show()
            
            // 扫描文件，使其在文件管理器中可见
            val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = android.net.Uri.fromFile(targetFile)
            context.sendBroadcast(mediaScanIntent)
            
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context, 
                "下载失败: ${e.message}", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * 读取文件内容，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun readFile(fileName: String): String? {
        return try {
            // 从assets目录读取文件
            val inputStream = context.assets.open(fileName)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            // 如果从assets读取失败，尝试从下载目录读取
            try {
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                )
                val filePath = fileName.replace("/", java.io.File.separator)
                val targetFile = java.io.File(downloadsDir, filePath)
                if (targetFile.exists()) {
                    targetFile.readText(Charsets.UTF_8)
                } else {
                    null
                }
            } catch (ex: Exception) {
                null
            }
        }
    }
    
    /**
     * 显示Toast消息，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun showToast(message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 获取设备信息，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun getDeviceInfo(): String {
        return "Android ${android.os.Build.VERSION.RELEASE}"
    }
    
    /**
     * 选择文件并读取内容，供JavaScript调用
     */
    @android.webkit.JavascriptInterface
    fun pickFile(mimeType: String, onSuccess: String, onError: String) {
        if (mainActivity != null) {
            mainActivity.runOnUiThread {
                mainActivity.pickFile(mimeType, onSuccess, onError)
            }
        } else {
            android.widget.Toast.makeText(context, "无法打开文件选择器", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 选择图片文件并返回Base64内容，供JavaScript调用
     * 使用原生文件选择器，无需权限
     */
    @android.webkit.JavascriptInterface
    fun pickImage(onSuccess: String, onError: String) {
        if (mainActivity != null) {
            mainActivity.runOnUiThread {
                mainActivity.pickImage(onSuccess, onError)
            }
        } else {
            android.widget.Toast.makeText(context, "无法打开图片选择器", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    @android.webkit.JavascriptInterface
    fun pickMultipleImages(onSuccess: String, onError: String) {
        if (mainActivity != null) {
            mainActivity.runOnUiThread {
                mainActivity.pickMultipleImages(onSuccess, onError)
            }
        } else {
            android.widget.Toast.makeText(context, "无法打开图片选择器", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}