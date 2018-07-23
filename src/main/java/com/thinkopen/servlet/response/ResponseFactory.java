package com.thinkopen.servlet.response;

public final class ResponseFactory {

    public enum ResponseType {
        JSON, XML;

        public static ResponseType parse(String str) {
            if(str == null || str.isEmpty())
                return null;

            for(ResponseType type : values()) {
                if(str.equalsIgnoreCase(type.toString()))
                    return type;
            }

            return null;
        }
    }

    public static Response createResponse(ResponseType type) {
        switch (type) {
            case JSON:
                return new JsonResponse();
            case XML:
                return new XmlResponse();
            default:
                return null;
        }
    }

}
