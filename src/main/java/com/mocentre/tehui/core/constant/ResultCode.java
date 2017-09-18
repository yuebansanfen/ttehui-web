package com.mocentre.tehui.core.constant;

import com.mocentre.tehui.core.utils.CodeBuildHelper;
import com.mocentre.tehui.core.utils.response.BaseResult;

public enum ResultCode implements CodeBuilder {
    USER_LOGIN_INCORRECT("the user %s with password %s is incorrect"),
    DATA_NOT_EXISTS("the query data %s not exists"),
    OPERATE_EXCEPTION("there is some unknown exception occurred "),
    PARAMER_ILLEGLE("illegle param, %s must not null"),
    PARAMER_LENGTH_ILLEGLE("illegle param length"),
    CUSTOM_MESSAGE("%s");

    private String msg;

    ResultCode(String msg) {
        this.msg = msg;
    }

    @Override
    public void build(BaseResult result) {
        CodeBuildHelper.build(this.name(), this.msg, result);
    }

    @Override
    public void build(BaseResult result, Object... args) {
        CodeBuildHelper.build(this.name(), this.msg, result, args);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
