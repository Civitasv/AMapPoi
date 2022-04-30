package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum UserType {
    IndividualDevelopers(0, "个人开发者"),
    IndividualCertifiedDeveloper(1, "个人认证开发者"),
    EnterpriseDeveloper(2, "企业开发者");

    private final Integer code;
    private final String description;

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
