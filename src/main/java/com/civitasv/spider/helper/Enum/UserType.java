package com.civitasv.spider.helper.Enum;

public enum UserType {
    IndividualDevelopers(0, "个人开发者"),
    IndividualCertifiedDeveloper(1, "个人认证开发者"),
    EnterpriseDeveloper(2, "企业开发者");

    private final String description;
    private final Integer code;

    UserType(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static UserType getUserType(String description) {
        for (UserType value : UserType.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static UserType getUserType(Integer code) {
        for (UserType value : UserType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
