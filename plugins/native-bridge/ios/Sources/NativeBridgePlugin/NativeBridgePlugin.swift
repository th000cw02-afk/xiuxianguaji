import Foundation
import Capacitor
import UIKit
import UniformTypeIdentifiers

@objc(NativeBridgePlugin)
public class NativeBridgePlugin: CAPPlugin, CAPBridgedPlugin, UIDocumentPickerDelegate {
    public let identifier = "NativeBridgePlugin"
    public let jsName = "NativeBridge"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "downloadFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "pickFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "showToast", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setKeepScreenOn", returnType: CAPPluginReturnPromise),
    ]

    private var pickFileCall: CAPPluginCall?

    @objc func downloadFile(_ call: CAPPluginCall) {
        guard let data = call.getString("data"),
              let fileName = call.getString("fileName") else {
            call.reject("Missing data or fileName")
            return
        }

        let sanitized = fileName.replacingOccurrences(of: "..", with: "_")
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        var target = docs.appendingPathComponent(sanitized)

        let parent = target.deletingLastPathComponent()
        try? FileManager.default.createDirectory(at: parent, withIntermediateDirectories: true)

        do {
            if let utf8 = data.data(using: .utf8) {
                try utf8.write(to: target)
            } else {
                try data.write(to: target, atomically: true, encoding: .utf8)
            }

            DispatchQueue.main.async {
                let activity = UIActivityViewController(activityItems: [target], applicationActivities: nil)
                if let popover = activity.popoverPresentationController {
                    popover.sourceView = self.bridge?.viewController?.view
                    popover.sourceRect = CGRect(x: UIScreen.main.bounds.midX, y: UIScreen.main.bounds.midY, width: 0, height: 0)
                }
                self.bridge?.viewController?.present(activity, animated: true)
            }

            call.resolve(["path": target.path])
        } catch {
            call.reject("Failed to save file: \(error.localizedDescription)")
        }
    }

    @objc func pickFile(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.pickFileCall = call
            let types: [UTType] = [.plainText, .text, .data, .content]
            let picker = UIDocumentPickerViewController(forOpeningContentTypes: types, asCopy: true)
            picker.delegate = self
            picker.allowsMultipleSelection = false
            self.bridge?.viewController?.present(picker, animated: true)
        }
    }

    public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        guard let call = pickFileCall else { return }
        pickFileCall = nil

        guard let url = urls.first else {
            call.reject("未选择文件")
            return
        }

        let accessing = url.startAccessingSecurityScopedResource()
        defer {
            if accessing { url.stopAccessingSecurityScopedResource() }
        }

        do {
            let content = try String(contentsOf: url, encoding: .utf8)
            call.resolve(["content": content])
        } catch {
            call.reject("无法读取文件内容: \(error.localizedDescription)")
        }
    }

    public func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        guard let call = pickFileCall else { return }
        pickFileCall = nil
        call.reject("取消选择")
    }

    @objc func showToast(_ call: CAPPluginCall) {
        guard let message = call.getString("message") else {
            call.reject("Missing message")
            return
        }

        DispatchQueue.main.async {
            guard let view = self.bridge?.viewController?.view else {
                call.resolve()
                return
            }

            let label = UILabel()
            label.text = message
            label.textColor = .white
            label.backgroundColor = UIColor(white: 0.1, alpha: 0.85)
            label.textAlignment = .center
            label.numberOfLines = 0
            label.font = .systemFont(ofSize: 14)
            label.layer.cornerRadius = 8
            label.clipsToBounds = true
            label.alpha = 0

            let padding: CGFloat = 12
            let maxWidth = view.bounds.width - 40
            let size = label.sizeThatFits(CGSize(width: maxWidth - padding * 2, height: .greatestFiniteMagnitude))
            label.frame = CGRect(
                x: (view.bounds.width - size.width - padding * 2) / 2,
                y: view.bounds.height - 120,
                width: size.width + padding * 2,
                height: size.height + padding * 2
            )

            view.addSubview(label)

            UIView.animate(withDuration: 0.25, animations: {
                label.alpha = 1
            }, completion: { _ in
                UIView.animate(withDuration: 0.25, delay: 1.5, options: [], animations: {
                    label.alpha = 0
                }, completion: { _ in
                    label.removeFromSuperview()
                })
            })

            call.resolve()
        }
    }

    @objc func setKeepScreenOn(_ call: CAPPluginCall) {
        let keepOn = call.getBool("keepScreenOn") ?? true
        DispatchQueue.main.async {
            UIApplication.shared.isIdleTimerDisabled = keepOn
            call.resolve()
        }
    }
}
