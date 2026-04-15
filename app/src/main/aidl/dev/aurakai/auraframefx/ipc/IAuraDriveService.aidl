// IAuraDriveService.aidl
package dev.aurakai.auraframefx.ipc;

import android.net.Uri;
import android.os.Bundle;
import dev.aurakai.auraframefx.ipc.IAuraDriveCallback;

/**
 * Interface for AuraDriveService IPC communication
 */
interface IAuraDriveService {
    String getServiceVersion();
    
    void registerCallback(IAuraDriveCallback callback);
    void unregisterCallback(IAuraDriveCallback callback);
    
    String executeCommand(String command, in Bundle params);
    String toggleLSPosedModule(String packageName, boolean enable);
    
    String getOracleDriveStatus();
    String getDetailedInternalStatus();
    String getInternalDiagnosticsLog();
    
    String getSystemInfo();
    
    boolean updateConfiguration(in Bundle config);
    void subscribeToEvents(int eventTypes);
    void unsubscribeFromEvents(int eventTypes);

    String importFile(in Uri uri);
    boolean exportFile(String fileId, in Uri destinationUri);
    boolean verifyFileIntegrity(String fileId);
}
