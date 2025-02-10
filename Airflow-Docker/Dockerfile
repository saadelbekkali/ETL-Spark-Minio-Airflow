FROM apache/airflow:2.6.3-python3.8

USER root

# Install OpenJDK-11 and other necessary tools
RUN apt-get update && \
    apt-get install -y \
    openjdk-11-jdk \
    procps \
    curl \
    wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME and SPARK_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV SPARK_HOME=/opt/spark
ENV PATH="${JAVA_HOME}/bin:${SPARK_HOME}/bin:${PATH}"

# Install Spark
RUN curl -O https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz && \
    tar -xf spark-3.4.0-bin-hadoop3.tgz && \
    mv spark-3.4.0-bin-hadoop3 /opt/spark && \
    rm spark-3.4.0-bin-hadoop3.tgz

# Switch to airflow user for pip installations
USER airflow

# Install Python dependencies
RUN pip install --no-cache-dir \
    apache-airflow-providers-apache-spark==4.1.3 \
    pyspark==3.4.0

WORKDIR /opt/airflow