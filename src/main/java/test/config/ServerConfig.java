package test.config;

public class ServerConfig {
    public static final int MAX_CONTENT_LENGHT = 10 * 1024 * 102;
    public static final int MAX_BACK_LOG_SIZE = 128;
    public static final int PORT = 8091;
    public static final String LINK_ADD_DEVICE = "http://localhost:" + PORT + "/test/post/addDevice";
    public static final String LINK_DELETE_DEVICE = "http://localhost:" + PORT + "/test/post/deleteDevice";
    public static final String LINK_GET_DEVICE_INFORMATION = "http://localhost:" + PORT + "/test/get/listOfDevices";
    public static final String LINK_ENTRY = "http://localhost:" + PORT + "/test/post/entry";
    public static final String LINK_REGISTRATION = "http://localhost:" + PORT + "/test/post/registration";
    public static final String LINK_DEVICE_RULES = "http://localhost:" + PORT + "/test/get/deviceRules";
    public static final String LINK_APPLY_RULE = "http://localhost:" + PORT + "/test/post/applyRule";
}
