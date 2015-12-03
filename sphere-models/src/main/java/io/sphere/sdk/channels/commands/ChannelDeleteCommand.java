package io.sphere.sdk.channels.commands;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.expansion.ChannelExpansionModel;
import io.sphere.sdk.commands.DeleteCommand;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;
import io.sphere.sdk.models.Versioned;

/** Deletes a channel.

 <p>Example:</p>
 {@include.example io.sphere.sdk.channels.commands.ChannelDeleteCommandTest#execution()}
 */
public interface ChannelDeleteCommand extends MetaModelExpansionDsl<Channel, ChannelDeleteCommand, ChannelExpansionModel<Channel>>, DeleteCommand<Channel> {
    static ChannelDeleteCommand of(final Versioned<Channel> versioned) {
        return new ChannelDeleteCommandImpl(versioned);
    }
}
