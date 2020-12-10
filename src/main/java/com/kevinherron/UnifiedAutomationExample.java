package com.kevinherron;

import org.eclipse.milo.opcua.binaryschema.GenericBsdParser;
import org.eclipse.milo.opcua.sdk.client.DataTypeDictionarySessionInitializer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnifiedAutomationExample {

  private static final Logger logger = LoggerFactory.getLogger(UnifiedAutomationExample.class);

  public static void main(String[] args) throws Exception {
    OpcUaClient client = OpcUaClient.create(
        "opc.tcp://172.16.127.2:48010",
        endpoints ->
            endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst(),
        OpcUaClientConfigBuilder::build
    );

    client.addSessionInitializer(new DataTypeDictionarySessionInitializer(new GenericBsdParser()));

    client.connect().get();

    DataValue dataValue = client.readValue(
        0.0,
        TimestampsToReturn.Neither,
        NodeId.parse("ns=2;s=Demo.Static.Scalar.WorkOrder")
    ).get();

    ExtensionObject xo = (ExtensionObject) dataValue.getValue().getValue();

    Object value = xo.decode(client.getSerializationContext());

    logger.info("value: {}", value);
  }

}
