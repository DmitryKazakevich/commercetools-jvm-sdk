package io.sphere.sdk.customergroups.commands;

import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.customergroups.CustomerGroup;
import io.sphere.sdk.customergroups.CustomerGroupDraft;
import io.sphere.sdk.customergroups.expansion.CustomerGroupExpansionModel;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;

/**
 * <p>Creates a new customer group</p>
 *
 * {@include.example io.sphere.sdk.customergroups.commands.CustomerGroupCreateCommandTest#execution()}
 *
 * @see CustomerGroup
 */
public interface CustomerGroupCreateCommand extends CreateCommand<CustomerGroup>, MetaModelExpansionDsl<CustomerGroup, CustomerGroupCreateCommand, CustomerGroupExpansionModel<CustomerGroup>> {
    static CustomerGroupCreateCommand of(final CustomerGroupDraft draft) {
        return new CustomerGroupCreateCommandImpl(draft);
    }

    static CustomerGroupCreateCommand of(final String groupName) {
        return of(CustomerGroupDraft.of(groupName));
    }
}
