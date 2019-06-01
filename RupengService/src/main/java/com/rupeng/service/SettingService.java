package com.rupeng.service;

import org.springframework.stereotype.Service;

import com.rupeng.annotation.RupengCacheable;
import com.rupeng.annotation.RupengUseCache;
import com.rupeng.pojo.Setting;

@Service
@RupengCacheable
public class SettingService extends BaseService<Setting> {

    @RupengUseCache
    public Setting selectOneByName(String name) {
        Setting params = new Setting();
        params.setName(name);
        return selectOne(params);
    }

}
