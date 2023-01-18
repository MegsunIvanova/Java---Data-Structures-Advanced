import java.util.Objects;

public class Product implements Comparable<Product> {
    private String name;
    private double price;
    private String producer;

    public Product(String name, double price, String producer) {
        this.name = name;
        this.price = price;
        this.producer = producer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public int compareTo(Product other) {
        if (this.name.compareTo(other.name) != 0) {
            return this.name.compareTo(other.name);
        }

        if (this.producer.compareTo(other.producer) != 0) {
            return this.producer.compareTo(other.producer);
        }

        return Double.compare(this.price, other.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 &&
                Objects.equals(name, product.name) &&
                Objects.equals(producer, product.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, producer);
    }

    @Override
    public String toString() {
        return String.format("{%s;%s;%.2f}", name, producer, price);
    }
}
