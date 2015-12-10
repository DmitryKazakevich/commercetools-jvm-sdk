package io.sphere.sdk.inventory;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.ChannelRole;
import io.sphere.sdk.client.TestClient;
import io.sphere.sdk.inventory.commands.InventoryEntryDeleteCommand;
import io.sphere.sdk.inventory.commands.InventoryEntryCreateCommand;
import io.sphere.sdk.models.Base;

import java.time.ZonedDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.sphere.sdk.channels.ChannelFixtures.withChannelOfRole;
import static io.sphere.sdk.test.SphereTestUtils.*;

public class InventoryEntryFixtures extends Base {
    public static void withUpdateableInventoryEntry(final TestClient client, final Function<InventoryEntry, InventoryEntry> f) {
        withUpdateableInventoryEntry(client, InventoryEntryDraft.of(randomKey(), 5L), f);
    }

    public static void withUpdateableInventoryEntry(final TestClient client, final InventoryEntryDraft inventoryEntryDraft, final Function<InventoryEntry, InventoryEntry> f) {
        final InventoryEntry inventoryEntry = client.executeBlocking(InventoryEntryCreateCommand.of(inventoryEntryDraft));
        final InventoryEntry updatedEntry = f.apply(inventoryEntry);
        client.executeBlocking(InventoryEntryDeleteCommand.of(updatedEntry));
    }

    public static void withInventoryEntryAndSupplyChannel(final TestClient client, final ChannelRole channelRole, final BiConsumer<InventoryEntry, Channel> consumer) {
        withChannelOfRole(client, channelRole, channel -> {
            final String sku = randomKey();
            final long quantityOnStock = 10;
            final ZonedDateTime expectedDelivery = tomorrowZonedDateTime();
            final int restockableInDays = 3;
            final InventoryEntryDraft draft = InventoryEntryDraft.of(sku, quantityOnStock)
                    .withExpectedDelivery(expectedDelivery)
                    .withRestockableInDays(restockableInDays)
                    .withSupplyChannel(channel);
            withUpdateableInventoryEntry(client, draft, entry -> {
                consumer.accept(entry, channel);
                return entry;
            });
        });
    }
}
