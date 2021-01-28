package com.gls.carwashapp.common;

import com.gls.carwashapp.model.Config;

public class PosConfigSingleTon {

    private static Config config = null;

    public static Config getInstance(){
        if (config == null){
            config = new Config();
        }
        return config;
    }

}
