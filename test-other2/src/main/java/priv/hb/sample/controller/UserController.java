package priv.hb.sample.controller;

import priv.hb.sample.dto.UserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * @author hubin
 * @date 2022年03月09日 10:10 上午
 */

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/get")
    public void get(@RequestParam("id") @Min(value = 1L, message = "编号必须大于1") Integer id) {
        logger.info("[get22][id: {}]", id);
    }


    @PostMapping("/add")
    public void add(@Valid UserDto userDto) {
        logger.info("[add][user: {}]", userDto);
    }


}
