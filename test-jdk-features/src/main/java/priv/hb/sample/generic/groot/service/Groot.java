package priv.hb.sample.generic.groot.service;


import java.lang.reflect.Type;

import priv.hb.sample.generic.groot.service.config.AliSTSConfig;
import priv.hb.sample.generic.groot.service.config.GrootConfig;

/**
 * @author hubin
 * @date 2022年11月23日 09:38
 */
public class Groot<T> {
    private GrootConfig<T> grootConfig;



    public Groot(GrootConfig<T> grootConfig) {
        this.grootConfig = grootConfig;
    }

    public T getClient() {
        AliSTSConfig sts = Common.getSts(grootConfig.getAppName());
        // Class clazz = grootConfig.getClazz();

        T client = grootConfig.getClientConstruction().create(sts.getAk(), sts.getSk(), sts.getToken());

        return client;
    }




}
