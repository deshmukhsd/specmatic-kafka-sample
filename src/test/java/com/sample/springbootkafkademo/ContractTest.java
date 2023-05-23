package com.sample.springbootkafkademo;

import in.specmatic.kafka.mock.KafkaMock;
import in.specmatic.test.SpecmaticJUnitSupport;
import kafka.metrics.KafkaMetricsReporter;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.common.utils.Time;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import scala.Option;
import scala.collection.JavaConverters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootTest(classes = {SpringbootKafkaDemoApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ContractTest extends SpecmaticJUnitSupport {
    public static KafkaMock kafkaMock = null;
    private static TestingServer zkServer = null;
    public static KafkaServer externalKafkaServer = null;
    private static ConfigurableApplicationContext context = null;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        System.setProperty("host", "localhost");
        System.setProperty("port", "8080");
        System.setProperty("CUSTOM_RESPONSE", "true");

        List<String> fileList= new ArrayList<>();
        fileList.add("src/test/resources/kafka_stub.yaml");

        // kafkaMock is a static variable
        kafkaMock = KafkaMock.fromAsyncAPIFiles(
                        fileList,
                        9092,
                        2181,
                        "./kafka-logs");

        kafkaMock.cleanupLogDir();

        zkServer = kafkaMock.startZooKeeper();

        Properties kafkaConfigProperties = kafkaMock.getKafkaConfigProperties();
        kafkaConfigProperties.put("offsets.topic.replication.factor", "1");

        KafkaConfig kafkaConfig = new KafkaConfig(kafkaConfigProperties);

        externalKafkaServer = new KafkaServer(kafkaConfig,
                Time.SYSTEM, Option.<String>empty(),
                true);
        externalKafkaServer.startup();
    }

    @AfterAll
    public static void tearDown() throws IOException, InterruptedException {
        Thread.sleep(5000);
        kafkaMock.subscribe();
        if (kafkaMock != null) {
            int count = waitForKafkaMessages();
            Assertions.assertNotEquals(0, count);
        }

        if (context != null) context.close();
        if (externalKafkaServer != null) externalKafkaServer.shutdown();
        if (zkServer != null) zkServer.close();
    }

    private static int waitForKafkaMessages() throws InterruptedException {
        int millisecondsWaited = 0;
        int count = 0;
        int sleepInterval = 1000;

        while ((kafkaMock.getMessageHistory().size() == 0 && millisecondsWaited < 5000) || kafkaMock.getMessageHistory().size() != count) {
            count = kafkaMock.getMessageHistory().size();
            System.out.println("Count is "+count+" and message history is "+kafkaMock.getMessageHistory());
            Thread.sleep(sleepInterval);
            millisecondsWaited += sleepInterval;
        }
        System.out.println("Total Kafka messages received by Specmatic: " + count);
        return count;
    }
}