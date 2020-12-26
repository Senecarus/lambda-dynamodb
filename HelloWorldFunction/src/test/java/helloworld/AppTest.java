package helloworld;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import org.junit.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppTest {

  @Test
  public void successfulSQSEvent() {
    App app = new App();
    SQSEvent event = new SQSEvent();

    SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
    message.setBody("Test message from sqs UT");
    event.setRecords(Arrays.asList(message));
    app.handleRequest(event, null);

  }
}
