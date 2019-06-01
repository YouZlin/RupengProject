package com.rupeng.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Setting;
import com.rupeng.pojo.User;
import com.rupeng.service.SettingService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.EmailUtils;
import com.rupeng.util.ImageCodeUtils;
import com.rupeng.util.JedisUtils;
import com.rupeng.util.JsonUtils;
import com.rupeng.util.SMSUtils;
import com.rupeng.util.UploadUtils;

@Controller
@MultipartConfig
public class OtherController {

    @Autowired
    private SettingService settingService;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping("/imageCode.do")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) {
        ImageCodeUtils.sendImageCode(request.getSession(), response);
    }

    @RequestMapping("/smsCode.do")
    public @ResponseBody AjaxResult smsCode(String phone, HttpServletRequest request, HttpServletResponse response) {

        if (CommonUtils.isEmpty(phone)) {
            return AjaxResult.errorInstance("手机号不能为空");
        }

        if (!CommonUtils.isPhone(phone)) {
            return AjaxResult.errorInstance("您输入的手机号不符合手机号格式");
        }

        Setting url = settingService.selectOneByName("sms_url");
        Setting username = settingService.selectOneByName("sms_username");
        Setting appKey = settingService.selectOneByName("sms_app_key");
        Setting templateId = settingService.selectOneByName("sms_code_template");

        SMSUtils.sendSMSCode(request.getSession(), url.getValue(), username.getValue(), appKey.getValue(), templateId.getValue(), phone);

        return AjaxResult.successInstance("发送成功");
    }

    @RequestMapping("/emailCode.do")
    public @ResponseBody AjaxResult emailCode(String email, HttpServletRequest request, HttpServletResponse response) {

        if (CommonUtils.isEmpty(email)) {
            return AjaxResult.errorInstance("邮箱不能为空");
        }

        if (!CommonUtils.isEmail(email)) {
            return AjaxResult.errorInstance("您输入的邮箱不符合邮箱格式");
        }
        /*
        Setting smtpHost = settingService.selectOneByName("email_smtp_host");
        Setting username = settingService.selectOneByName("email_username");
        Setting password = settingService.selectOneByName("email_password");
        Setting from = settingService.selectOneByName("email_from");
        
        EmailUtils.sendEmailCode(request.getSession(), smtpHost.getValue(), username.getValue(), password.getValue(), from.getValue(), email);
        */
        Setting url = settingService.selectOneByName("sendcloud_email_url");
        Setting apiUser = settingService.selectOneByName("sendcloud_email_api_user");
        Setting apiKey = settingService.selectOneByName("sendcloud_email_api_key");
        Setting from = settingService.selectOneByName("sendcloud_email_from");
        Setting templateInvokeName = settingService.selectOneByName("sendcloud_email_code_template");

        //使用sendcloud发送邮件验证码
        EmailUtils.sendEmailCodeWithSendCloud(request.getSession(), url.getValue(), apiUser.getValue(), apiKey.getValue(), from.getValue(), email, templateInvokeName.getValue());

        return AjaxResult.successInstance("发送成功");
    }

    @RequestMapping("/notification.do")
    public @ResponseBody AjaxResult notificationList(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return AjaxResult.errorInstance("您还没有登录");
        }

        //获得当前登录用户所有的通知消息，并且把通知消息从redis里面删除掉
        Set<String> datas = JedisUtils.smembersAndDel("notification_" + user.getId());

        if (CommonUtils.isEmpty(datas)) {
            return AjaxResult.successInstance(null);
        }

        List<Object> list = new ArrayList<Object>();

        for (String string : datas) {
            Object obj = JsonUtils.toBean(string, Object.class);
            list.add(obj);
        }

        return AjaxResult.successInstance(list);

    }

    @RequestMapping(value = "/upload.do", method = RequestMethod.GET)
    public ModelAndView uploadConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/lib/ueditor/jsp/config.json").forward(request, response);

        return null;
    }

    //处理文件上传请求
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> uploadSubmit(MultipartFile upfile, HttpServletRequest request) throws Exception {

        //创建目标文件
        String extension = UploadUtils.getExtension(upfile.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + extension;

        File parentDir = new File(request.getServletContext().getRealPath("/upload"));
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        File file = new File(parentDir, filename);

        upfile.transferTo(file);

        Setting bucket = settingService.selectOneByName("qiniu_upload_bucket");
        Setting accessKey = settingService.selectOneByName("qiniu_upload_accessKey");
        Setting secretKey = settingService.selectOneByName("qiniu_upload_secretKey");

        UploadUtils.uploadWithQiniu(file, accessKey.getValue(), secretKey.getValue(), bucket.getValue());

        file.delete();

        Map<String, String> result = new HashMap<String, String>();
        result.put("state", "SUCCESS");
        result.put("original", upfile.getOriginalFilename());
        result.put("title", filename);
        result.put("type", extension);
        result.put("url", "/" + filename);

        return result;
    }

}
