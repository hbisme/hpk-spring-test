package priv.hb.sample.controller;

import priv.hb.sample.dto.TestDTO;
import priv.hb.sample.service.TestService;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author hubin
 * @date 2022年08月10日 16:09
 */
@RestController
public class TestController {

    private TestService testService;

    @PostMapping("/test")
    public Double test(@RequestBody TestDTO testDTO) {
        try {
            Double result = this.testService.service(testDTO);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 统一异常拦截测试
     *
     */
    @GetMapping("/test2")
    public Double test(@RequestParam Integer num) {
        try {
            TestDTO testDTO = new TestDTO();
            testDTO.setNum(num);
            testDTO.setType("square");
            Double result = this.testService.service(testDTO);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 参数校验测试1
     * @param num
     * @return
     */
    @GetMapping("/{num}")
    public Integer detail(@Valid @PathVariable("num") @Min(1) @Max(20) Integer num) {
        return num * num;
    }

    /**
     * 参数校验测试2
     * @param email
     * @return
     */
    @GetMapping("/getByEmail")
    public TestDTO getByAccount(@RequestParam @NotBlank @Email String email) {
        TestDTO testDTO = new TestDTO();
        testDTO.setEmail(email);
        return testDTO;
    }

    /**
     * 参数校验测试3
     * @param testDTO
     */
    @PostMapping("/test-validation")
    public void testValidation(@RequestBody @Validated TestDTO testDTO) throws Exception {
        this.testService.service(testDTO);
    }




    @Autowired
    public void setTestService(TestService testService) {
        this.testService = testService;
    }
}
