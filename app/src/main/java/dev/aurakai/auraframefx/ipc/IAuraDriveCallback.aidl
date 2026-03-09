// IAuraDriveCallback.aidl
package dev.aurakai.auraframefx.ipc;

// Callback interface for service-to-client communication
oneway interface IAuraDriveCallback {
    // Event types
    // Connection events
    void onConnected();
    void onDisconnected(String reason);
    
    // Status updates
    void onStatusUpdate(in String status);
    void onError(int errorCode, String errorMessage);
    
    // Data events
    void onDataReceived(String dataType, in byte[] data);
    void onEvent(int eventType, in String eventData);
    
    // Module management
    void onModuleStateChanged(String packageName, boolean enabled);
    
    // System events
    void onSystemEvent(int eventType, in String eventData);
    
    // Deprecated - kept for backward compatibility
    void onServiceEvent(int eventType, in String message);
}