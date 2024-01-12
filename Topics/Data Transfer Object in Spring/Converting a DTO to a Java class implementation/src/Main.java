import java.time.LocalDate;

class Solution {
    Product convertProductDTOToProduct(ProductDTO dto) {
        Product p = new Product();
        p.setId(dto.getId());
        p.setModel(dto.getModel());
        p.setPrice(dto.getPrice());
        p.setDateOfArrival(LocalDate.of(2023, 1, 15));
        p.setVendor("SuperVendor");
        return p;
    }
}