package com.lcc.mqtt.constant;

public enum MqttQos {
    ZERO(0),
    ONE(1),
    TWO(2),
    ;

    int code;

    public int get() {
        return code;
    }

    MqttQos(int code) {
        this.code = code;
    }
}
