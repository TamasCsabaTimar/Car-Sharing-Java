class Product {
    private long id;
    private String model;
    private int price;
    private LocalDate dateOfArrival;
    private String vendor;

    public Product(long id, String model, int price, LocalDate dateOfArrival, String vendor) {
        this.id = id;
        this.model = model;
        this.price = price;
        this.dateOfArrival = dateOfArrival;
        this.vendor = vendor;
    }

    public Product() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getDateOfArrival() {
        return dateOfArrival;
    }

    public void setDateOfArrival(LocalDate dateOfArrival) {
        this.dateOfArrival = dateOfArrival;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}

class ProductDTO {
    private long id;
    private String model;
    private int price;

    public ProductDTO(long id, String model, int price) {
        this.id = id;
        this.model = model;
        this.price = price;
    }

    public ProductDTO() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class Solution {
    ProductDTO convertProductToDTO(Product product) {
        return new ProductDTO(product.getId(), product.getModel(), product.getPrice());
    }
}