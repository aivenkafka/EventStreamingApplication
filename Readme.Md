# Introduction

Hi! Welcome to my an application that will show how Aiven can assist your organization on how
to deploy your Data Mesh!

In our Application today, we will create a random Datagen Producer that will send a list
of FANG organizations and stock prices that people bid at. A Random number generator, stock symbol
and the Time in ISO format will be used.

# Pre-requisites before the Demo

A. Basic knowledge of Apache Kafka is recommended. For more information on Kafka please check the url below

<a href="https://kafka.apache.org/" target="_blank">Kafka Introduction</a>

B. Introduction to the Data Mesh

<a href="https://developer.confluent.io/learn-kafka/data-mesh/intro/" target="_blank">What is a Data Mesh</a>


# Technologies used in this Demo

A. The Aiven Console
B. Kafka Producer
C. Telegraf as our Kafka Consumer
D. InfluxDB + Grafana
E. Kafka Streams and the Aiven Schema Registry
F. Kafka Connect
G. OpenSearch (Elasticsearch Alternative)



# First and foremost, we will create our Kafka producer.

The Kafka Producer is called "KafkaProducerApplication". This shows code Produces a JSON Formatted message when a random UUID as the
key and the values will show a bid of a stock price, time of trade and the stock symbol name.

A basic Kafka Producer with the Metadata Callback Method just for fun called "KafkaProducer"


# Optional: Modifying your Path
To see the data via our CLI, please download the Kafka Binaries
Place it in my path so I do can execute these commands everywhere in your CLI. This makes life easier!

export PATH=$PATH:/Users/Kevin/<kafka_binary_folder_of_choice/bin:$PATH


# Execute our Kafka Consumer. Note how client.properties maps to our ssl certificates


kafka-console-consumer \
--topic stock-prices-topic-aiven-cloud-json \
--bootstrap-server kafka-interview-prep-kjang1923-2d80.aivencloud.com:17180 \
--property print.key=true \
--consumer.config client.properties \
--group "group1"

Tip #1: By default the Consumer reads from latest. Just add --from-beginning if you want to process all messages
Tip #2: You can also specify the group as well! Highly recommended. The Group ID overrides the default Consumer Setting



# Consume data from Kafka and send to InfluxDB using the Telegraf Kafka Plugin

telegraf -config ./src/main/resources/telegraf.conf




