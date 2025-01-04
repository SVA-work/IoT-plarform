package config;

public class ServerConfig {
    public static final int MAX_CONTENT_LENGHT = 10 * 1024 * 102;
    public static final int MAX_BACK_LOG_SIZE = 128;
    public static final int PORT = 8091;
    public static final String LINK_ADD_DEVICE = "http://localhost:" + PORT + "/post/addDevice";
    public static final String LINK_DELETE_DEVICE = "http://localhost:" + PORT + "/post/deleteDevice";
    public static final String LINK_GET_DEVICE_INFORMATION = "http://localhost:" + PORT + "/get/listOfDevicesOfUser";
    public static final String LINK_ENTRY = "http://localhost:" + PORT + "/post/entry";
    public static final String LINK_REGISTRATION = "http://localhost:" + PORT + "/post/registration";
    public static final String LINK_ALL_AVAILABLE_RULES = "http://localhost:" + PORT + "/get/allAvailableRules";
    public static final String LINK_APPLY_RULE = "http://localhost:" + PORT + "/post/applyRule";
    public static final String LINK_DELETE_DEVICE_RULE = "http://localhost:" + PORT + "/post/deleteDeviceRule";
    public static final String LINK_DEVICE_RULES = "http://localhost:" + PORT + "/get/deviceRules";
    public static final String LINK_REPORT = "http://localhost:" + PORT + "/post/report";

    public static final String SHORT_LINK_ADD_DEVICE = "/post/addDevice";
    public static final String SHORT_LINK_DELETE_DEVICE = "/post/deleteDevice";
    public static final String SHORT_LINK_GET_DEVICE_INFORMATION = "/get/listOfDevicesOfUser";
    public static final String SHORT_LINK_ENTRY = "/post/entry";
    public static final String SHORT_LINK_REGISTRATION = "/post/registration";
    public static final String SHORT_LINK_ALL_AVAILABLE_RULES = "/get/allAvailableRules";
    public static final String SHORT_LINK_APPLY_RULE = "/post/applyRule";
    public static final String SHORT_LINK_DELETE_DEVICE_RULE = "/post/deleteDeviceRule";
    public static final String SHORT_LINK_DEVICE_RULES = "/get/deviceRules";
    public static final String SHORT_LINK_REPORT = "/post/report";
}
