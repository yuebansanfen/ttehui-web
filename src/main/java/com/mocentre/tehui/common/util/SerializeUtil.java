/*
 * Copyright 2016 gaoshou360.com All right reserved. This software is the
 * confidential and proprietary information of gaoshou360.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with gaoshou360.com .
 */
package com.mocentre.tehui.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * 类SerializeUtil.java的实现描述：序列化工具类，来提供对象的序列化和饭序列化的工作
 * 
 * @author sz.gong 2016年3月22日 下午5:07:00
 */
public class SerializeUtil {

    private static final Logger logger = Logger.getLogger(SerializeUtil.class);

    /**
     * 序列化
     * 
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] result = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            result = baos.toByteArray();
        } catch (IOException e) {
            logger.error("Non-serializable object", e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.error("error message", e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.error("error message", e);
                }
            }
        }
        return result;
    }

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        Object result = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            result = ois.readObject();
        } catch (Exception e) {
            logger.error("Non-serializable object", e);
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    logger.error("error message", e);
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    logger.error("error message", e);
                }
            }
        }
        return result;
    }

}
