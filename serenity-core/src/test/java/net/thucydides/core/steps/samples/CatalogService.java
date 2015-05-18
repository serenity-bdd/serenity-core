package net.thucydides.core.steps.samples;

public class CatalogService {
    private String name;
    private int quota;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public void saveProduct(Product product) {
    }
}
