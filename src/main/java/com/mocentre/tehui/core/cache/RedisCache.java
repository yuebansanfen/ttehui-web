/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.core.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.mocentre.tehui.backend.model.MemberAccountKeyInstance;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.constant.RedisKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.common.util.ThreadTokenHolder;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;

/**
 * 类RedisCache.java的实现描述：缓存信息
 * 
 * @author sz.gong 2017年2月13日 下午2:20:47
 */
@Component
public class RedisCache {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 保存用户信息到缓存(24小时过期)
     * 
     * @param userInfo
     * @return
     */
    public String saveLoginUser(CustomerInfoFTInstance userInfo) {
        //生成session
        String uuidKey = DigestUtils.md5Hex(CommUtil.generateUUID());
        Long id = userInfo.getId();
        String seed = DigestUtils.md5Hex(String.valueOf(id));
        // 清空之前的登录信息
        clearLoginInfoBySeed(seed);
        // 保存新的session到缓存（保存时间1天）
        int expTime = 1 * 24 * 60 * 60;
        String keySeed = RedisKeyConstant.SEED + ":" + seed;
        String keyUser = RedisKeyConstant.USER_UUID + ":" + uuidKey;
        try {
            redisTemplate.opsForValue().set(keySeed, uuidKey, expTime, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(keyUser, userInfo, expTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuidKey;
    }

    /**
     * 延长用户缓存时间(24小时过期)
     * 
     * @param userInfo
     */
    public void delayLoginUserTime(CustomerInfoFTInstance userInfo) {
        // 保存session到缓存（保存时间1天）
        int expTime = 1 * 24 * 60 * 60;
        Long id = userInfo.getId();
        String seed = DigestUtils.md5Hex(String.valueOf(id));
        String keySeed = RedisKeyConstant.SEED + ":" + seed;
        Object obj = redisTemplate.opsForValue().get(keySeed);
        if (obj != null) {
            String uuidKey = (String) obj;
            String keyUser = RedisKeyConstant.USER_UUID + ":" + uuidKey;
            redisTemplate.expire(keyUser, expTime, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取当前线程中的用户信息
     * 
     * @return
     */
    public CustomerInfoFTInstance currentLoginUser() {
        String key = RedisKeyConstant.USER_UUID + ":" + ThreadTokenHolder.getToken();
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : (CustomerInfoFTInstance) obj;
    }

    /**
     * 根据token检查用户是否登录
     * 
     * @param token
     * @return
     */
    public boolean checkLoginInfo(String token) {
        String key = RedisKeyConstant.USER_UUID + ":" + token;
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null && (CustomerInfoFTInstance) obj != null;
    }

    /**
     * 清空登录信息
     */
    public void clearLoginInfo() {
        CustomerInfoFTInstance userInfo = currentLoginUser();
        if (userInfo != null) {
            Long id = userInfo.getId();
            if (id != null) {
                String seed = DigestUtils.md5Hex(String.valueOf(id));
                clearLoginInfoBySeed(seed);
            }
        }
    }

    /**
     * 根据seed清空登录信息
     * 
     * @param seed
     */
    public void clearLoginInfoBySeed(String seed) {
        String keySeed = RedisKeyConstant.SEED + ":" + seed;
        Object obj = redisTemplate.opsForValue().get(keySeed);
        if (obj != null) {
            String sessionid = (String) obj;
            String keyUser = RedisKeyConstant.USER_UUID + ":" + sessionid;
            redisTemplate.delete(keySeed);
            redisTemplate.delete(keyUser);
        }
    }

    /**
     * 清空登入信息
     * 
     * @param request
     */
    public void clearLoginInfo(CustomerInfoFTInstance userInfo) {
        if (userInfo != null) {
            Long id = userInfo.getId();
            if (id != null) {
                String seed = DigestUtils.md5Hex(String.valueOf(id));
                clearLoginInfoBySeed(seed);
            }
        }
    }

    /**
     * 获取当前用户
     * 
     * @param request
     * @return
     */
    public CustomerInfoFTInstance currentLoginUser(HttpServletRequest request) {
        String ticket = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (Constants.COOKIE_TICKET.equals(cookie.getName())) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if (ticket == null) {
            return null;
        }
        String key = RedisKeyConstant.USER_UUID + ":" + ticket;
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : (CustomerInfoFTInstance) obj;
    }

    /**
     * 是否已登入
     * 
     * @param request
     * @return
     */
    public boolean isLoginUser(HttpServletRequest request) {
        CustomerInfoFTInstance cumInfo = this.currentLoginUser(request);
        if (cumInfo == null) {
            return false;
        }
        return true;
    }

    /**
     * 微信jsTicket保存缓存7000秒
     * 
     * @param ticket
     */
    public boolean saveJSticket(String ticket) {
        try {
            int expTime = 7000;
            String key = RedisKeyConstant.WX_JSAPI_TICKET;
            redisTemplate.opsForValue().set(key, ticket, expTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveJSticket", e);
        }
        return false;
    }

    /**
     * 获取微信jsTicket
     * 
     * @return
     */
    public String getWxJSticket() {
        try {
            String key = RedisKeyConstant.WX_JSAPI_TICKET;
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return (String) obj;
            }
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("getWxJSticket", e);
        }
        return null;
    }

    /**
     * 验证码保存缓存5分钟
     * 
     * @param telephone
     * @param vcode
     */
    public boolean saveVerificationCode(String telephone, String vcode) {
        try {
            int expTime = 5 * 60;
            String key = RedisKeyConstant.VERIFICATION_CODE + ":" + telephone;
            redisTemplate.opsForValue().set(key, vcode, expTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveVerificationCode", e);
        }
        return false;
    }

    /**
     * 获取验证码
     * 
     * @param telephone
     * @return
     */
    public String getVerificationCode(String telephone) {
        try {
            String key = RedisKeyConstant.VERIFICATION_CODE + ":" + telephone;
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return (String) obj;
            }
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("getVerificationCode", e);
        }
        return null;
    }

    /**
     * 自增验证码条数
     *
     * @param telephone
     */
    public Long saveVerificationCodeCount(String telephone) {
        try {
            int expTime = 60 * 60;
            String key = RedisKeyConstant.VERIFICATION_CODE_COUNT + ":" + telephone;
            Long count = redisTemplate.opsForValue().increment(key, 1);
            Long times = redisTemplate.getExpire(key);
            if (times <= 0) {
                redisTemplate.expire(key, expTime, TimeUnit.SECONDS);
            }
            return count;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveVerificationCodeCount", e);
        }
        return 0l;
    }

    /**
     * 自减验证码条数
     * 
     * @param telephone
     * @return
     */
    public Long reduVerificationCodeCount(String telephone) {
        try {
            String key = RedisKeyConstant.VERIFICATION_CODE_COUNT + ":" + telephone;
            Long count = redisTemplate.opsForValue().increment(key, -1);
            if (count <= 0) {
                redisTemplate.delete(key);
            }
            return count;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("reduVerificationCodeCount", e);
        }
        return 0l;
    }

    /**
     * 保存appSercet
     * 
     * @param appKey
     * @param appSercet
     * @return
     */
    public boolean saveMebAccountAppSecret(String appKey, String appSercet) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":appkey" + ":" + appKey;
            redisTemplate.opsForValue().set(key, appSercet);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveMebAccountAppSecret", e);
        }
        return false;
    }

    /**
     * 通过appKey查询
     * 
     * @param appKey
     * @return appSercet
     */
    public String getMebAccountAppSecret(String appKey) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":appkey" + ":" + appKey;
            Object obj = redisTemplate.opsForValue().get(key);
            return obj == null ? null : (String) obj;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("getMebAccountAppSecret", e);
        }
        return null;
    }

    /**
     * 通过appKey查询
     * 
     * @param appKey
     * @return
     */
    public boolean delMebAccountAppSecret(String appKey) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":appkey" + ":" + appKey;
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("delMebAccountAppSecret", e);
        }
        return false;
    }

