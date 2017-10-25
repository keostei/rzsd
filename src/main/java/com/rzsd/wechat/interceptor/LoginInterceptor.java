package com.rzsd.wechat.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.entity.LoginUser;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            return true;
        }
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz.isAnnotationPresent(WebAuth.class) || method.isAnnotationPresent(WebAuth.class)) {
            boolean isAuthed = true;
            if (loginUser == null) {
                isAuthed = false;
            } else {
                WebAuth webAuth = method.getAnnotation(WebAuth.class);
                if (webAuth != null && webAuth.lever().length > 0
                        && !Arrays.asList(webAuth.lever()).contains(loginUser.getUserType())) {
                    isAuthed = false;
                }
            }
            if (!isAuthed) {
                request.getSession().removeAttribute("LOGIN_USER");
                boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
                if (isAjax) {
                    String jsonObject = "{\"success\":false,\"isLoginRequired\":true}";
                    String contentType = "application/json";
                    response.setContentType(contentType);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    PrintWriter out = response.getWriter();
                    out.print(jsonObject);
                    out.flush();
                    out.close();
                } else {
                    response.sendRedirect("/login");
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

}
