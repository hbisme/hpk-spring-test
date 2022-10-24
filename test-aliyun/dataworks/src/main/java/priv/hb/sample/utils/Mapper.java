package priv.hb.sample.utils;

import com.aliyun.dataworks_public20200518.models.ListFilesResponseBody;

import priv.hb.sample.argument.ListFilesOutputArgs;

/**
 * @author hubin
 * @date 2022年08月10日 13:52
 */

@org.mapstruct.Mapper
public interface Mapper {
    ListFilesOutputArgs responseToOutput(ListFilesResponseBody.ListFilesResponseBodyDataFiles listFilesResponseBodyDataFiles);
}
