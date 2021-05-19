package example.kinesis;

import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class AWSConfig {

  public AmazonDynamoDB dynamoDBClient() {
    return AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(
        new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-2"))
      .withClientConfiguration(
        PredefinedClientConfigurations.defaultConfig()
          .withNonProxyHosts("localhost")
          .withSignerOverride("AWSS3V4SignerType"))
      .build();
  }

  public AWSCredentialsProvider getCredentials() {
    return new AWSStaticCredentialsProvider(new BasicAWSCredentials("accesskey", "secretkey"));
  }

  public MetricsLevel getCloudWatchMetricsLevel() {
    return MetricsLevel.NONE;
  }
}
