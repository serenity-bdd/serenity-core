package caffeinateme.model;

import java.util.ArrayList;
import java.util.List;

public class ProductCatalog {

    List<ProductPrice> catalog = new ArrayList<>();

    public void addProductsWithPrices(List<ProductPrice> productPrices) {
        catalog.addAll(productPrices);
    }

    public double priceOf(String productName) {
        return catalog.stream()
                .filter(product -> product.productName().equals(productName))
                .findFirst()
                .orElseThrow(UnknownProductException::new)
                .price();
    }
}
