import com.hb.api.Common;
import com.hb.api.CreateFile;
import com.hb.api.DeployFile;
import com.hb.api.SubmitFile;
import com.hb.argument.CreateFileInputArgs;
import com.hb.argument.DeployFileInputArgs;
import com.hb.argument.SubmitFileInputArgs;

import org.junit.Test;

import java.util.Map;

/**
 * @author hubin
 * @date 2022年08月09日 13:52
 */
public class Test1 {

    @Test
    public void test1() {
        System.out.println("hello");
    }

    @Test
    public void testCreateFile() throws Exception {
        CreateFileInputArgs createFileInputArgs = new CreateFileInputArgs("test_file_6", "业务流程/hb/MaxCompute", 48843L, "ybtest_ytdw_default_root", "select 123 \n  select 123");
        // CreateFile.createFile(createFileInputArgs);
        Map<String, Object> map = Common.toMap(createFileInputArgs);
        // System.out.println();


        Long file2 = CreateFile.createFile(map);
        System.out.println(file2);
    }

    @Test
    public void testSubmitFile() throws Exception {
        SubmitFileInputArgs submitFileInputArgs = new SubmitFileInputArgs(505965470L, 48843L);
        Map<String, Object> map = Common.toMap(submitFileInputArgs);
        Long id = SubmitFile.submitFile(map);
        System.out.println(id);
    }



    @Test
    public void testDeploymentFile() throws Exception {
        DeployFileInputArgs deployFileInputArgs = new DeployFileInputArgs(505965470L, 48843L);
        Map<String, Object> map = Common.toMap(deployFileInputArgs);
        Long id = DeployFile.deployFile(map);
        System.out.println(id);
    }


}
