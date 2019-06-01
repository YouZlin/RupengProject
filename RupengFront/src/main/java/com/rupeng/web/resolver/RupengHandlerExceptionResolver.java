package com.rupeng.web.resolver;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.util.AjaxResult;
import com.rupeng.util.JsonUtils;

@Component
public class RupengHandlerExceptionResolver implements HandlerExceptionResolver {
    private static final Logger logger = LogManager.getLogger(RupengHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {

        logger.error("服务器出错", ex);

        //如果是ajax请求，就返回一个json格式的出错提示信息
        if (request.getHeader("X-Requested-With") != null) {
            try {
                response.getWriter().println(JsonUtils.toJson(AjaxResult.errorInstance("服务器出错了")));
            } catch (IOException e) {
                logger.error("服务器失败时发送错误提示信息失败", e);
            }
            //返回一个空的ModelAndView表示已经手动生成响应
            //return null表示使用默认的处理方式，等于没处理
            return new ModelAndView();
        } else {
            return new ModelAndView("500");
        }
    }
}
