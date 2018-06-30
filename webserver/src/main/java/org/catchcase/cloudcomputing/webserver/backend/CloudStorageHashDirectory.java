package org.catchcase.cloudcomputing.webserver.backend;

import java.util.*;

/**
 * class CloudStorageHashDirectory
 *
 * This class is defined as a Singleton and implements a hash directory.
 * The size of this hash directory is static but modifiable.
 */
public class CloudStorageHashDirectory {

    private static final int DIRECTORY_SIZE = 4;
    private static CloudStorageHashDirectory directoryInstance = null;
    private List<CloudStorageBucket> hashTable;

    private CloudStorageHashDirectory(){
        hashTable = new ArrayList<>();
        for(int i = 0; i < DIRECTORY_SIZE; i++) {
            hashTable.add(new CloudStorageBucket(i));
        }
    }

    /**
     * CloudStorageHashDirectory getDirectoryInstance ()
     *
     * Instantiate a new CloudStorageHashDirectory class, if not already existing.
     *
     * @return new CloudStorageHashDirectory or the existing CloudStorageHashDirectory
     */
    public static CloudStorageHashDirectory getDirectoryInstance(){
        if(directoryInstance == null){
            directoryInstance = new CloudStorageHashDirectory();
        }
        return directoryInstance;
    }

    /**
     * boolean add (int bucketNumber, MyKeyValue keyValue)
     *
     * Gets the bucket and calls the add method of the bucket.
     *
     * @param bucketNumber - index of the bucket
     * @param keyValue - key/value pair
     *
     * @return a boolean true, if add operation was successful else return false
     */
    protected boolean add(int bucketNumber, MyKeyValue keyValue){
        return hashTable.get(bucketNumber).add(keyValue);
    }

    /**
     * boolean remove (int bucketNumber, int key)
     *
     * Gets the bucket and calls the delete method of the bucket.
     *
     * @param bucketNumber - index of the bucket
     * @param key - key of the wanted value
     *
     * @return a boolean true, if delete operation was successful else return false
     */
    protected boolean remove(int bucketNumber, int key){
        return hashTable.get(bucketNumber).delete(key);
    }

    /**
     * MyKeyValue getKeyValuePair (int bucketNumber, int key)
     *
     * Gets the bucket and calls the getKeyValue method of the bucket.
     *
     * @param bucketNumber - index of the bucket
     * @param key - key of the wanted value
     *
     * @return the key/value pair as MyKeyValue object
     */
    protected MyKeyValue getKeyValuePair(int bucketNumber, int key){
        return hashTable.get(bucketNumber).getKeyValue(key);
    }

    /**
     * List<String> getEntries (int bucketNumber)
     *
     * Gets the bucket and calls the getEntries method of the bucket.
     *
     * @param bucketNumber - index of the bucket
     *
     * @return a List of the key/value pairs of the bucket
     */
    protected List<String> getEntries(int bucketNumber){
        return hashTable.get(bucketNumber).getEntries();
    }

    /**
     * List<String> getAllEntries ()
     *
     * Get every bucket and calls the getEntries method of the bucket.
     *
     * @return a List of the key/value pairs of all the buckets
     */
    protected List<String> getAllEntries(){
        List<String> temp = new ArrayList<>();

        hashTable.forEach(bucket -> {
            temp.add("Bucket " + hashTable.indexOf(bucket));
            bucket.getEntries().forEach(enty -> temp.add(enty));
        });

        return temp;
    }

    /**
     * int getSize ()
     *
     * @return the size of the hash directory
     */
    protected int getSize(){
        return hashTable.size();
    }
}
