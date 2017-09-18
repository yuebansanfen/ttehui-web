package com.mocentre.tehui.core.utils.response;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mocentre.tehui.core.constant.ResultCode;

public class BaseResult implements Serializable {

    private static final long serialVersionUID = -7443513247351024039L;

    private String            code;

    private String            message;

    private Boolean           success;

    public BaseResult() {
        this.success = true;
        this.code = "200";
        this.message = "success";
    }

    @SuppressWarnings("unchecked")
    public <R extends BaseResult> R setErrorMessage(String code, String message) {
        this.code = code;
        this.success = false;
        this.message = message;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public <R extends BaseResult> R setErrorMessage(ResultCode code, Object... args) {
        this.code = code.name();
        this.success = false;
        this.message = String.format(code.getMsg(), args);
        return (R) this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean isSuccess() {
        return getSuccess();
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String toJsonString() {
        //return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue);
    }

}
