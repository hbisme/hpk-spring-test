package priv.hb.sample.generic.groot.service.config;

import jdk.nashorn.internal.objects.annotations.Function;

/**
 * @author hubin
 * @date 2022年11月23日 09:33
 */
public interface ClientConstruction<T> {
    @Function
    T create(String ak, String sk, String token);
}
