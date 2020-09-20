package cloud.localstack.docker;

import cloud.localstack.Localstack;
import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.awssdkv1.TestUtils;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.*;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

import static cloud.localstack.docker.ContainerTest.*;

@RunWith(LocalstackTestRunner.class)
@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(randomizePorts = false, services = { "sqs:12345" })
@org.junit.Ignore
@org.junit.jupiter.api.Disabled
public class PortBindingTest {

    @Test
    public void testAccessPredefinedPort() {
        String endpoint = Localstack.INSTANCE.endpointForPort(12345);
        AmazonSQS amazonSQS = TestUtils.getClientSQS(endpoint);
        String url = amazonSQS.createQueue("test-queue").getQueueUrl();
        assertTrue(url.contains("://localhost:12345/queue/test-queue"));
    }

    @Test
    public void createLocalstackContainerWithRandomPorts() throws Exception {
        Container container = Container.createLocalstackContainer(
            EXTERNAL_HOST_NAME, pullNewImage, true, null, null, null);

        try {
            container.waitForAllPorts(EXTERNAL_HOST_NAME);

            assertNotEquals(4567, container.getExternalPortFor(4567));
            assertNotEquals(4575, container.getExternalPortFor(4575));
            assertNotEquals(4583, container.getExternalPortFor(4583));
            assertNotEquals(4584, container.getExternalPortFor(4584));
        }
        finally {
            container.stop();
        }
    }

    @Test
    public void createLocalstackContainerWithStaticPorts() throws Exception {
        Container container = Container.createLocalstackContainer(
            EXTERNAL_HOST_NAME, pullNewImage, false, null, null, null);

        try {
            container.waitForAllPorts(EXTERNAL_HOST_NAME);

            assertEquals(4567, container.getExternalPortFor(4567));
            assertEquals(4575, container.getExternalPortFor(4575));
            assertEquals(4583, container.getExternalPortFor(4583));
            assertEquals(4584, container.getExternalPortFor(4584));
        }
        finally {
            container.stop();
        }
    }

}
