from pyspark.sql import SparkSession

def main():
    # Create Spark session with S3/MinIO support
    spark = SparkSession.builder \
        .appName("ReadFromMinIO") \
        .getOrCreate()


    """
    All the tables that we have:

    silver_catalog.products
    silver_catalog.users

    gold_catalog.aggUsers
    gold_catalog.infoUsers
    gold_catalog.aggProducts
    gold_catalog.infoProducts
    
    """


    print("Reading ICEBERG from MinIO...")

    # Read JSON file as DataFrame
    df = spark.read \
        .format("iceberg") \
        .load("gold_catalog.aggUsers")

    print("DataFrame loaded successfully:")
    df.show(truncate=False)



    # Print the schema of the DataFrame
    print("Schema of the DataFrame:")
    df.printSchema()



    # Stop Spark session
    spark.stop()

if __name__ == "__main__":
    main()
