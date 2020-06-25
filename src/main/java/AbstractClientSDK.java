import com.demo.RestClient;
import com.demo.RestClientFactory;
import com.demo.RestClientType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class AbstractClientSDK implements ClientSDK {
    // RestClient Instance being used
    private RestClient restClient;

    // Underlying Service Instance (Client API location)
    private ServiceInstance serviceInstance;

    // Discovery Client being used to access the client API - can be null
    private DiscoveryClient discoveryClient;

    // Whether or not this instance is using a Discovery Client to access the client API
    private boolean isDiscoveryClient = false;

    private String serviceName;

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    /**
     * Direct invocation to the service
     * @param host
     * @param port
     */
    public AbstractClientSDK(String serviceName, String host, String port, boolean isSecure) {
        this(serviceName, host, port, isSecure, RestClientType.DEFAULT);
    }

    public AbstractClientSDK(String serviceName, String host, String port, boolean isSecure, RestClientType type) {
        this(serviceName, getServiceInstance(serviceName, host, port, isSecure), type, null);
    }

    /**
     * Connect via DiscoveryClient
     *
     */
    public AbstractClientSDK(String serviceName, DiscoveryClient discoveryClient) {
        this(serviceName, discoveryClient, RestClientType.DEFAULT);
    }

    public AbstractClientSDK(String serviceName, DiscoveryClient discoveryClient, RestClientType type) {
        this(serviceName, getServiceInstance(serviceName, discoveryClient), type, discoveryClient);
    }

    public AbstractClientSDK(String serviceName, ServiceInstance serviceInstance, RestClientType type,
                             DiscoveryClient discoveryClient) {
        log.info("SERVICE ID: " + serviceInstance.getServiceId());
        log.info("Host: " + serviceInstance.getHost());
        log.info("Port: " + serviceInstance.getPort());
        log.info("URI: " + serviceInstance.getUri());

        log.info("Discovery Client: " + discoveryClient);
        this.discoveryClient = discoveryClient;
        if(discoveryClient != null) {
            this.isDiscoveryClient = true;
        }

        this.serviceInstance = serviceInstance;
        this.serviceName = serviceName;
        this.restClient = RestClientFactory.build(type);
    }

    private static ServiceInstance getServiceInstance(String serviceName, String host, String port, boolean isSecure) {
        if (host.isEmpty()) {
            throw new IllegalArgumentException("Host may not be null/empty.");
        }

        if (port.isEmpty()) {
            throw new IllegalArgumentException("Port may not be null/empty.");
        }

        try {
            int portNum = Integer.parseInt(port);

            if (portNum < MIN_PORT || portNum > MAX_PORT) {
                throw new IllegalArgumentException(String.format("Port must be between %d and %d.", MIN_PORT, MAX_PORT));
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Port must be an integer.");
        }

        return new DefaultServiceInstance(UUID.randomUUID().toString(), serviceName,
                host, Integer.parseInt(port), isSecure);
    }

    private static ServiceInstance getServiceInstance(String serviceName, DiscoveryClient discoveryClient) {
        List<ServiceInstance> services = discoveryClient.getInstances(serviceName);

        if (services.isEmpty()) {
            throw new IllegalStateException(serviceName + " not found in the Discovery Client " + discoveryClient.description());
        }

        log.info("Services " + services);

        return services.get(0);
    }

    @Override
    public ServiceInstance getServiceInstance() {
        return this.serviceInstance;
    }

    @Override
    public boolean usingDiscoveryClient() {
        return this.isDiscoveryClient;
    }

    @Override
    public String getUri() {
        return this.serviceInstance.getUri().toString();
    }

    @Override
    public RestClient getRestClient() {
        return this.restClient;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }
}