    /**
     * 缓存活动账号
     * 
     * @param keymark
     * @param abcAppsercet
     * @return
     */
    public boolean saveMebAccount(String keymark, MemberAccountKeyInstance mebAutKey) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":keymark" + ":" + keymark;
            redisTemplate.opsForValue().set(key, mebAutKey);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveMebAccount", e);
        }
        return false;
    }

    /**
     * 通过keymark查询
     * 
     * @param keymark
     * @return
     */
    public MemberAccountKeyInstance getMebAccount(String keymark) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":keymark" + ":" + keymark;
            Object obj = redisTemplate.opsForValue().get(key);
            return obj == null ? null : (MemberAccountKeyInstance) obj;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("getMebAccount", e);
        }
        return null;
    }

    /**
     * 通过keymark删除
     * 
     * @param keymark
     * @return
     */
    public boolean delMebAccount(String keymark) {
        try {
            String key = RedisKeyConstant.ACCOUNT_MEB + ":keymark" + ":" + keymark;
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("delMebAccount", e);
        }
        return false;
    }

    /**
     * 自增登入次数（一个小时）
     * 
     * @param telephone
     * @return
     */
    public Long saveLoginCount(String telephone) {
        try {
            int expTime = 60 * 60;
            String key = RedisKeyConstant.LOGIN_COUNT + ":" + telephone;
            Long count = redisTemplate.opsForValue().increment(key, 1);
            Long times = redisTemplate.getExpire(key);
            if (times <= 0) {
                redisTemplate.expire(key, expTime, TimeUnit.SECONDS);
            }
            return count;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("saveLoginCount", e);
        }
        return 0l;
    }

    /**
     * 自减登入次数
     * 
     * @param telephone
     * @return
     */
    public Long reduLoginCount(String telephone) {
        try {
            String key = RedisKeyConstant.LOGIN_COUNT + ":" + telephone;
            Long count = redisTemplate.opsForValue().increment(key, -1);
            if (count <= 0) {
                redisTemplate.delete(key);
            }
            return count;
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("reduLoginCount", e);
        }
        return 0l;
    }

}
