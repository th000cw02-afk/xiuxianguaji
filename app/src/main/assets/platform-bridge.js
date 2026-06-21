/**
 * 跨平台原生桥接垫片：Android 使用 MainActivity 注入的 AndroidInterface；
 * iOS Capacitor 使用 NativeBridge 插件并挂载为同名 AndroidInterface。
 */
(function () {
  if (typeof AndroidInterface !== 'undefined') {
    return;
  }

  function waitForCapacitor(callback) {
    if (window.Capacitor && window.Capacitor.Plugins) {
      callback();
      return;
    }
    setTimeout(function () {
      waitForCapacitor(callback);
    }, 30);
  }

  function readBundledFile(fileName) {
    try {
      var xhr = new XMLHttpRequest();
      xhr.open('GET', fileName, false);
      xhr.send(null);
      if (xhr.status === 200 || xhr.status === 0) {
        return xhr.responseText;
      }
    } catch (e) {
      console.warn('readBundledFile failed:', fileName, e);
    }
    return null;
  }

  waitForCapacitor(function () {
    if (window.Capacitor.getPlatform() !== 'ios') {
      return;
    }

    var NativeBridge = window.Capacitor.Plugins.NativeBridge;
    if (!NativeBridge) {
      console.warn('NativeBridge plugin not available on iOS');
      return;
    }

    NativeBridge.setKeepScreenOn({ keepScreenOn: true }).catch(function () {});

    window.AndroidInterface = {
      setKeepScreenOn: function (keepScreenOn) {
        NativeBridge.setKeepScreenOn({ keepScreenOn: !!keepScreenOn }).catch(function () {});
      },

      downloadFile: function (base64Data, fileName, mimeType) {
        NativeBridge.downloadFile({
          data: base64Data,
          fileName: fileName,
          mimeType: mimeType || 'text/plain',
        }).catch(function (err) {
          console.error('downloadFile failed:', err);
        });
      },

      readFile: function (fileName) {
        var fromBundle = readBundledFile(fileName);
        if (fromBundle != null) {
          return fromBundle;
        }
        return null;
      },

      showToast: function (message) {
        NativeBridge.showToast({ message: message }).catch(function () {
          console.log(message);
        });
      },

      getDeviceInfo: function () {
        return 'iOS';
      },

      pickFile: function (mimeType, onSuccess, onError) {
        NativeBridge.pickFile({ mimeType: mimeType || 'text/*' })
          .then(function (result) {
            if (result && result.content != null && typeof window[onSuccess] === 'function') {
              window[onSuccess](result.content);
            }
          })
          .catch(function (err) {
            if (typeof window[onError] === 'function') {
              window[onError](err && err.message ? err.message : String(err));
            }
          });
      },

      pickImage: function (onSuccess, onError) {
        if (typeof window[onError] === 'function') {
          window[onError]('请使用页面内文件选择器');
        }
      },

      pickMultipleImages: function (onSuccess, onError) {
        if (typeof window[onError] === 'function') {
          window[onError]('请使用页面内文件选择器');
        }
      },
    };
  });
})();
