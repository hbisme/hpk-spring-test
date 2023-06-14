package priv.hb.sample.generic.groot.service;

import java.util.HashMap;

import priv.hb.sample.generic.groot.service.config.AliSTSConfig;

/**
 * @author hubin
 * @date 2022年11月23日 09:45
 */
public class Common {
    private static HashMap<String, AliSTSConfig> stsMap = new HashMap<>();

    static {
        stsMap.put("app1", new AliSTSConfig("ak1", "sk1", "token1"));
        stsMap.put("app2", new AliSTSConfig("ak2", "sk2", "token2"));
    }


    public static AliSTSConfig getSts(String appName) {
        return stsMap.get(appName);
    }
}
