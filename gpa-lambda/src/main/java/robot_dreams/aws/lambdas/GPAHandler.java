package robot_dreams.aws.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GPAHandler implements RequestHandler<S3Event, String> {

    private static final Region REGION = Region.EU_CENTRAL_1;

    private S3Client s3Client;

    private LambdaLogger logger;

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        logger = context.getLogger();

        if (s3Client == null) {
            s3Client = S3Client.builder()
                    .httpClientBuilder(ApacheHttpClient.builder())
                    .region(REGION)
                    .build();
        }

        List<S3EventNotification.S3EventNotificationRecord> records = s3event.getRecords();

        String result;

        if (records.size() == 1) {
            String srcBucket = records.getFirst().getS3().getBucket().getName();
            logger.log("Bucket: " + srcBucket);
            String srcKey = records.getFirst().getS3().getObject().getUrlDecodedKey();
            logger.log("Key: " + srcKey);

            List<Data> data = convertData(getData(s3Client, srcBucket, srcKey), ',', true);
            logger.log("Data size: " + data.size());

            double averageGpa = averageGpa(data);
            result = "Average of GPA: " + averageGpa;
        } else {
            result = "Invalid event. Expected 1 record, got " + records.size();
        }

        logger.log(result);
        return result;
    }

    public InputStream getData(S3Client s3Client, String bucketName, String keyName) {
        GetObjectRequest objectRequest = createGetObjectRequest(bucketName, keyName);
        try {
            return s3Client.getObject(objectRequest);
        } catch (S3Exception e) {
            handleException(e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public List<Data> convertData(InputStream inputStream, Character lineSeparator, boolean skipHeader) {
        List<Data> listOfData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = skipHeader;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(String.valueOf(lineSeparator));

                if (fields.length == 2) {
                    Data data = new Data(Integer.parseInt(fields[0].trim()), Double.parseDouble(fields[1].trim()));
                    listOfData.add(data);
                }
            }
        } catch (Exception e) {
            handleException(e.getMessage());
        }

        return listOfData;
    }

    private GetObjectRequest createGetObjectRequest(String bucketName, String keyName) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
    }

    private void handleException(String message) {
        System.err.println(message);
        logger.log(message);
        System.exit(1);
    }

    public double averageGpa(List<Data> data) {
        return BigDecimal.valueOf(data.parallelStream()
                        .mapToDouble(Data::getGpa)
                        .average()
                        .orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}