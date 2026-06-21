import type { PluginListenerHandle } from '@capacitor/core';

export interface NativeBridgePlugin {
  downloadFile(options: {
    data: string;
    fileName: string;
    mimeType: string;
  }): Promise<{ path: string }>;

  pickFile(options: { mimeType: string }): Promise<{ content: string }>;

  showToast(options: { message: string }): Promise<void>;

  setKeepScreenOn(options: { keepScreenOn: boolean }): Promise<void>;
}

declare global {
  interface CapacitorPlugins {
    NativeBridge: NativeBridgePlugin;
  }
}

export type { PluginListenerHandle };
