import { registerPlugin } from '@capacitor/core';
import type { NativeBridgePlugin } from './definitions';

const NativeBridge = registerPlugin<NativeBridgePlugin>('NativeBridge');

export * from './definitions';
export { NativeBridge };
