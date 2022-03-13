package KafkaStreamsApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fang.stock.symbols;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class KafkaStreamsJsonToAvro {
    private static Logger logger;

    public static void main(String[] args) throws FileNotFoundException {
        logger = Logger.getLogger(KafkaStreamsJsonToAvro.class.getName());

        final Properties kafkaConfig = buildStreamConfiguration();
        final Properties topicConfig = buildTopicConfiguration();

        if (kafkaConfig == null || topicConfig == null)
            return;

        final String jsonTopic = topicConfig.getProperty("json-topic");
        final String avroTopic = topicConfig.getProperty("avro-topic");

        final ObjectMapper objectMapper = new ObjectMapper();

        StreamsBuilder builder = new StreamsBuilder();
        //Read from our JSON topic
        final KStream<String, String> jsonToAvroStream = builder.stream(
                jsonTopic,
                Consumed.with(Serdes.String(),
                        Serdes.String()));

        jsonToAvroStream.mapValues(v -> {
            symbols stocks = null;

            try {
                final JsonNode jsonNode = objectMapper.readTree(v);

                stocks = new symbols(
                        jsonNode.get("Symbol").asText(),
                        jsonNode.get("AskingPrice").asInt(),
                        jsonNode.get("TimeOfBid").asText());

            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            return stocks;
        }).filter((k,v) -> v != null).to(avroTopic);

        final KafkaStreams streams = new KafkaStreams(builder.build(), kafkaConfig);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run () {
                streams.close();
            }
        }));
    }

    private static Properties buildStreamConfiguration() throws FileNotFoundException {
        logger.info("loading Kafka streams configurations");
        File configFile = new File("./src/main/resources/streams-application-aiven.properties");

        InputStream inputStream = new FileInputStream(configFile);

        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "3");
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000");

        try {
            streamsConfiguration.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("unable to load file");
            return null;
        }

        logger.info("Loading up configs :)");
        return streamsConfiguration;
    }
    private static Properties buildTopicConfiguration() throws FileNotFoundException {
        logger.info ("Loading up the topic configs!");
        File configFile = new File("./src/main/resources/streams-application-aiven.properties");
        InputStream inputStream = new FileInputStream(configFile);
        Properties topicConfiguration = new Properties();

        try {
            topicConfiguration.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Cannot Load File for Topic Configurations");
            return null;
        }
        logger.info("topic configuration loaded...");
        return topicConfiguration;

    }
}
