import com.aliyuncs.CommonResponse;
import com.dezheng.aliyun.AliyunSms;

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.0.3</version>
</dependency>
*/
class SendSms {
    public static void main(String[] args) {
        AliyunSms aliyunSms = new AliyunSms();
        CommonResponse response = aliyunSms.sendSms("18024088480", "1234");
        System.out.println(response.getData());
    }
}