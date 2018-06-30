package org.catchcase.cloudcomputing.webserver.backend;

/**
 * class CloudStorageLogger
 *
 * This class implements the log messages and sends them to the webServer.
 */
public class CloudStorageLogger {
    private String msg;
    private boolean status;

    /**
     * String getMsg ()
     *
     * @return the message as String
     */
    public String getMsg() {
        return msg;
    }

    /**
     * void setMsg (String msg)
     *
     * @param msg - msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * boolean isStatus ()
     *
     * @return the status of operation as boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * void setStatus (boolean status)
     *
     * @param status - status of operation
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
}
