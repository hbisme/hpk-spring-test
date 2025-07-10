package com.hb.example1.ddd;

import lombok.Value;

/**
 * @author hubin
 * @date 2024年04月28日 17:35
 */
@Value
public class Vector {
    public static final Vector ZERO = new Vector(0, 0);
    long x;
    long y;
}