#!/bin/bash

echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
java -version
mvn -version

# Встановлюємо OpenJDK 17
apt-get update -y && apt-get install -y openjdk-17-jdk

# Визначаємо шлях до JDK
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Встановлюємо Maven вручну
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xvf apache-maven-3.9.6-bin.tar.gz
export PATH="$PWD/apache-maven-3.9.6/bin:$PATH"

# Збираємо проєкт
mvn clean install