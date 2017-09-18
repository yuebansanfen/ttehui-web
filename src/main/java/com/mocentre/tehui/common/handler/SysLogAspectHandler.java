//package com.mocentre.tehui.common.handler;
//
//import java.util.Date;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import com.mocentre.tehui.backend.model.LoginUserInstance;
//import com.mocentre.tehui.common.constant.SessionKeyConstant;
//import com.mocentre.tehui.common.ip.IPSeeker;
//import com.mocentre.tehui.common.util.CommUtil;
//import com.mocentre.tehui.core.annotation.SysLog;
//
///**
// * 类SysLogAspectHandler.java的实现描述： </p>
// * 系统日志管理类，这里使用Spring环绕通知和异常通知进行动态管理,系统只记录管理员操作记录 ， 对访问列表不进行记录
// * 
// * @author sz.gong 2016年3月11日 下午4:06:37
// */
//@Component
//@Aspect
//public class SysLogAspectHandler {
//
//    //    @Autowired
//    //    private OperateLogService operLogService;
//    //   @Autowired
//    //    private OperateService operateService;
//
//    //配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
//    @Pointcut(value = "execution(* com.mocentre.*.*.controller.*.*(..))")
//    public void controllerAspect() {
//    }
//
//    @After("controllerAspect() && @annotation(log) && args(request,..)")
//    //@After(value = "execution(* com.mocentre.*.*.action.*.*(..)) && @annotation(log)&&args(request,..)")
//    public void admin_op_log(JoinPoint joinPoint, SysLog log, HttpServletRequest request) {
//        try {
//            this.saveLog(joinPoint, log, request);
//            //System.out.println(getControllerMethodDescription(joinPoint));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 异常日志记录
//    @AfterThrowing(value = "controllerAspect() && args(request,..)", throwing = "exception")
//    public void exceptionLog(HttpServletRequest request, Throwable exception) {
//        LoginUserInstance user = (LoginUserInstance) request.getSession().getAttribute(SessionKeyConstant.USER);
//        if (user != null) {
//            String current_ip = CommUtil.getIpAddr(request);
//            String ipCity = "未知地区";
//            if (CommUtil.isIp(current_ip)) {
//                IPSeeker ip = new IPSeeker(null, null);
//                ipCity = ip.getIPLocation(current_ip).getCountry();
//            }
//            //            OperateLog operLog = new OperateLog();
//            //            operLog.setOperator(user.getUserName());
//            //            operLog.setTitle("系统异常");
//            //            operLog.setType(1);
//            //            operLog.setIp(current_ip);
//            //            operLog.setIpCity(ipCity);
//            //            operLog.setContent(CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date()) + "执行" + request.getRequestURI() == null ? ""
//            //                    : request.getRequestURI() + "时出现异常，异常代码为:" + exception.getMessage());
//            //            operLogService.saveOperateLog(operLog);
//        }
//    }
//
//    private void saveLog(JoinPoint joinPoint, SysLog log, HttpServletRequest request) throws Exception {
//        LoginUserInstance user = (LoginUserInstance) request.getSession().getAttribute(SessionKeyConstant.USER);
//        System.out.println("进入了事务-------------------");
//        if (user != null) {
//            String userName = user.getUserName();
//            StringBuffer buf = new StringBuffer();
//            // 获取操作内容
//            String id = request.getParameter("id");
//            String mulitId = request.getParameter("ids");
//            buf.append(userName);
//            buf.append("(" + user.getId() + ")于");
//            buf.append(CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date()));
//            if (id != null || mulitId != null || log.value().indexOf("save") >= 0 || log.value().indexOf("add") >= 0
//                    || log.value().indexOf("edit") >= 0 || log.value().indexOf("update") >= 0
//                    || log.value().indexOf("delete") >= 0) {
//                String option1 = "执行";
//                String option2 = "操作";
//                buf.append(option1 + log.title() + option2);
//                if (mulitId != null & !"".equals(mulitId)) {
//                    buf.append("。操作数据id为：" + mulitId);
//                } else {
//                    if (id != null && !"".equals(id)) {
//                        buf.append("。操作数据id为：" + id);
//                    }
//                }
//                String current_ip = CommUtil.getIpAddr(request);
//                String ipCity = "未知地区";
//                if (CommUtil.isIp(current_ip)) {
//                    IPSeeker ip = new IPSeeker(null, null);
//                    ipCity = ip.getIPLocation(current_ip).getCountry();
//                }
//                //                OperateLog operLog = new OperateLog();
//                //                operLog.setOperator(userName);
//                //                operLog.setTitle(log.title());
//                //                operLog.setType(0);
//                //                operLog.setIp(current_ip);
//                //                operLog.setIpCity(ipCity);
//                //                operLog.setContent(buf.toString());
//                //                operLogService.saveOperateLog(operLog);
//            }
//
//        }
//    }
//
//    @After("controllerAspect() && @annotation(log) && args(request,..)")
//    //@After(value = "execution(* com.mocentre.*.*.action.*.*(..)) && @annotation(log)&&args(request,..)")
//    public void operateAspect(HttpServletRequest request, SysLog log) {
//        try {
//            System.out.println("进入了操作记录事务----------------");
//            LoginUserInstance user = (LoginUserInstance) request.getSession().getAttribute(SessionKeyConstant.USER);
//            //            Operation operation = new Operation();
//            //            operation.setOperator(user.getUserName());
//            //            operation.setTitle(log.title());
//            //            operateService.saveEntity(operation);
//            //System.out.println(getControllerMethodDescription(joinPoint));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
//    //        String targetName = joinPoint.getTarget().getClass().getName();
//    //        String methodName = joinPoint.getSignature().getName();
//    //        Object[] arguments = joinPoint.getArgs();
//    //        Class targetClass = Class.forName(targetName);
//    //        Method[] methods = targetClass.getMethods();
//    //        String description = "";
//    //        for (Method method : methods) {
//    //            if (method.getName().equals(methodName)) {
//    //                Class[] clazzs = method.getParameterTypes();
//    //                if (clazzs.length == arguments.length) {
//    //                    System.out.println(method.isAnnotationPresent(SysLog.class));
//    //                    description = method.getAnnotation(SysLog.class).description();
//    //                    break;
//    //                }
//    //            }
//    //        }
//    //        return description;
//    //    }
//
//}
