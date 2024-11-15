package test.config;

public class ServerConfig {
    public static final int MAX_CONTENT_LENGHT = 10 * 1024 * 102;
    public static final int MAX_BACK_LOG_SIZE = 128;
    public static final int PORT = 8090;
    public static final String LINK_ADD_DEVICE = "http://localhost:8090/test/post/addDevice";
    public static final String LINK_DELETE_DEVICE = "http://localhost:8090/test/post/deleteDevice";
    public static final String LINK_GET_DEVICE_INFORMATION = "http://localhost:8090/test/get/listOfDevices";
    public static final String LINK_ENTRY = "http://localhost:8090/test/post/entry";
}
