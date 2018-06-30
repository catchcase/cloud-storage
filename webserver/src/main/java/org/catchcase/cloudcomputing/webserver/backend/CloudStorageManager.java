package org.catchcase.cloudcomputing.webserver.backend;

import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * class CloudStorageManager
 *
 * This class is defined as a Singleton, which manage the distributed file
 * storage system.
 */
@Component
public class CloudStorageManager {

    private final static Logger logger = Logger.getLogger(CloudStorageManager.class.getName());
    private static CloudStorageManager managerInstance = null;
    private static Semaphore lock = new Semaphore(1);
    private CloudStorageHashDirectory hashDirectory;
    private CloudStorageLogger cloudLogger;

    private CloudStorageManager() {
        hashDirectory = CloudStorageHashDirectory.getDirectoryInstance();
        cloudLogger = new CloudStorageLogger();
    }

    /**
     * CloudStorageManager getManagerInstance ()
     *
     * Instantiate a new CloudStorageManger class, if not already existing.
     *
     * @return new CloudStorageManager or the existing CloudStorageManager
     */
    public static CloudStorageManager getManagerInstance() {
        lock.acquireUninterruptibly();
        try {
            if (managerInstance == null) {
                logger.info("New Cloud Storage Manager instantiated.");
                managerInstance = new CloudStorageManager();
            } else {
                logger.info("Cloud Storage Manager already instantiated.");
            }
        } finally {
            lock.release();
            return managerInstance;
        }
    }

    /**
     * CloudStorageLogger insert (int key, String value)
     *
     * Calculates the hash value of the given key, to know in which bucket it
     * should be stored.
     * Creates a MyKeyValue object with the given parameters and pass it to
     * the hashDirectory.
     *
     * @param key   - key for the value
     * @param value - value which will be stored
     *
     * @return a CloudStorageLogger which contains the Status of operation
     *      as boolean and a Message as String
     */
    public CloudStorageLogger insert(int key, String value) {
        int bucketNumber = hashFunctionDecideBucket(key);

        MyKeyValue myKeyValue = new MyKeyValue();
        myKeyValue.setKey(key);
        myKeyValue.setValue(value);

        cloudLogger.setStatus(hashDirectory.add(bucketNumber, myKeyValue));

        if (cloudLogger.isStatus()) {
            cloudLogger.setMsg("Key: " + key + " with value: " + value + " will be stored in bucket " + bucketNumber);
            logger.info("Key: " + key + " with value: " + value + " will be stored in bucket " + bucketNumber);
        } else {
            cloudLogger.setMsg("Key already exist.");
            logger.info("Key already exist.");
        }
        return cloudLogger;
    }

    /**
     * CloudStorageLogger delete (int key)
     *
     * Calculates the hash value of the given key, to know in which bucket it
     * should search and delete the value.
     *
     * @param key - key of the wanted value
     *
     * @return a CloudStorageLogger which contains
     *      the Status of operation as boolean and a Message as String
     */
    public CloudStorageLogger delete(int key) {
        int bucketNumber = hashFunctionDecideBucket(key);

        cloudLogger.setStatus(hashDirectory.remove(bucketNumber, key));

        if (cloudLogger.isStatus()) {
            cloudLogger.setMsg("Data with key: " + key + " deleted from bucket " + bucketNumber);
            logger.info("Data with key: " + key + " deleted from bucket " + bucketNumber);
        } else {
            cloudLogger.setMsg("Key not existing.");
            logger.info("Key not existing.");
        }
        return cloudLogger;
    }

    /**
     * CloudStorageLogger search (int key)
     *
     * Calculates the hash value of the given key, to know in which bucket it
     * should search the value.
     *
     * @param key - key of the wanted value
     *
     * @return a CloudStorageLogger which contains
     *      the Status of operation as boolean and a Message as String
     */
    public CloudStorageLogger search(int key) {
        int bucketNumber = hashFunctionDecideBucket(key);

        if (hashDirectory.getKeyValuePair(bucketNumber, key) != null) {
            MyKeyValue myKeyValue = hashDirectory.getKeyValuePair(bucketNumber, key);

            cloudLogger.setStatus(true);
            cloudLogger.setMsg("Data: " + myKeyValue.getValue() + " with key: " + key + " found in bucket " + bucketNumber);
            logger.info("Data with key: " + key + " found in bucket " + bucketNumber);

            return cloudLogger;
        } else {
            cloudLogger.setStatus(false);
            cloudLogger.setMsg("Data with key: " + key + " not found.");
            logger.info("Data with key: " + key + " not found.");
            return cloudLogger;
        }
    }

    /**
     * List<String> rangeQuery(int key1, int key2)
     *
     * It searches for every value between the two keys.
     *
     * @param key1 - first key to determine the beginning of the range
     * @param key2 - second key to determine the end of the range
     *
     * @return a List of String containing the key and the value
     */
    public List<String> rangeQuery(int key1, int key2) {
        int temp = 0;
        List<String> result = new ArrayList<>();

        if (key1 > key2) {
            temp = key1;
            key1 = key2;
            key2 = temp;
        }

        for (int i = key1; i <= key2; i++) {
            if (search(i) != null) {
                result.add(search(i).getMsg());
            }
        }
        logger.info(result.size() + " Entry/ies found.");
        return result;
    }

    /**
     * List<String> listEntries (int bucketNumber)
     *
     * This method will get the whole bucket as a list.
     *
     * @param bucketNumber - the number of the bucket which should be displayed
     *
     * @return a List of String containing the key and the value of the wanted bucket
     */
    public List<String> listEntries(int bucketNumber) {
        return hashDirectory.getEntries(bucketNumber);
    }

    /**
     * List<String> listAllEntries ()
     *
     * It will get all buckets as a list.
     *
     * @return a List of String containing the key and the value of the wanted bucket
     */
    public List<String> listAllEntries() {
        return hashDirectory.getAllEntries();
    }

    /**
     * int hashFunctionDecideBucket (int key)
     *
     * Calculates the hash value of the given key with the modulo operator.
     *
     * @param key - key of the value
     *
     * @return a hash value
     */
    private int hashFunctionDecideBucket(int key) {
        return key % hashDirectory.getSize();
    }

}
