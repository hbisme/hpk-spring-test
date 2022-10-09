package com.hb.imports;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author hubin
 * @date 2022年09月27日 16:42
 */
@Configuration
@Import({MyImportSelector.class})
public class MyConfigure {
}
