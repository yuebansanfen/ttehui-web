package com.mocentre.tehui.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlparser.lexer.Page;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mocentre.tehui.core.model.BaseEntity;
import com.mocentre.tehui.core.utils.json.BaseProcessor;
import com.mocentre.tehui.core.utils.json.DateJsonValueProcessor;
import com.mocentre.tehui.core.utils.json.SqlDateProcessor;

/**
 * Json工具类.
 */
public class JsonUtils {

    /**
     * 日志记录器
     */
    private static final Logger log           = Logger.getLogger(JsonUtils.class);

    /**
     * java对象和JSON结构的转换类
     */
    static final ObjectMapper   OBJECT_MAPPER = new ObjectMapper();

    protected JsonUtils() {
    }

    protected static String[] EXCLUDES = { "start", "limit", "sort", "dir", "data" };

    // ----------------------------------------------------------------- BEAN 2 Json

    public static JSONObject bean2JSONObject(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        return JSONObject.fromObject(o, jsonConfig);
    }

    public static JSONObject bean2JSONObject(Object o, String dateFormat) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        if (!StringUtils.isBlank(dateFormat)) {
            jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(dateFormat));
        } else {
            jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        }
        return JSONObject.fromObject(o, jsonConfig);
    }

    /**
     * 对象转换成Json字符串
     * 
     * @param bean 将转换为Json字符串的对象
     */
    public static String bean2Json(Object o) {
        return bean2Json(o, EXCLUDES);
    }

    public static String bean2Json(Object o, String dateFormat) {
        return bean2Json(o, EXCLUDES, dateFormat);
    }

    public static String bean2Json(Object o, Integer count) {
        return appendResult(o, count, EXCLUDES);
    }

    /**
     * Page对象转换成Json字符串
     * 
     * @param page 将转换为Json字符串的Page对象
     */
    public static String bean2Json(Page page) {
        //System.out.println(bean2Json(page, EXCLUDES));
        return bean2Json(page, EXCLUDES);
    }

    /**
     * Page对象转换成Json字符串,带自定义日期转换格式
     * 
     * @param page page对象
     * @param dateFormat 日期格式
     * @return
     */
    public static String bean2Json(Page page, String dateFormat) {
        return bean2Json(page, EXCLUDES, dateFormat);
    }

    /**
     * 对象转换成Json字符串
     * 
     * @param bean 将转换为Json字符串的对象，不可为<code>null<code>
     * @param excludes 不转换的属性数组
     */
    public static String bean2Json(Object o, String[] excludes) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(excludes);
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        String jsonString = JSONObject.fromObject(o, jsonConfig).toString();
        jsonString = jsonString.replace("result", "rows");
        return jsonString;
    }

    /**
     * 对象转换成Json字符串
     * 
     * @param bean 将转换为Json字符串的对象，不可为<code>null<code>
     * @param excludes 不转换的属性数组
     */
    public static String bean2JsonArray(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        String jsonString = JSONArray.fromObject(o, jsonConfig).toString();

        return jsonString;
    }

    /**
     * 对象转换成Json字符串
     * 
     * @param bean 将转换为Json字符串的对象，不可为<code>null<code>
     * @param excludes 不转换的属性数组
     */
    public static String bean2JsonArray(Object o, String dateFormat) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(dateFormat));
        String jsonString = JSONArray.fromObject(o, jsonConfig).toString();

        return jsonString;
    }

    /**
     * 自定义日期格式转换
     * 
     * @param o
     * @param excludes
     * @param dateFormat
     * @return
     */
    public static String bean2Json(Object o, String[] excludes, String dateFormat) {
        if (o == null) {
            throw new IllegalArgumentException("object is null while write the Json string...");
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(excludes);
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new SqlDateProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Integer.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(java.lang.Long.class, new BaseProcessor());
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(dateFormat));
        jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor(dateFormat));
        String jsonString = JSONArray.fromObject(o, jsonConfig).toString();

        // 默认输出的格式如下：[{"result":[{"id":0},{"id":1}],"totalCount":2}]
        // Ext.data.JsonReader支持的格式则为：{"result":[{"id":0},{"id":1}],"totalCount":2}
        // 去掉JsonArray自动生成最前面的"["和最后的"]"，暂时没找到对应的API处理:)
        jsonString = jsonString.substring(1, jsonString.length());
        jsonString = jsonString.substring(0, jsonString.length() - 1);

        return jsonString;
    }

    public static String appendResult(Object o, Integer count, String[] excludes) {
        String jsonString = bean2Json(o, excludes);
        jsonString = "{\"results\":" + count + ",\"rows\":[" + jsonString + "]}";
        return jsonString;
    }

    // ----------------------------------------------------------------- Json 2 BEAN
    /**
     * 根据json数据创建指定的POJO.
     * 
     * @param jsonData json数据字符串，如：{"id":"1"}
     * @param clazz 需要转换成bean的具体类型
     * @return T
     */
    public static <T extends BaseEntity> T json2Bean(String jsonData, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(jsonData)) {
            return clazz.newInstance();
        }

        JSONObject jsonObject = JSONObject.fromObject(jsonData);
        return json2Bean(jsonObject, clazz);
    }

    /**
     * 根据json数据创建指定的POJO List.
     * 
     * @param jsonData json数据字符串，如：{"id":"1"}
     * @param clazz 需要转换成bean的具体类型
     * @return List<T>
     */
    public static <T extends BaseEntity> List<T> json2List(String jsonData, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(jsonData)) {
            return new ArrayList<T>(0);
        }

        JSONArray jsonArray = JSONArray.fromObject(jsonData);
        List<T> list = new ArrayList<T>(jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(json2Bean(jsonObject, clazz));
        }

        return list;
    }

    /**
     * **********************************************
     * 
     * @method：getJsonCombox
     * @param map 根据下拉类型获取BaseConfigObj中定义的Map
     * @return 前台JsonReader所需的数据形式,如：{'totalCount':2,'rows':[{ 'id': 1, 'text':
     *         'Bill'},{'id':2,'text':'Ben'}]}
     * @throws Exception
     * @description：根据后台定义组装前台JSON下拉列表数据
     * @create:Oct 27, 2008-5:42:51 PM huangrh
     */
    public static String getJsonCombox(Map<String, String> map) {
        StringBuffer strBuffer = new StringBuffer();
        if (map.isEmpty()) {
            strBuffer.append("{\"result\":[],").append("\"totalCount\":0}");
            return strBuffer.toString();
        }

        strBuffer.append("{\"result\":[");
        //strBuffer.append("{\"id\":\"\",\"text\":\" - 请选择 - \"},");

        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            String propertyName = entry.getKey().toString();
            String propertyValue = entry.getValue().toString();
            strBuffer.append("{\"id\":\"" + propertyName + "\",\"text\":\"" + propertyValue + "\"},");
        }
        String buffer = strBuffer.substring(0, strBuffer.length() - 1);
        buffer = buffer + "],\"totalCount\":" + map.size() + "}";
        return buffer;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseEntity> T json2Bean(JSONObject jsonObject, Class<T> clazz) throws Exception {
        T entity = clazz.newInstance();

        for (Object object : jsonObject.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            String propertyName = entry.getKey().toString();
            String propertyValue = entry.getValue().toString();
            BeanUtils.setProperty(entity, propertyName, propertyValue);
        }

        return entity;
    }

    /**
     * JSON串转换Map
     *
     * @param jsonString JSON字符串
     * @return Map
     */
    public static Map<String, Object> json2Map(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = OBJECT_MAPPER.readValue(jsonString, Map.class);
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * JSON串转换为Java泛型对象，可以是各种类型。
     * 
     * @param <T> java对象的类型
     * @param jsonString JSON字符串
     * @param tr TypeReference,例如: new TypeReference< List<FamousUser> >(){}
     * @return List对象列表
     */
    public static <T> T jsonToGenericObject(String jsonString, TypeReference<T> tr) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        } else {
            try {
                return OBJECT_MAPPER.readValue(jsonString, tr);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
