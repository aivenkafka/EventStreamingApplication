package KafkaClients;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

public class KafkaProducer {
    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(KafkaProducer.class.getName());
        File configFile = new File("./src/main/resources/client-ssl.properties");

        InputStream inputStream = new FileInputStream(configFile);
        Properties config = new Properties();

        config.load(inputStream);

        String topic = config.getProperty("topic");
        String message = config.getProperty("message");

        //System.out.println(topic);
        //System.out.println(message);

        try {
            config.load(new FileInputStream("./src/main/resources/client-ssl.properties"));
        }  catch (IOException e) {

        }

        // create the producer
        org.apache.kafka.clients.producer.KafkaProducer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(config);


        for (int i=0; i<10; i++ ) {
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();

            String key = uuidAsString;
            logger.info("Key: " + key); // log the key
            // create a producer record
            ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>(topic,key, message);

            // send data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic:" + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.warning("Error while producing");
                    }
                }
            });
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();

    }

}
