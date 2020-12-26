package helloworld;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ExpectedAttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<SQSEvent, Void> {

    private DynamoDbClient ddb;
    private String DYNAMODB_TABLE_NAME = "Clicks";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public Void handleRequest(final SQSEvent event, final Context context) {

        logger.info("Got request: " + event.toString());

        try {

            this.initDynamoDbClient();
            Map<String, AttributeValue> item = new HashMap<>();

            for(SQSEvent.SQSMessage msg : event.getRecords()){
                item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
                item.put("timestamp", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());
                item.put("request", AttributeValue.builder().s(msg.getBody()).build());
                writeItem(item);
            }
        } catch (Exception e) {
            logger.error("Handling request exception!", e);

        }
        return null;
    }

    private void writeItem(Map<String, AttributeValue> item) {

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .item(item)
                .expected(Collections.singletonMap("id",
                        ExpectedAttributeValue.builder().exists(false).build()))
                .build();

        ddb.putItem(putItemRequest);
        logger.info("Request successfully recorded in DynamoDB: " + putItemRequest);
    }

    private void initDynamoDbClient() {

        if (ddb == null) {
            ddb = DynamoDbClient.builder()
                    .region(Region.EU_NORTH_1)
                    .httpClient(ApacheHttpClient.builder()
                            .maxConnections(50)
                            .build())
                    .build();
        }

    }
}
