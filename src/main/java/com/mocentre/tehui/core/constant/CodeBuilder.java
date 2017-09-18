package com.mocentre.tehui.core.constant;

import com.mocentre.tehui.core.utils.response.BaseResult;

public interface CodeBuilder {

    public void build(BaseResult result);

    public void build(BaseResult result, Object... args);

}
