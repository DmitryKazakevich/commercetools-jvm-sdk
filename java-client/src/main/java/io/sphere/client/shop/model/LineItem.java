package io.sphere.client.shop.model;

import io.sphere.client.model.Money;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Single product variant in a {@link Cart} or {@link Order}, with a quantity. */
public class LineItem {
    @Nonnull private String id;
    @Nonnull private String productId;
    @Nonnull @JsonProperty("name") private String productName = "";
    @Nonnull @JsonProperty("variant") private Variant variant;
    private int quantity;
    @Nonnull private Price price;
    private TaxRate taxRate;

    // for JSON deserializer
    private LineItem() {}

    /** Unique id of this line item. */
    @Nonnull public String getId() { return id; }

    /** Unique id of the product. */
    @Nonnull public String getProductId() { return productId; }

    /** Name of the product. */
    @Nonnull public String getProductName() { return productName; }

    /** Copy of the product variant from the time when time line item was created. */
    @Nonnull public Variant getVariant() { return variant; }

    /** Number of items ordered. */
    public int getQuantity() { return quantity; }

    /** The total price of this line item, that is price value times quantity. */
    @Nonnull public Money getTotalPrice() { return price.getValue().multiply(quantity); }

    /** The price. */
    @Nonnull public Price getPrice() { return price; }

    /** The tax rate of this line item. Optional.
     *
     *  <p>The tax rate is selected based on the cart's shipping address and is only set when the
     *  shipping address is set. */
    @Nullable public TaxRate getTaxRate() { return taxRate; }
}
