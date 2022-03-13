package KafkaClients;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;

public class KafkaProducerApplication {
    public static void main(String[] args) throws IOException {
        //Add a Logger to show the logs
        final Logger logger = Logger.getLogger(KafkaProducerApplication.class.getName());
        //We will invoke the properties file. Settings like the Kafka Server Name and Authentication Information will be provided here.
        File configFile = new File("./src/main/resources/client-ssl.properties");
        //We will have an Input Stream to load the configurations file
        InputStream inputStream = new FileInputStream(configFile);
        //We will create the properties
        Properties config = new Properties();
        //We will now load the Properties
        config.load(inputStream);
        try {
            config.load(new FileInputStream("./src/main/resources/client-ssl.properties"));
        }  catch (IOException e) {
            logger.info("Error reading the ssl info");

        }
        //Build the Kafka Producer off of the Properties as seen in the "client-ssl.properties" seen in lines 26-38
        org.apache.kafka.clients.producer.KafkaProducer<String, String> producer
                = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(config);

        //Our producer will continuously run.
        while(true) {
            //The function called newStockPrice is our Producer Record. For people new to Java, I created the Producer Record seen in lines
            //59 through 89. There is a way that the code can be defined without functions. See "KafkaProducer" as an example.
            try {
                producer.send(newStockPrice("key"));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        //Safely shut down the Producer.
        producer.close();
    }


    public static ProducerRecord<String, String> newStockPrice(String key) {
            // create a producer record Lets create a JSON message
            ObjectNode stockInformation = JsonNodeFactory.instance.objectNode();

            //Traded Amount each day
            Integer traded_amount = ThreadLocalRandom.current().nextInt(0, 3000);

            //Range of FB Stock



            //Time of the Trade
            Instant now = Instant.now();
            //Generate a UUID
            UUID uuid = UUID.randomUUID();

            //Transform the uuid into a string and concanate it into a random JSON
            String uuidJson = "{\"key\"" + ":" + " \"" + uuid.toString() + "\" }";

            //Random Ticker Function to enable to randomness in the asking price, stockTickerSymbol and time
            Random randomTicker = new Random();

            //FANG Stock Symbol Names
            String[] stockSymbol = {"FB", "AAPL", "NFLX", "AAPL", "GOOGL"};

            while(true) {
                    int stockTickerSymbol = randomTicker.nextInt(stockSymbol.length);
                    stockInformation.put("Symbol", stockSymbol[stockTickerSymbol]);
                    stockInformation.put("AskingPrice", traded_amount);
                    stockInformation.put("TimeOfBid", now.toString());
                    break;
                }

        return new ProducerRecord<String, String>("stock-prices-topic-aiven", uuidJson, stockInformation.toString());
        }
    }

