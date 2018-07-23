package com.thinkopen.servlet.response;

public interface Response {

    void setRootTag(String name);
    void addAttribute(String name, String value);
    String get();
}
