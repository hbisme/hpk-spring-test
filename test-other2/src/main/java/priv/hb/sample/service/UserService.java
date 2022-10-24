package priv.hb.sample.service;

import priv.hb.sample.dto.UserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * @author hubin
 * @date 2022年03月09日 10:36 上午
 */
@Service
@Validated
public class UserService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void get(@Min(value = 1L, message = "编号必须大于0") Integer id){
        logger.info("[get][id: {}]", id);
    }

    public void add(@Valid UserDto addDTO) {
        logger.info("[add][addDTO: {}]", addDTO);
    }

    // 如果这样间接调用,是不会进行校验的.
    public void add1(UserDto userDto) {
        this.add(userDto);
    }



    @PostConstruct
    public void init() {
        logger.info("UserService 初始化..");
    }


    @PreDestroy
    public void  destroy() {
        logger.info("UserService 进行 destroy..");
    }

}
