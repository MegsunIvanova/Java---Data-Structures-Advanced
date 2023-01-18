import java.util.*;

public class ShoppingCentre {
    private Map<Product, Integer> productsQuantity;

    private Map<String, TreeSet<Product>> productsByName;
    private Map<String, TreeSet<Product>> productsByProducer;
    private Map<NameAndProducerKey, TreeSet<Product>> productsByNameAndProducer;

    public ShoppingCentre() {
        productsQuantity = new TreeMap<>();

        productsByName = new HashMap<>();
        productsByProducer = new HashMap<>();
        productsByNameAndProducer = new HashMap<>();
    }

    private static class NameAndProducerKey {
        private final String name;
        private final String producer;

        private NameAndProducerKey(String name, String producer) {
            this.name = name;
            this.producer = producer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NameAndProducerKey)) return false;
            NameAndProducerKey that = (NameAndProducerKey) o;
            return Objects.equals(name, that.name) && Objects.equals(producer, that.producer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name) + 73 * Objects.hash(producer);
        }
    }

    public String addProduct(String name, double price, String producer) {
        Product product = new Product(name, price, producer);

        productsQuantity.putIfAbsent(product, 0);
        Integer newQuantity = productsQuantity.get(product) + 1;
        productsQuantity.put(product, newQuantity);

        addToIndices(product);

        return "Product added";
    }

    public String delete(String name, String producer) {
        Set<Product> toDelete = productsByNameAndProducer.remove(new NameAndProducerKey(name, producer));

        if (toDelete == null) {
            return "No products found";
        }

        return deleteProductsQuantity(toDelete);
    }

    public String delete(String producer) {
        Set<Product> toDelete = productsByProducer.remove(producer);

        if (toDelete == null) {
            return "No products found";
        }

        return deleteProductsQuantity(toDelete);
    }

    public String findProductsByName(String name) {
        if (productsByName.get(name) == null) {
            return "No products found";
        }

        StringBuilder result = new StringBuilder();
        for (Product product : productsByName.get(name)) {
            Integer quantity = productsQuantity.get(product) == null ? 0 : productsQuantity.get(product);

            while (quantity-- > 0) {
                result.append(product.toString()).append(System.lineSeparator());
            }

        }

        if (result.toString().trim().isEmpty()) {
            return "No products found";
        }

        return result.toString().trim();
    }

    public String findProductsByProducer(String producer) {
        if (productsByProducer.get(producer) == null) {
            return "No products found";
        }

        StringBuilder result = new StringBuilder();
        for (Product product : productsByProducer.get(producer)) {
            Integer quantity = productsQuantity.get(product) == null ? 0 : productsQuantity.get(product);

            while (quantity-- > 0) {
                result.append(product.toString()).append(System.lineSeparator());
            }

        }

        if (result.toString().trim().isEmpty()) {
            return "No products found";
        }

        return result.toString().trim();
    }

    public String findProductsByPriceRange(double priceFrom, double priceTo) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Product, Integer> entry : productsQuantity.entrySet()) {
            double currPrice = entry.getKey().getPrice();
            if (currPrice >= priceFrom && currPrice <= priceTo) {
                Integer quantity = entry.getValue();
                while (quantity-- > 0) {
                    result.append(entry.getKey().toString()).append(System.lineSeparator());
                }
            }
        }

        if (result.toString().trim().isEmpty()) {
            return "No products found";
        }

        return result.toString().trim();
    }

    private void addToIndices(Product p) {
        addToIndices(p.getName(), p, productsByName);
        addToIndices(p.getProducer(), p, productsByProducer);

        NameAndProducerKey nameAndProducerKey = new NameAndProducerKey(p.getName(), p.getProducer());
        addToIndices(nameAndProducerKey, p, productsByNameAndProducer);
    }

    private <K> void addToIndices(K key, Product p, Map<K, TreeSet<Product>> map) {
        map.putIfAbsent(key, new TreeSet<>());
        map.get(key).add(p);
    }

    private String deleteProductsQuantity(Set<Product> products) {
        int countRemoved = 0;
        for (Product product : products) {
            countRemoved += productsQuantity.remove(product);
        }
        if (countRemoved == 0) {
            return "No products found";
        }

        return countRemoved + " products deleted";
    }

}
