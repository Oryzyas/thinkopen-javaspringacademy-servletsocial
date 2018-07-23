package com.thinkopen.servlet.response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class JsonResponse implements Response {
    private String rootTag;
    private Map<String, String> values;

    public JsonResponse() {
        values = new HashMap<>();
    }

    @Override
    public void setRootTag(String name) {
        this.rootTag = name;
    }

    @Override
    public void addAttribute(String name, String value) {
        this.values.put(name, value);
    }

    @Override
    public String get() {
        final StringBuilder sb = new StringBuilder();

        if(rootTag != null) {
            sb.append(rootTag);
            sb.append(":");
        }

        sb.append("{");

        final Iterator<Map.Entry<String, String>> iter = values.entrySet().iterator();
        Map.Entry<String, String> entry;

        if(iter.hasNext()) {
            entry = iter.next();

            sb.append('"' + entry.getKey() + '"');
            sb.append(":");
            sb.append('"' + entry.getValue() + '"');
        }

        while(iter.hasNext()) {
            entry = iter.next();

            sb.append(",");
            sb.append('"' + entry.getKey() + '"');
            sb.append(":");
            sb.append('"' + entry.getValue() + '"');
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return get();
    }
}
