package com.example.excelmultiexportprogress.model;

import java.util.HashMap;

public class Response extends HashMap<String, Object> {

    public static Response success(String message) {
        Response restResponse = new Response();
        restResponse.setRet(0);
        restResponse.setMessage(message);
        return restResponse;
    }

    public Object getData() {
        return this.get("datas");
    }

    public Response setData(Object data) {
        if (data != null) put("data", data);
        return this;
    }

    public Response setRet(Integer ret) {
        put("ret", ret);
        return this;
    }

    public Response setMessage(String message) {
        if (message != null) put("msg", message);
        return this;
    }

    public Response ret(Integer ret,String message){
        put("ret", ret);
        if (message != null) put("msg", message);
        return this;
    }

}
