package com.civitasv.spider.helper;

public enum UserType {
    IndividualDevelopers("个人开发者"),
    IndividualCertifiedDeveloper("个人认证开发者"),
    EnterpriseDeveloper("企业开发者");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public static UserType getUserType(String description){
        for (UserType value : UserType.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
