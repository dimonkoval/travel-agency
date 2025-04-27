#!/bin/bash
# Встановлюємо Maven
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xvf apache-maven-3.9.6-bin.tar.gz
export PATH="$PWD/apache-maven-3.9.6/bin:$PATH"

# Збираємо проєкт
mvn clean install