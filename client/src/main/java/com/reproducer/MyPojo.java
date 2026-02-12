package com.reproducer;

/**
 * Простой POJO класс для тестирования. Должен быть доступен только на клиенте, сервер должен загрузить его через peerClassLoading или deploymentSpi.
 */
public class MyPojo {

    private final String value;
    private final long timestamp;

    public MyPojo(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "MyPojo{value='" + value + "', timestamp=" + timestamp + "}";
    }
}
