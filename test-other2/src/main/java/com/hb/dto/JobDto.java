package com.hb.dto;

import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hubin
 * @date 2022年03月10日 2:15 下午
 */
@Getter
@Setter
@NoArgsConstructor
public class JobDto {
    private Integer id;
    private String name2;
    private Date time;
}
