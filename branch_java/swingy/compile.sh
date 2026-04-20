# !bin/bash

./mvnw clean package
java -DskipTests -cp target/swingy-1.0-SNAPSHOT.jar swingy.Swingy