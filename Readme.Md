# Introduction
The Data Mesh is the future. At the heart of the Data Mesh is Apache Kafka.
Currently 70% of Fortune 500 Companies are utilizing Apache Kafka.
While Kafka can be extremely powerful, the setup and orchestration can be complex and mind boggling.
With Aiven, we take care of the infrastructure and maintenance for you so that way organizations can focus on software development.

In this tutorial, we will show how Aiven can assist your organization on how
to deploy your Data Mesh using Aiven as our Provider.

In our Application today, we will create a random Datagen Producer that will send a list
of FANG organizations and stock prices that people bid at. A Random number generator, stock symbol
and the Time in ISO format will be used.

Upon sending the data into Kafka, different applications will use the data for different purposes to demonstrate
scalability. Apache Kafka will act as our Central Nervous System of Big Data so applications can react and respond
with little to no latency.

# High Level Overview of the Data Mesh using Aiven

![Alt text](./images/Data_Mesh.png?raw=true "Building the Data Mesh with Aiven")

# Pre-requisites before the Demo

A. Basic knowledge of Apache Kafka is recommended. For more information on Kafka please check the url below

<a href="https://kafka.apache.org/" target="_blank">Kafka Introduction</a>

The Kafka binaries can be downloaded here

<a href="https://kafka.apache.org/downloads" target="_blank">Kafka Binaries</a>

The Blog on How Setting Up Kafka via SSL, Creating a Kafka Producer and Monitoring can be leveraged

<a href="https://github.com/aivenkafka/EventStreamingApplication/tree/master/Blog" target="_blank">Building a Data Mesh With Aiven</a>



B. Introduction to the Data Mesh

<a href="https://www.montecarlodata.com/data-mesh-101-everything-you-need-to-know-to-get-started/" target="_blank">What is a Data Mesh</a>

# Technologies used in this Demo

A. The Aiven Console <br />
B. Kafka Producer <br />
C. Telegraf as our Kafka Consumer <br />
D. InfluxDB + Grafana <br />
E. Kafka Streams and the Aiven Schema Registry <br />
F. Kafka Connect <br />
G. OpenSearch (Elasticsearch Alternative) <br />

C. Installing the Aiven CLI

<a href="https://developer.aiven.io/docs/tools/cli" target="_blank">Aiven CLI</a>


# First and foremost, we will create our Kafka producer.

The Kafka Producer is called "KafkaProducerApplication". This shows code Produces a JSON Formatted message when a random UUID as the
key and the values will show a bid of a stock price, time of trade and the stock symbol name.

A basic Kafka Producer with the Metadata Callback Method just for fun called "KafkaProducer"

# Consume data from Kafka with Telegraf.

You can install the Telegraf CLI.
<a href="https://github.com/influxdata/telegraf" target="_blank">Telegraf CLI</a>

Telegraf is a Kafka Consumer that will read the data from Kafka and send it to the Influx DB.

Model:
telegraf -config <location of telegraf.conf> file

Example:
telegraf -config ./src/main/resources/telegraf.conf

Tip #1: Change the settings in Telegraf to account for your Kafka Server Host Name and the Authentication Settings.

Tip #2: Note the setting with the Consumer Group. I called it "stock-bids-telegraf_metrics_consumers-readme"

![Alt text](./images/telegraf_consumer.png?raw=true "Telegraf Consumer")

# Kafka Streams & Kafka Connect

Kafka Streams can transform the data. For this Demo, data will be transformed from JSON to Avro. The app is called "KafkaStreamsJsontoAvro"
The avsc file will be generated in the "stocktrades.avsc" file to reference the data and transform to Avro. Data will be sent to the Avro Topic

A Kafka Connector will then read from the Output topic and send the data to Opensearch (an alternative to Elasticsearch)


# Monitoring and Observability

Metrics of Kafka instances can be monitored in Grafana via InfluxDB. See the technical blog for details

Note: I created a new Kafka Server and InfluxDB to showcase the demo in a step by step process.

![Alt text](./images/01_Kafka_Metrics.png?raw=true "Step 1: Enable the Integration")
![Alt text](./images/02_InfluxDB.png?raw=true "Step 2: Set Connectivity from InfluxDB to Grafana")
![Alt text](./images/03_Grafana.png?raw=true "Step 3: See the Kafka Metrics in Grafana")

An example of Monitoring is Consumer Groups. Since we had a telegraf Consumer send data to InfluxDB, we were able to
process the data

![Alt text](./images/Consumer_Group_Grafana.png?raw=true "Kafka Consumer Group Visual")

Another example can be found here too!
![Alt text](./images/KafkaMetrics.png?raw=true "Kafka Metrics Sample")


#Optional #1: Modifying your Path
To see the data via our CLI, please download the Kafka Binaries
Place it in my path so I do can execute these commands everywhere in your CLI. This makes life easier!

export PATH=$PATH:/Users/Kevin/<kafka_binary_folder_of_choice/bin:$PATH

Execute our Kafka Consumer. Note how client.properties maps to our ssl certificates

kafka-console-consumer --topic stock-prices-topic-aiven-cloud-json --bootstrap-server kafka-interview-prep-kjang1923-2d80.aivencloud.com:17180 --property print.key=true --consumer.config <location of properties file> --group "group1"

Tip #1: By default the Consumer reads from latest. Just add --from-beginning if you want to process all messages
Tip #2: You can also specify the group as well! Highly recommended. The Group ID overrides the default Consumer Setting


#Optional #2: Monitoring CPU via InfluxDB

Earlier in the tutorial, I showed how telegraf can act as our Kafka Consumer. That same "telefraf.conf" file can be used to send metrics from InfluxDB to Grafana
<a href="https://aiven.io/blog/tig-stack-using-telegraf-influxdb-and-grafana-on-aiven" target="_blank">Tutorial by Francesco Tisiot</a>

#Optional 3: Check out the OpenSearch Kafka Connect Sink Postman Commands. Kafka Connectors can be created via the REST API or the Aiven Console.



