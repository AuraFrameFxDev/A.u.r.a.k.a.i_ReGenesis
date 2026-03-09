// IAuraDriveService.aidl
package dev.aurakai.auraframefx.ipc;

// Import the callback interface
import dev.aurakai.auraframefx.ipc.IAuraDriveCallback;
import android.net.Uri;
import android.os.Bundle;

/**
 * Interface for AuraDriveService IPC communication
 * 
 * This interface defines the contract for secure file operations with built-in
 * memory integrity verification through the R.G.S.F. (Royal Guard Security Framework).
 */
interface IAuraDriveService {
    // Service information
    String getServiceVersion();
    
    // Connection management
    void registerCallback(IAuraDriveCallback callback);
    void unregisterCallback(IAuraDriveCallback callback);
    
    // Command execution
    // Use Bundle for structured, parcelable parameters to avoid deprecated Parcel APIs
    String executeCommand(String command, in Bundle params);

    // Module management
    String toggleLSPosedModule(String packageName, boolean enable);
    
    // Status and diagnostics
    String getOracleDriveStatus();
    String getDetailedInternalStatus();
    String getInternalDiagnosticsLog();
    
    // System information
    String getSystemInfo();
    
    // Configuration
    // Use Bundle for configuration to avoid deprecated Parcel.readHashMap
    boolean updateConfiguration(in Bundle config);

    // Event subscription
    void subscribeToEvents(int eventTypes);
    void unsubscribeFromEvents(int eventTypes);
    
    // File Operations with R.G.S.F. Integration
    
    /**
     * Imports a file from the specified URI into the OracleDrive secure storage.
     * 
     * This method reads the file content from the provided URI, verifies its integrity
     * using the R.G.S.F. MemoryVerifier, and stores it in the secure storage.
     * 
     * @param uri The content URI of the file to import
     * @return String A unique file ID assigned to the imported file
     * @throws SecurityException if the operation is not permitted
     * @throws IOException if an I/O error occurs during the operation
     */
    String importFile(in Uri uri);
    
    /**
     * Exports a file from OracleDrive secure storage to the specified destination URI.
     * 
     * This method reads the file content from secure storage, verifies its integrity
     * using the R.G.S.F. MemoryVerifier, and writes it to the specified destination URI.
     * 
     * @param fileId The unique ID of the file to export
     * @param destinationUri The content URI where the file should be exported
     * @return boolean True if the export was successful, false otherwise
     * @throws SecurityException if the operation is not permitted
     * @throws IOException if an I/O error occurs during the operation
     * @throws FileNotFoundException if the specified fileId does not exist
     */
    boolean exportFile(String fileId, in Uri destinationUri);
    
    /**
     * Verifies the integrity of a file in the OracleDrive secure storage.
     * 
     * This method performs an integrity check on the specified file using the R.G.S.F. MemoryVerifier.
     * 
     * @param fileId The unique ID of the file to verify
     * @return boolean True if the file's integrity is verified, false otherwise
     * @throws FileNotFoundException if the specified fileId does not exist
     */
    boolean verifyFileIntegrity(String fileId);
}