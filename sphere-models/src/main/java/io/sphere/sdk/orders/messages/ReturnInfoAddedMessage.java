package io.sphere.sdk.orders.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.sphere.sdk.messages.GenericMessageImpl;
import io.sphere.sdk.messages.MessageDerivatHint;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.ReturnInfo;
import io.sphere.sdk.queries.PagedQueryResult;

import java.time.ZonedDateTime;

@JsonDeserialize(as = ReturnInfoAddedMessage.class)//important to override annotation in Message class
public class ReturnInfoAddedMessage extends GenericMessageImpl<Order> {
    public static final String MESSAGE_TYPE = "ReturnInfoAdded";
    public static final MessageDerivatHint<ReturnInfoAddedMessage> MESSAGE_HINT =
            MessageDerivatHint.ofSingleMessageType(MESSAGE_TYPE,
                    new TypeReference<PagedQueryResult<ReturnInfoAddedMessage>>() {
                    },
                    new TypeReference<ReturnInfoAddedMessage>() {
                    }
            );

    private final ReturnInfo returnInfo;

    @JsonCreator
    private ReturnInfoAddedMessage(final String id, final Long version, final ZonedDateTime createdAt, final ZonedDateTime lastModifiedAt, final JsonNode resource, final Long sequenceNumber, final Long resourceVersion, final String type, final ReturnInfo returnInfo) {
        super(id, version, createdAt, lastModifiedAt, resource, sequenceNumber, resourceVersion, type, MessagesPackage.ORDER_REFERENCE_TYPE_REFERENCE);
        this.returnInfo = returnInfo;
    }

    public ReturnInfo getReturnInfo() {
        return returnInfo;
    }
}
