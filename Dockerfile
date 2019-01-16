FROM selenium/standalone-firefox:3.141.59

RUN \
    sudo apt-get -y update && \
    sudo apt-get install -y maven && \
    sudo apt-get install openjdk-8-jdk-headless