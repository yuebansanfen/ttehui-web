package com.mocentre.tehui.common.ip;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 类LogFactory.java的实现描述：纯真ip查询日志记录 </p>
 * 
 * @author sz.gong 2016年3月13日 下午5:25:58
 */
public class LogFactory {

    private static final Logger logger;
    static {
        logger = Logger.getLogger("stdout");
        logger.setLevel(Level.INFO);
    }

    public static void log(String info, Level level, Throwable ex) {
        logger.log(level, info, ex);
    }

    public static Level getLogLevel() {
        return logger.getLevel();
    }

}
