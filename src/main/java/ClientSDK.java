import com.demo.RestClient;
import org.springframework.cloud.client.ServiceInstance;

public interface ClientSDK {
    public String getServiceName();

    public ServiceInstance getServiceInstance();

    public RestClient getRestClient();

    public String getUri();

    public boolean usingDiscoveryClient();
}
