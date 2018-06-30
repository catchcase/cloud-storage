package org.catchcase.cloudcomputing.webserver.backend;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * class CloudStorageBucket
 *
 * This class implements the bucket in which the key/value pair will be stored
 * as MyKeyValue objects.
 */
public class CloudStorageBucket {

    private final static Logger logger = Logger.getLogger(CloudStorageBucket.class.getName());
    private String fileName;
    private File file;
    private HashMap hashTable;
    private Semaphore lock = new Semaphore(1);
    private AzureStorage azureStorage;


    public CloudStorageBucket(int bucketNumber){
        fileName = "bucket" + bucketNumber + ".txt";
        file = new File(fileName);
        hashTable = new HashMap(1, 0.5F);
        azureStorage = AzureStorage.getAzureInstance();
    }

    /**
     * boolean add (MyKeyValue myKeyValue)
     *
     * This method adds the myKeyValue object into the hash map if not already
     * existing.
     * Further it downloads/uploads the file where the hash map is stored from/to the
     * Azure FileStorage and reads/writes it.
     * This method is also thread safe, because the of the semaphore.
     *
     * @param myKeyValue - key/value pair as MyKeyValue object
     *
     * @return a boolean true, if add operation was successful, else returns false
     */
    public boolean add(MyKeyValue myKeyValue){
        lock.acquireUninterruptibly();

        try {
            read();

            if(hashTable.containsKey(myKeyValue.getKey())) {
                logger.info("Key is already existing.");

                return false;
            } else {
                hashTable.put(myKeyValue.getKey(), myKeyValue.getValue());
                logger.info("KeyValuePair successfully stored.");
                write();

                return true;
            }
        } finally {
            lock.release();
        }
    }

    /**
     * boolean delete (int key)
     *
     * This method deletes the key/value pair from the hash map if existing.
     * Further it downloads/uploads the file where the hash map is stored from/to the
     * Azure FileStorage and reads/writes it.
     * This method is also thread safe, because the of the semaphore.
     *
     * @param key - key of the wanted value
     *
     * @return a boolean true, if delete operation was successful, else returns false
     */
    public boolean delete(int key){
        lock.acquireUninterruptibly();

        try {
            read();

            if(hashTable.containsKey(key)) {
                hashTable.remove(key);
                logger.info("KeyValuePair successfully removed.");
                write();

                return true;
            } else {
                logger.info("Key not existing.");

                return false;
            }
        } finally {
            lock.release();
        }
    }

    /**
     * MyKeyValue getKeyValue (int key)
     *
     * This method searches for the key/value pair from the hash map if existing.
     * Further it downloads the file where the hash map is stored from the
     * Azure FileStorage and reads it.
     * This method is also thread safe, because the of the semaphore.
     *
     * @param key - key of the wanted value
     *
     * @return the key/value pair as MyKeyValue object
     */
    public MyKeyValue getKeyValue(int key){
        lock.acquireUninterruptibly();
        try {
            read();

            if (hashTable.containsKey(key)) {
                MyKeyValue myKeyValue = new MyKeyValue();

                myKeyValue.setKey(key);
                myKeyValue.setValue(hashTable.get(key).toString());
                logger.info("KeyValuePair successfully acquired.");

                return myKeyValue;
            } else {
                logger.info("Key not existing.");
                return null;
            }
        } finally {
            lock.release();
        }
    }

    /**
     * List<String> getKeyValue ()
     *
     * This method retrieves all key/value pairs from the hash map if existing.
     * Further it downloads the file where the hash map is stored from the
     * Azure FileStorage and reads it.
     * This method is also thread safe, because the of the semaphore.
     *
     * @return a List of key/value pairs as String
     */
    public List<String> getEntries() {
        lock.acquireUninterruptibly();

        List<String> result = new ArrayList<>();
        MyKeyValue temp = new MyKeyValue();

        read();

        try {
            hashTable.keySet().forEach(key -> {
                temp.setKey(key.hashCode());
                temp.setValue(hashTable.get(key).toString());
                result.add(temp.toString());
            });

            return result;
        } finally {
            lock.release();
        }
    }

    /**
     * void write ()
     *
     * This method creates a file if not already existing.
     * Write the hash map into this file and uploads it to the
     * Azure FileStorage.
     */
    private void write(){
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
            oos.writeObject(hashTable);
            azureStorage.uploadBucket(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * void read ()
     *
     * This method creates a empty file if not already existing.
     * Downloads a file from the Azure FileStorage, overrides the current ones
     * and read the content of the file and put it into the hash map.
     */
    private void read() {
        ObjectInputStream ois = null;

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            azureStorage.downloadBucket(file);
            ois = new ObjectInputStream(new FileInputStream(file));
            hashTable = (HashMap) ois.readObject();
        } catch (EOFException e) {
            //Do nothing, this Exception just tells that he has read all the file
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
