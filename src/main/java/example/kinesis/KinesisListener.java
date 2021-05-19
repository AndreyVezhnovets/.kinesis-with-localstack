package example.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KinesisListener implements SmartLifecycle {

  @Value("${kinesis.streamName}")
  private String kinesisStreamName;

  @Value("${application.name}")
  private String applicationName;

  private final AmazonKinesis amazonKinesis;
  private final AWSConfig awsConfig;
  private final RecordProcessorFactory recordProcessorFactory;

  private Worker worker;
  private Thread workerThread;

  @Override
  public void start() {
    String workerId = applicationName + UUID.randomUUID();
    final KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(
      applicationName,
      kinesisStreamName,
      awsConfig.getCredentials(),
      workerId)
      .withMetricsLevel(awsConfig.getCloudWatchMetricsLevel())
      .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);

    worker = new Worker.Builder()
      .recordProcessorFactory(recordProcessorFactory)
      .config(config)
      .kinesisClient(amazonKinesis)
      .dynamoDBClient(awsConfig.dynamoDBClient())
      .build();

    workerThread = new Thread(worker, "kinesisListener");
    workerThread.start();
  }

  @Override
  public void stop() {
    log.info("Stopping the worker");

    try {
      worker.createGracefulShutdownCallable().call();
    } catch (Exception e) {
      log.error("Shutting down the Kinesis Worker failed!", e);
    }
  }

  @Override
  public boolean isRunning() {
    return null != workerThread && workerThread.isAlive();
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }
}
