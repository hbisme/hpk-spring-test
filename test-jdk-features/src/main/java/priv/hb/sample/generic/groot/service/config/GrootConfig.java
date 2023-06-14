package priv.hb.sample.generic.groot.service.config;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年11月23日 09:30
 */
@Data
public class GrootConfig<T> {
    private String appName;
    // private String ak;
    // private String sk;
    // private String token;

    // private Class<T> clazz ;
    private ClientConstruction<T> clientConstruction;

    public GrootConfig(String appName, Class<T> clazz, ClientConstruction<T> clientConstruction) {
        this.appName = appName;
        // this.clazz = clazz;
        this.clientConstruction = clientConstruction;
    }


    public GrootConfig(String appName, ClientConstruction<T> clientConstruction) {
        this.appName = appName;
        this.clientConstruction = clientConstruction;
    }







}
