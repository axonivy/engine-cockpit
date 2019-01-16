FROM selenium/standalone-firefox:3.141.59

RUN \
    sudo apt-get -y update && \
    sudo apt-get install -y maven