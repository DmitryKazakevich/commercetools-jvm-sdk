package io.sphere.sdk.customergroups.queries;

import io.sphere.sdk.customergroups.CustomerGroup;
import io.sphere.sdk.customergroups.expansion.CustomerGroupExpansionModel;
import io.sphere.sdk.queries.MetaModelFetchDslBuilder;
import io.sphere.sdk.queries.MetaModelFetchDslImpl;

public class CustomerGroupByIdFetchImpl extends MetaModelFetchDslImpl<CustomerGroup, CustomerGroupByIdFetch, CustomerGroupExpansionModel<CustomerGroup>> implements CustomerGroupByIdFetch {
    CustomerGroupByIdFetchImpl(final String id) {
        super(id, CustomerGroupEndpoint.ENDPOINT, CustomerGroupExpansionModel.of(), CustomerGroupByIdFetchImpl::new);
    }

    public CustomerGroupByIdFetchImpl(MetaModelFetchDslBuilder<CustomerGroup, CustomerGroupByIdFetch, CustomerGroupExpansionModel<CustomerGroup>> builder) {
        super(builder);
    }
}
