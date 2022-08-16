import com.aliyun.dataworks_public20200518.Client;
import com.hb.utils.Common;
import com.hb.api.CreateFile;
import com.hb.api.DeleteFile;
import com.hb.api.DeployFile;
import com.hb.api.ListFiles;
import com.hb.api.SubmitFile;
import com.hb.api.UpdateFile;
import com.hb.argument.CreateFileInputArgs;
import com.hb.argument.DeployFileInputArgs;
import com.hb.argument.ListFilesInputArgs;
import com.hb.argument.ListFilesOutputArgs;
import com.hb.argument.SubmitFileInputArgs;
import com.hb.argument.UpdateFileInputArgs;

import org.junit.Test;

import java.util.List;
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
        Client client = Common.createCrmClient();
        CreateFileInputArgs createFileInputArgs = new CreateFileInputArgs("test_file_6", "业务流程/hb/MaxCompute", 48843L, "ybtest_ytdw_default_root", "select 123 \n  select 123");
        // CreateFile.createFile(createFileInputArgs);
        Map<String, Object> map = Common.toMap(createFileInputArgs);

        Long file2 = CreateFile.createFile(map, client);
        System.out.println(file2);
    }


    @Test
    public void testSubmitFile() throws Exception {
        Client client = Common.createCrmClient();
        SubmitFileInputArgs submitFileInputArgs = new SubmitFileInputArgs(505965470L, 48843L);
        Map<String, Object> map = Common.toMap(submitFileInputArgs);
        Long id = SubmitFile.submitFile(map, client);
        System.out.println(id);
    }


    @Test
    public void testDeploymentFile() throws Exception {
        Client client = Common.createCrmClient();
        DeployFileInputArgs deployFileInputArgs = new DeployFileInputArgs(505965470L, 48843L);
        Map<String, Object> map = Common.toMap(deployFileInputArgs);
        Long id = DeployFile.deployFile(map, client);
        System.out.println(id);
    }


    @Test
    public void testDeleteFile() throws Exception {
        Client client = Common.createCrmClient();
        DeployFileInputArgs deployFileInputArgs = new DeployFileInputArgs(505961665L, 48843L);
        Map<String, Object> map = Common.toMap(deployFileInputArgs);
        boolean res = DeleteFile.deleteFile(map, client);
        System.out.println(res);
    }


    @Test
    public void testUpdateFile() throws Exception {
        Client client = Common.createCrmClient();
        UpdateFileInputArgs updateFileInputArgs = new UpdateFileInputArgs(505961472L, 48843L, 1);
        updateFileInputArgs.setContent("select 20220810");
        Map<String, Object> map = Common.toMap(updateFileInputArgs);
        boolean res = UpdateFile.updateFile(map, client);
        System.out.println(res);
    }


    @Test
    public void testListFile() throws Exception {
        Client client = Common.createCrmClient();
        ListFilesInputArgs listFilesInputArgs = new ListFilesInputArgs(48843L);
        listFilesInputArgs.setKeyword("test");
        Map<String, Object> map = Common.toMap(listFilesInputArgs);
        List<ListFilesOutputArgs> listFilesOutputArgs = ListFiles.listFiles(map, client);
        System.out.println(listFilesOutputArgs);
    }

}
