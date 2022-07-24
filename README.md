# sparkstreaming-cassandra

Scala version __2.12.15__

An external library is needed : __spark-cassandra-connector-assembly-3.0.0-18-gcb4065b5.jar__  
File -> Project Structure -> Libraries -> `+`

Start Cassandra server
`cassandra -f`

Keyspace and Table creation + Insertion  
`CREATE KEYSPACE spark WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};`  
`CREATE TABLE spark.user(id text PRIMARY KEY, name text, city text, age text);`  
`INSERT INTO spark.user(id, name, city, age) VALUES ('a1', 'Alice', 'New York', '27');`  
