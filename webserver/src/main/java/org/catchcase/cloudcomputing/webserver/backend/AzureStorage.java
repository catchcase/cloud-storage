package org.catchcase.cloudcomputing.webserver.backend;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.file.*;

import java.io.*;
import java.net.*;
import java.security.*;

/**
 * class AzureStorage
 *
 * This class is defined as Singleton and implements the connection to the
 * Azure FileStorage.
 */
public class AzureStorage {
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName=;" +
                    "AccountKey=";

    private static AzureStorage azureInstance = null;
    private CloudStorageAccount storageAccount;
    private CloudFileClient fileClient;
    private CloudFileShare share;
    private CloudFileDirectory rootDir;
    private CloudFile cloudFile;

    private AzureStorage(){
        try{
            //Connect to storage account.
            storageAccount = CloudStorageAccount.parse(storageConnectionString);

            //Create Azure Files client.
            fileClient = storageAccount.createCloudFileClient();

            //Create Fileshare if not exisiting.
            share = fileClient.getShareReference("cca4buckets");
            share.createIfNotExists();

            //Get a reference to the root directory for the share.
            rootDir = share.getRootDirectoryReference();

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
    }

    /**
     * AzureStorage getAzureInstance ()
     *
     * Instantiate a new AzureStorage class, if not already existing.
     *
     * @return new AzureStorage or the existing AzureStorage
     */
    public static AzureStorage getAzureInstance(){
        if(azureInstance == null){
            azureInstance = new AzureStorage();
        }
        return azureInstance;
    }

    /**
     * void uploadBucket (File reference)
     *
     * This method uploads the given file to the Azure FileStorage.
     *
     * @param reference - reference of the file
     */
    protected void uploadBucket(File reference){
        try {
            cloudFile = rootDir.getFileReference(reference.getName());
            cloudFile.uploadFromFile(reference.getAbsolutePath());
        } catch (StorageException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * void downloadBucket (File reference)
     *
     * This method downloads the wanted file from the Azure FileStorage and
     * overrides the current one.
     *
     * @param reference - reference of the file
     */
    protected void downloadBucket(File reference){
        try {
            cloudFile = rootDir.getFileReference(reference.getName());
            cloudFile.downloadToFile(reference.getAbsolutePath());
        } catch (URISyntaxException | IOException | StorageException e) {
            e.printStackTrace();
        }
    }
}
