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

        //Build Function to invoke Properties
        final Properties kafkaConfig = buildStreamConfiguration();
        final Properties topicConfig = buildTopicConfiguration();

        if (kafkaConfig == null || topicConfig == null)
            return;
        //In "streams-application-aiven.properties" I have the JSON Topic listed and the Avro Topic as well
        final String jsonTopic = topicConfig.getProperty("json-topic");
        final String avroTopic = topicConfig.getProperty("avro-topic");

        //I set the Object Mapper to read our JSON Data
        final ObjectMapper objectMapper = new ObjectMapper();

        //A Kafka Streams building is being built to read from a JSON Topic
        StreamsBuilder builder = new StreamsBuilder();
        //Read from our JSON topic
        final KStream<String, String> jsonToAvroStream = builder.stream(
                jsonTopic,
                Consumed.with(Serdes.String(),
                        Serdes.String()));
        jsonToAvroStream.mapValues(v -> {
            //My Avro Pojo Class I generated from the avsc file in "main" -> "avro"
            symbols stocks = null;
            //I am reading the JSON Data and referencing the Properties so they can be transformed to Avro
            try {
                final JsonNode jsonNode = objectMapper.readTree(v);
                //I
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
    //This is a Function to invoke the Kafka Streams Info particularly the Security configurations

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
    //This is a function to load in Topic Configs.
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
