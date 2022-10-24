package priv.hb.sample.service;

import org.springframework.stereotype.Service;


@Service
public class ArthasDebug {
    public String test1(String input) {
        if (!input.equals("empty")) {
            String str = test11(input);
            return str + " ok2";
        } else {
            return null;
        }
    }


    public String test11(String input) {
        System.out.println(input);
        return input+","+input;
    }
}
