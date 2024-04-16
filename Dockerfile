
FROM openjdk:11-jdk-slim

ENV ANDROID_HOME /usr/local/android-sdk

ENV PATH $PATH:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools

RUN apt-get update && \
    apt-get install -y wget unzip && \
    apt-get clean

RUN mkdir $ANDROID_HOME && \
    cd $ANDROID_HOME && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip -O sdk.zip && \
    unzip -q sdk.zip && \
    rm sdk.zip

RUN mkdir ~/.android && touch ~/.android/repositories.cfg && \
    yes | $ANDROID_HOME/cmdline-tools/bin/sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "platforms;android-29"

RUN mkdir /app

COPY app/build/outputs/apk/debug/app-debug.apk /app/app-debug.apk

EXPOSE 8080

CMD ["bash", "-c", "echo 'Running Android app' && sleep infinity"]
