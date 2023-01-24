package caffeinateme.model;

import java.util.List;

public record Receipt(double subtotal,
                     double serviceFee,
                     double total,
                     List<ReceiptLineItem> lineItems) {
}
