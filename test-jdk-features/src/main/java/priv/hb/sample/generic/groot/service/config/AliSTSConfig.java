package priv.hb.sample.generic.groot.service.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hubin
 * @date 2022年11月23日 09:40
 */
@Data
@AllArgsConstructor
public class AliSTSConfig {
    private String ak;
    private String sk;
    private String token;
}
