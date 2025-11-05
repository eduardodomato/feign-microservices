package dto;

public record ProductDTO(Integer id, String name, String description, String imageURL) {
    // Constructor for creating new products (without ID)
    public ProductDTO(String name, String description, String imageURL) {
        this(null, name, description, imageURL);
    }
}


