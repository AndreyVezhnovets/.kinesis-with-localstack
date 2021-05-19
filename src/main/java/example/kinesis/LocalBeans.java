package example.kinesis;

import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import com.amazonaws.services.kinesis.model.ResourceInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class LocalBeans {

  @Bean
  public AmazonKinesis localAmazonKinesis() {
    System.setProperty("aws.accessKeyId", "accesskey");
    System.setProperty("aws.secretKey", "secretkey");
    AmazonKinesis kinesis = AmazonKinesisClientBuilder
      .standard()
      .withEndpointConfiguration(
        new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-2"))
      .withClientConfiguration(
        PredefinedClientConfigurations.defaultConfig()
          .withNonProxyHosts("localhost")
          .withSignerOverride("AWSS3V4SignerType"))
      .build();

    System.setProperty("com.amazonaws.sdk.disableCbor", "1");

    return kinesis;
  }

}
