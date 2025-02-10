from pyspark.sql import SparkSession

def main():
    # Create Spark session with S3/MinIO support
    spark = SparkSession.builder \
        .appName("ReadFromMinIO") \
        .getOrCreate()




    print("Reading ICEBERG from MinIO...")

    # Read JSON file as DataFrame
    df = spark.read \
        .format("iceberg") \
        .load("silver_catalog.products")

    print("DataFrame loaded successfully:")
    df.show(truncate=False)


    # Print the schema of the DataFrame
    print("Schema of the DataFrame:")
    df.printSchema()



    # Stop Spark session
    spark.stop()

if __name__ == "__main__":
    main()
