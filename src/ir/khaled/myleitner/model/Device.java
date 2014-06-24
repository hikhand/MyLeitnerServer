package ir.khaled.myleitner.model;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ir.khaled.myleitner.Helper.DatabaseHelper;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 4/30/2014.
 */
public class Device {
    public static final String COLUMN_UDK = "UDK";
    public static final String COLUMN_ID = "ID";
    public static final String PARAM_UDK = "udk";
    private static final String PARAM_DEVICE_INFO = "deviceInfo";
    private static PreparedStatement statementDeviceCheck;
    private static PreparedStatement statementRegisterDevice;
    private static PreparedStatement statementDeviceId;
    private static Gson gson = new Gson();
    public int id;
    public String udk;

    public static boolean isDeviceValid(Request request) throws SQLException {
        String udk = request.getParamValue(PARAM_UDK);
        if (udk == null)
            return false;

        return isDeviceValid(udk);
    }

    public static Response handleRegisterDevice(Request request) throws Exception {
        Response response = new Response();

        String jsonDeviceInfo = request.getParamValue(PARAM_DEVICE_INFO);
        if (jsonDeviceInfo == null || jsonDeviceInfo.length() == 0) {
//            response.success = false;//TODO decide whether allow without device info !
            response.success = true;
            return response;
        }

        DeviceInfo deviceInfo = getDeviceInfo(jsonDeviceInfo);
        if (deviceInfo == null) {
            response.success = true;
            return response;
        }

        PreparedStatement statement = getStatementRegisterDevice();
        statement.setString(1, deviceInfo.system.udk);
        statement.setInt(2, deviceInfo.display.densityDpi);
        statement.setDouble(3, deviceInfo.display.size_inches);
        statement.setInt(4, deviceInfo.display.height);
        statement.setFloat(5, deviceInfo.display.density);
        statement.setInt(6, deviceInfo.display.width);
        statement.setFloat(7, deviceInfo.display.xdpi);
        statement.setFloat(8, deviceInfo.display.ydpi);
        statement.setFloat(9, deviceInfo.memory.storage_external);
        statement.setFloat(10, deviceInfo.memory.storage_external_free);
        statement.setFloat(11, deviceInfo.memory.storage_internal);
        statement.setFloat(12, deviceInfo.memory.ram_size);
        statement.setString(13, deviceInfo.processor.cpu_abi);
        statement.setString(14, deviceInfo.processor.cpu_abi2);
        statement.setInt(15, deviceInfo.processor.frequency_max);
        statement.setInt(16, deviceInfo.processor.cores);
        statement.setString(17, deviceInfo.system.android_id);
        statement.setString(18, deviceInfo.system.bluetooth_address);
        statement.setString(19, deviceInfo.system.board);
        statement.setString(20, deviceInfo.system.brand);
        statement.setString(21, deviceInfo.system.deviceName);
        statement.setString(22, deviceInfo.system.displayName);
        statement.setString(23, deviceInfo.system.label);
        statement.setString(24, deviceInfo.system.imei);
        statement.setString(25, deviceInfo.system.manufacture);
        statement.setString(26, deviceInfo.system.model);
        statement.setString(27, deviceInfo.system.product);
        statement.setString(28, deviceInfo.system.wlan_address);
        statement.setInt(29, deviceInfo.system.sdk_version);
        statement.executeUpdate();
        response.success = true;
        return response;
    }

    public static int getDeviceId(String udk) throws Exception {
        PreparedStatement statement = getStatementDeviceId();
        statement.setString(1, udk);
        ResultSet resultSet = statement.executeQuery();

        //go to first
        resultSet.first();
        return resultSet.getInt(COLUMN_ID);
    }

    /**
     * checks whether a device is valid or not
     *
     * @param udk is the device key and used to find the device
     * @return true if device is valid else otherwise
     */
    private static boolean isDeviceValid(String udk) throws SQLException {
        PreparedStatement statement = getStatementDeviceCheck();
        statement.setString(1, udk);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet == null)
            return false;

        if (resultSet.first())
            return resultSet.getString(COLUMN_UDK) != null;
        return false;
    }

    private static DeviceInfo getDeviceInfo(String jsonDeviceInfo) throws Exception {
        return gson.fromJson(jsonDeviceInfo, DeviceInfo.class);
    }

    private static PreparedStatement getStatementRegisterDevice() throws SQLException {
        if (statementRegisterDevice == null) {
            statementRegisterDevice = DatabaseHelper.getConnection().prepareStatement("INSERT INTO DEVICE (UDK, DENSITY_DPI, SIZE_INCHES, HEIGHT, DENSITY, WIDTH, XDPI, YDPI, STORAGE_EXTERNAL, STORAGE_EXTERNAL_FREE, STORAGE_INTERNAL, RAM_SIZE, CPU_ABI, CPU_ABI2, MAX_FREQUENCY, CORES, ANDROID_ID, BLUETOOTH_ADDRESS, BOARD, BRAND, DEVICE_NAME, DISPLAY_NAME, LABEL, IMEI, MANUFACTURE, MODEL, PRODUCT, WLAN_ADDRESS, SDK_VERSION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        }
        return statementRegisterDevice;
    }

    private static PreparedStatement getStatementDeviceId() throws SQLException {
        if (statementDeviceId == null) {
            statementDeviceId = DatabaseHelper.getConnection().prepareStatement("SELECT ID FROM device WHERE UDK = ?");
        }
        return statementDeviceId;
    }

    private static PreparedStatement getStatementDeviceCheck() throws SQLException {
        if (statementDeviceCheck == null) {
            statementDeviceCheck = DatabaseHelper.getConnection().prepareStatement("SELECT UDK FROM DEVICE WHERE UDK=?");
        }
        return statementDeviceCheck;
    }


    public class DeviceInfo {
        public SystemInfo system;
        public ProcessorInfo processor;
        public MemoryInfo memory;
        public DisplayInfo display;

        public class SystemInfo {
            public String label;
            public String displayName;
            public String product;
            public String deviceName;
            public String board;
            public String brand;
            public String model;
            public String manufacture;
            public String imei;
            public String wlan_address;
            public String bluetooth_address;
            public int sdk_version;
            public String android_id;
            public String udk;
        }

        public class ProcessorInfo {
            public String cpu_abi;
            public String cpu_abi2;
            public int cores;
            public int frequency_max;
        }

        public class MemoryInfo {
            public long ram_size;
            public long storage_internal;
            public long storage_external;
            public long storage_external_free;
        }

        public class DisplayInfo {
            public int width = 0;
            public int height = 0;
            public float size_inches;
            public float density;
            public int densityDpi;
            public float xdpi;
            public float ydpi;
        }

    }
}
