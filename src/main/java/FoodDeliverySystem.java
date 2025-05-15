// FoodDeliverySystem.java
import java.util.*;

// ---------------------- MODELS ----------------------
class Customer {
    String id, name, address;

    public Customer(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}

class MenuItem {
    String id, name;
    double price;

    public MenuItem(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

class Menu {
    List<MenuItem> items = new ArrayList<>();

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getItems() {
        return items;
    }
}

class Restaurant {
    String id, name, location;
    Menu menu;

    public Restaurant(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.menu = new Menu();
    }

    public Menu getMenu() {
        return menu;
    }
}

enum OrderStatus {
    PLACED, ACCEPTED, PREPARING, DISPATCHED, DELIVERED, CANCELLED
}

class Order {
    String id;
    Customer user;
    Restaurant restaurant;
    List<MenuItem> items;
    double totalAmount;
    OrderStatus status;
    DeliveryPartner deliveryPartner;

    public Order(String id, Customer user, Restaurant restaurant, List<MenuItem> items) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.totalAmount = calculateTotal();
        this.status = OrderStatus.PLACED;
    }

    private double calculateTotal() {
        return items.stream().mapToDouble(item -> item.price).sum();
    }

    public void assignDeliveryPartner(DeliveryPartner partner) {
        this.deliveryPartner = partner;
        partner.setAvailable(false);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}

class DeliveryPartner {
    String id, name;
    boolean isAvailable;

    public DeliveryPartner(String id, String name) {
        this.id = id;
        this.name = name;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}

// ---------------------- SERVICES ----------------------
class RestaurantService {
    Map<String, Restaurant> restaurants = new HashMap<>();

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.id, restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants.values());
    }
}

class DeliveryService {
    List<DeliveryPartner> partners = new ArrayList<>();

    public void addPartner(DeliveryPartner partner) {
        partners.add(partner);
    }

    public DeliveryPartner assignPartner() {
        for (DeliveryPartner partner : partners) {
            if (partner.isAvailable()) return partner;
        }
        return null;
    }
}

class OrderService {
    Map<String, Order> orders = new HashMap<>();
    DeliveryService deliveryService;

    public OrderService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public Order placeOrder(Customer user, Restaurant restaurant, List<MenuItem> items) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, user, restaurant, items);

        DeliveryPartner partner = deliveryService.assignPartner();
        if (partner != null) {
            order.assignDeliveryPartner(partner);
            order.updateStatus(OrderStatus.ACCEPTED);
        }

        orders.put(orderId, order);
        return order;
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        if (orders.containsKey(orderId)) {
            orders.get(orderId).updateStatus(status);
        }
    }

    public void printOrderDetails(String orderId) {
        Order o = orders.get(orderId);
        System.out.println("Order ID: " + o.id);
        System.out.println("User: " + o.user.name);
        System.out.println("Restaurant: " + o.restaurant.name);
        System.out.println("Items:");
        for (MenuItem i : o.items) System.out.println("- " + i.name + " : Rs " + i.price);
        System.out.println("Total: Rs " + o.totalAmount);
        System.out.println("Status: " + o.status);
        if (o.deliveryPartner != null)
            System.out.println("Delivery By: " + o.deliveryPartner.name);
    }
}

// ---------------------- MAIN TEST ----------------------
public class FoodDeliverySystem {
    public static void main(String[] args) {
        // Setup
        RestaurantService restaurantService = new RestaurantService();
        DeliveryService deliveryService = new DeliveryService();
        OrderService orderService = new OrderService(deliveryService);

        // Create sample data
        Customer user = new Customer("u1", "Alice", "123 Main St");

        Restaurant rest1 = new Restaurant("r1", "PizzaHub", "Delhi");
        rest1.getMenu().addItem(new MenuItem("m1", "Margherita", 250));
        rest1.getMenu().addItem(new MenuItem("m2", "Farmhouse", 350));
        restaurantService.addRestaurant(rest1);

        DeliveryPartner dp1 = new DeliveryPartner("d1", "John");
        DeliveryPartner dp2 = new DeliveryPartner("d2", "Jane");
        deliveryService.addPartner(dp1);
        deliveryService.addPartner(dp2);

        // Place order
        List<MenuItem> items = Arrays.asList(
                rest1.getMenu().getItems().get(0),
                rest1.getMenu().getItems().get(1)
        );
        Order order = orderService.placeOrder(user, rest1, items);

        // Print order
        orderService.printOrderDetails(order.id);

        // Update status
        orderService.updateOrderStatus(order.id, OrderStatus.DISPATCHED);
        orderService.printOrderDetails(order.id);
    }
}