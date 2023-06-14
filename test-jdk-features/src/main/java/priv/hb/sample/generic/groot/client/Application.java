package priv.hb.sample.generic.groot.client;


import priv.hb.sample.generic.groot.aliClient.AliClient1;
import priv.hb.sample.generic.groot.aliClient.AliClient2;
import priv.hb.sample.generic.groot.service.Groot;
import priv.hb.sample.generic.groot.service.config.ClientConstruction;
import priv.hb.sample.generic.groot.service.config.GrootConfig;

/**
 * @author hubin
 * @date 2022年11月23日 09:29
 */
public class Application {
    public static void main(String[] args) {

        ClientConstruction aliClientConstruction1 = (String ak, String sk, String token) -> {
            AliClient1 aliClient1 = new AliClient1(ak, sk, token);
            return aliClient1;
        };

        GrootConfig<AliClient1> grootConfig = new GrootConfig<AliClient1>("app1", aliClientConstruction1);

        Groot<AliClient1> groot1 = new Groot(grootConfig);

        AliClient1 client1 = groot1.getClient();

        client1.echo();

        //////////////////


        String endpoint = "endpoint2";
        ClientConstruction aliClientConstruction2 = (String ak, String sk, String token) -> {
            AliClient2 aliClient2 = new AliClient2(ak, sk, token, endpoint);
            return aliClient2;
        };

        GrootConfig grootConfig2 = new GrootConfig("app2", aliClientConstruction2);
        Groot<AliClient2> groot2 = new Groot(grootConfig2);
        AliClient2 client2 = groot2.getClient();
        client2.echo();





    }
}
