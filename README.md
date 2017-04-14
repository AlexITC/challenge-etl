# ODA-Hackathon

## ETL Challenge

This project implements a very simple ETL.

There is a main application built with Scala and Spark which can receive a CSV file or a MySQL table and load it into a Hadoop File System.

When the application is run, it detects if the model already exists to perform incremental updates.

## Running

Before being able to run the application you will need to install the following dependencies:
- JDK 8
- SBT

### Importing CSV file

Run the following command (this may require sudo privileges):
```
sbt "run csv [output-location] [key] [csv-file-path]"
```

Where:
- [output-location] is the location the imported data is stored
- [key] is the name of the model that you are importing.
- [csv-file-path] is the path to the csv file to import.

For example, run the following to import the example `check-in.csv` file:

```
sbt "run csv hdfs://localhost:9000 checkin csv/check-in.csv"
```


### Importing MySQL table

Run the following command (this may require sudo privileges):
```
sbt "run mysql [output-location] [table] [host] [port] [database] [user] [password]"
```

Where:
- [output-location] is the location the imported data is stored
- [table] is the name of the table that you are importing (This is handled the same way as the "key" value while importing a csv file).
- [host] the mysql host.
- [port] the mysql port.
- [database] the mysql database where the table is located.
- [user] the mysql user.
- [password] the mysql password.

For example, run the following to import the example `person` table:

```
sbt "run mysql hdfs://localhost:9000 person 127.0.1.1 33060 hackathon root root"
```

## Development

In order to do development related tasks, you will need to also install `docker` and `docker-compose`.

To set up the environment, run `docker-compose up` and then `./fill_mysql.sh`.

Use `sbt compile` to compile the application and run the application using root user.
