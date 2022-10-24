package priv.hb.sample.service.impl.arthas;

import priv.hb.sample.service.arthas.ArthasTestService;

import org.springframework.stereotype.Service;

/**
 * @author hubin
 * @date 2022年10月20日 13:55
 */
@Service
public class ArthasTestServiceImpl implements ArthasTestService {
    @Override
    public String getMyString(String input) {
        String result = doubleString(input);
        return "result is: " + result;
    }

    @Override
    public String doubleString(String input) {
        return input + ":" + input;
    }
}
