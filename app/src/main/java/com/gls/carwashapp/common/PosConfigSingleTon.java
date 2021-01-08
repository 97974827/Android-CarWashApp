package com.gls.carwashapp.common;

import com.gls.carwashapp.model.PosConfigVO;

public class PosConfigSingleTon {

    private static PosConfigVO config = null;

    public static PosConfigVO getInstance(){
        if (config == null){
            config = new PosConfigVO();
        }
        return config;
    }

}
