package com.rzsd.wechat.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "/*", filterName = "timeParseFilter")
public class TimeParseFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(TimeParseFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String url = ((HttpServletRequest) request).getRequestURI();
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        if (url.startsWith("/js/") || url.startsWith("/css/")) {
            return;
        }
        logger.info(url + " ParseTime:" + (endTime - startTime) + "ms.");
    }

    @Override
    public void destroy() {
    }

}
