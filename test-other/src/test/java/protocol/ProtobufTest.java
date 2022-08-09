// package protocol;
//
//
// import com.google.protobuf.ByteString;
// import com.google.protobuf.InvalidProtocolBufferException;
// import com.google.protobuf.ProtocolStringList;
// import com.hb.protocol.JobExecuteKind;
// import com.hb.protocol.PersonEntity;
// import com.hb.protocol.ResponseStatus;
// import com.hb.protocol.RpcCancelMessage;
// import com.hb.protocol.RpcExecuteMessage;
// import com.hb.protocol.RpcHeartBeatMessage;
// import com.hb.protocol.RpcManualMessage;
// import com.hb.protocol.RpcOperate;
// import com.hb.protocol.RpcRequest;
// import com.hb.protocol.RpcResponse;
// import com.hb.protocol.RpcSocketMessage;
// import com.hb.protocol.RpcWebOperate;
// import com.hb.protocol.RpcWebRequest;
// import com.hb.protocol.RpcWebResponse;
// import com.hb.protocol.RpcWorkInfo;
// import com.sun.xml.internal.xsom.impl.scd.Iterators;
//
// import org.junit.Test;
//
// import java.util.ArrayList;
//
// public class ProtobufTest {
//
//     /**
//      * 普通的 Protobuf序列化反序列化类测试
//      *
//      * @throws InvalidProtocolBufferException
//      */
//     @Test
//     public void test1() throws InvalidProtocolBufferException {
//
//         // 使用生成的protobuf类来构造对象
//         PersonEntity.Person.Builder build = PersonEntity.Person.newBuilder();
//         build.setId(1);
//         build.setName("hb");
//         build.setEmail("343122422@163.com");
//         PersonEntity.Person person = build.build();
//         System.out.println("person: " + person);
//
//         // 将生成的对象,序列化成二进制数组
//         byte[] bytes = person.toByteArray();
//
//
//         // 将二进制数据反序列为protobuf对象
//         PersonEntity.Person person2 = PersonEntity.Person.parseFrom(bytes);
//         System.out.println(person2);
//     }
//
//
//     @Test
//     public void testEnum() {
//         JobExecuteKind.ExecuteKind[] values = JobExecuteKind.ExecuteKind.values();
//         for (JobExecuteKind.ExecuteKind executedKind : values) {
//             System.out.println(executedKind);
//         }
//     }
//
//     @Test
//     public void testRpcExecuteMessage() {
//         RpcExecuteMessage.ExecuteMessage.Builder build = RpcExecuteMessage.ExecuteMessage.newBuilder();
//         build.setActionId("100");
//         build.setExitCode(200);
//
//         RpcExecuteMessage.ExecuteMessage executeMessage = build.build();
//         System.out.println(executeMessage);
//     }
//
//     @Test
//     public void testRpcCancelMessage() {
//         RpcCancelMessage.CancelMessage.Builder build = RpcCancelMessage.CancelMessage.newBuilder();
//         build.setId("1");
//         build.setEk(JobExecuteKind.ExecuteKind.valueOf(1));
//         RpcCancelMessage.CancelMessage cancelMessage = build.build();
//         System.out.println(cancelMessage);
//     }
//
//
//     @Test
//     public void testListField() {
//         RpcHeartBeatMessage.HeartBeatMessage.Builder build = RpcHeartBeatMessage.HeartBeatMessage.newBuilder();
//
//         ArrayList<String> debugRunnings = new ArrayList<>();
//         debugRunnings.add("debugJob1");
//         debugRunnings.add("debugJob2");
//
//         ArrayList<String> manualRunnings = new ArrayList<>();
//         manualRunnings.add("manualJob1");
//         manualRunnings.add("manualJob2");
//
//         ArrayList<String> runnings = new ArrayList<>();
//         runnings.add("running-job1");
//         runnings.add("running-job2");
//
//
//         build.setHost("host1")
//                 .setMemTotal(1024)
//                 .setMemRate(0.6f)
//                 .setCpuLoadPerCore(0.2f)
//                 .setTimestamp(123456)
//                 .setCores(4)
//                 .addAllDebugRunnings(debugRunnings)
//                 .addAllManualRunnings(manualRunnings)
//                 .addAllRunnings(runnings);
//
//         RpcHeartBeatMessage.HeartBeatMessage hearBeatMessage = build.build();
//         System.out.println(hearBeatMessage);
//
//         ProtocolStringList runningsJob = hearBeatMessage.getRunningsList();
//         for (String job : runningsJob) {
//             System.out.println(job);
//
//         }
//     }
//
//
//     @Test
//     public void testRpcManualMessage() {
//         RpcManualMessage.ManualMessage.Builder build = RpcManualMessage.ManualMessage.newBuilder();
//         build.setHistoryId("100").setExitCode(200);
//         RpcManualMessage.ManualMessage manualMessage = build.build();
//         System.out.println(manualMessage);
//     }
//
//     @Test
//     public void testRequest() {
//         RpcRequest.Request.Builder build = RpcRequest.Request.newBuilder();
//         build.setRid(100)
//                 .setOperate(RpcOperate.Operate.HeartBeat)
//                 .setBody(ByteString.copyFrom("http body".getBytes()))
//         ;
//
//         RpcRequest.Request request = build.build();
//         System.out.println(request);
//     }
//
//     @Test
//     public void testResponse() {
//         RpcResponse.Response.Builder build = RpcResponse.Response.newBuilder();
//         build.setRid(100)
//                 .setStatusEnum(ResponseStatus.Status.ERROR)
//                 .setOperate(RpcOperate.Operate.Schedule)
//                 .setErrorText("调度失败")
//                 .setBody(ByteString.copyFrom("http body".getBytes()));
//
//         RpcResponse.Response response = build.build();
//
//         System.out.println(response);
//     }
//
//
//     @Test
//     public void testSocketMessage() {
//         RpcSocketMessage.SocketMessage.Builder build = RpcSocketMessage.SocketMessage.newBuilder();
//         build.setKind(RpcSocketMessage.SocketMessage.Kind.REQUEST)
//                 .setBody(ByteString.copyFrom("http body".getBytes()));
//
//         RpcSocketMessage.SocketMessage socketMessage = build.build();
//         System.out.println(socketMessage);
//
//     }
//
//     @Test
//     public void testWebRequest() {
//         RpcWebRequest.WebRequest.Builder build = RpcWebRequest.WebRequest.newBuilder();
//         build.setRid(101)
//                 .setId("1001")
//                 .setOperate(RpcWebOperate.WebOperate.UpdateJob)
//                 .setEkValue(JobExecuteKind.ExecuteKind.ScheduleKind_VALUE)
//                 .setExecutor("executor1")
//                 .setScript("script1")
//                 .setBody(ByteString.copyFrom("http body".getBytes()));
//
//         RpcWebRequest.WebRequest webRequest = build.build();
//
//         System.out.println(webRequest);
//     }
//
//     @Test
//     public void testWebReponse() {
//         RpcWebResponse.WebResponse.Builder build = RpcWebResponse.WebResponse.newBuilder();
//         build.setRid(101)
//                 .setStatus(ResponseStatus.Status.OK)
//                 .setOperate(RpcWebOperate.WebOperate.ExecuteJob)
//                 .setErrorText("")
//                 .setBody(ByteString.copyFrom("http body".getBytes()));
//
//         RpcWebResponse.WebResponse webResponse = build.build();
//         System.out.println(webResponse);
//     }
//
//
//     @Test
//     public void testWorkInfo() {
//         RpcWorkInfo.OSInfo.Builder osInfoBuild = RpcWorkInfo.OSInfo.newBuilder();
//         osInfoBuild.setCpu(1.1f)
//                 .setMem(1024)
//                 .setSwap(2048)
//                 .setUser(0.3f)
//                 .setSystem(0.2f);
//
//         RpcWorkInfo.OSInfo osInfo = osInfoBuild.build();
//         System.out.println(osInfo);
//
//         RpcWorkInfo.MachineInfo.Builder machineBuild = RpcWorkInfo.MachineInfo.newBuilder();
//         machineBuild
//                 .setFilesystem("system1")
//                 .setType("type1")
//                 .setSize("10240")
//                 .setUsed("1024")
//                 .setAvail("2049")
//                 .setUse("0.6")
//                 .setMountedOn("/var");
//
//         RpcWorkInfo.MachineInfo machineInfo = machineBuild.build();
//         System.out.println(machineInfo);
//
//
//         RpcWorkInfo.ProcessMonitor.Builder processMonitorBuild = RpcWorkInfo.ProcessMonitor.newBuilder();
//         processMonitorBuild
//                 .setPid("101")
//                 .setUser("hb")
//                 .setViri("10240")
//                 .setRes("8096")
//                 .setCpu("1.1")
//                 .setMem("4096")
//                 .setTime("90")
//                 .setCommand("pwd");
//
//         RpcWorkInfo.ProcessMonitor processMonitor = processMonitorBuild.build();
//         System.out.println(processMonitor);
//
//
//         ArrayList<RpcWorkInfo.MachineInfo> machineInfos = new ArrayList<RpcWorkInfo.MachineInfo>();
//         machineInfos.add(machineInfo);
//         ArrayList<RpcWorkInfo.ProcessMonitor> processMonitors = new ArrayList<RpcWorkInfo.ProcessMonitor>();
//         processMonitors.add(processMonitor);
//
//         System.out.println("--------");
//         RpcWorkInfo.WorkInfo.Builder workInfoBuild = RpcWorkInfo.WorkInfo.newBuilder();
//         workInfoBuild
//                 .setOSInfo(osInfo)
//                 .addAllMachineInfo(machineInfos)
//                 .addAllProcessMonitor(processMonitors);
//
//         RpcWorkInfo.WorkInfo workInfo = workInfoBuild.build();
//         System.out.println(workInfo);
//
//         System.out.println("2-------");
//         RpcWorkInfo.AllWorkInfo.Builder allWorkInfoBuild = RpcWorkInfo.AllWorkInfo.newBuilder();
//         allWorkInfoBuild.putValues("key1", workInfo);
//         allWorkInfoBuild.putValues("key2", workInfo);
//         RpcWorkInfo.AllWorkInfo allWorkInfo = allWorkInfoBuild.build();
//
//         System.out.println(allWorkInfo);
//     }
//
//
// }
