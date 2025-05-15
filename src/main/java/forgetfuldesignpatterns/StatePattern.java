package forgetfuldesignpatterns;


import java.util.*;

// Core: Item.java
class Item {
    private final String id;
    private final String name;
    private final double price;

    public Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}

// Core: Inventory.java

class Inventory {
    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    public void addItem(Item item, int count) {
        items.put(item.getId(), item);
        stock.put(item.getId(), stock.getOrDefault(item.getId(), 0) + count);
    }

    public boolean isAvailable(String itemId) {
        return stock.getOrDefault(itemId, 0) > 0;
    }

    public void dispenseItem(String itemId) {
        if (!isAvailable(itemId)) throw new RuntimeException("Out of stock");
        stock.put(itemId, stock.get(itemId) - 1);
    }

    public Item getItem(String itemId) {
        return items.get(itemId);
    }

    public void displayItems() {
        System.out.println("Available Items:");
        for (String id : items.keySet()) {
            Item item = items.get(id);
            System.out.println(id + ": " + item.getName() + " - $" + item.getPrice() + ", Stock: " + stock.getOrDefault(id, 0));
        }
    }
}

// Payment: Payment.java
interface Payment {
    boolean pay(double amount);
}

// Payment: CashPayment.java
class CashPayment implements Payment {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid $" + amount + " using Cash");
        return true;
    }
}

// Payment: CardPayment.java
class CardPayment implements Payment {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid $" + amount + " using Card");
        return true;
    }
}

// Payment: PaymentFactory.java
class PaymentFactory {
    public static Payment getPaymentMethod(String type) {
        switch (type.toLowerCase()) {
            case "cash": return new CashPayment();
            case "card": return new CardPayment();
            default: throw new IllegalArgumentException("Unsupported Payment Type");
        }
    }
}

// State Pattern: VendingState.java
interface VendingState {
    void insertMoney(double amount);
    void selectItem(String itemId);
    void dispense();
    void cancel();
}

// State: IdleState.java
class IdleState implements VendingState {
    private VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney(double amount) {
        machine.setCurrentBalance(amount);
        System.out.println("Money inserted: $" + amount);
        machine.setState(machine.getHasMoneyState());
    }

    public void selectItem(String itemId) {
        System.out.println("Please insert money first.");
    }

    public void dispense() {
        System.out.println("Insert money and select item first.");
    }

    public void cancel() {
        System.out.println("No transaction to cancel.");
    }
}

// State: HasMoneyState.java
class HasMoneyState implements VendingState {
    private VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney(double amount) {
        System.out.println("Money already inserted.");
    }

    public void selectItem(String itemId) {
        if (!machine.getInventory().isAvailable(itemId)) {
            System.out.println("Item out of stock.");
            machine.setState(machine.getIdleState());
            return;
        }
        Item item = machine.getInventory().getItem(itemId);
        if (machine.getCurrentBalance() < item.getPrice()) {
            System.out.println("Insufficient balance.");
            return;
        }
        machine.setSelectedItemId(itemId);
        machine.setState(machine.getDispensingState());
        machine.dispense();
    }

    public void dispense() {
        System.out.println("Please select item first.");
    }

    public void cancel() {
        System.out.println("Transaction cancelled. Refunding $" + machine.getCurrentBalance());
        machine.setCurrentBalance(0);
        machine.setState(machine.getIdleState());
    }
}

// State: DispensingState.java
class DispensingState implements VendingState {
    private VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney(double amount) {
        System.out.println("Already processing item.");
    }

    public void selectItem(String itemId) {
        System.out.println("Already processing.");
    }

    public void dispense() {
        String itemId = machine.getSelectedItemId();
        Item item = machine.getInventory().getItem(itemId);
        machine.getInventory().dispenseItem(itemId);
        machine.setCurrentBalance(machine.getCurrentBalance() - item.getPrice());
        System.out.println("Dispensing item: " + item.getName());
        System.out.println("Change returned: $" + machine.getCurrentBalance());
        machine.setCurrentBalance(0);
        machine.setSelectedItemId(null);
        machine.setState(machine.getIdleState());
    }

    public void cancel() {
        System.out.println("Already dispensing. Cannot cancel.");
    }
}

// Core: VendingMachine.java
class VendingMachine {
    private VendingState idleState;
    private VendingState hasMoneyState;
    private VendingState dispensingState;

    private VendingState currentState;
    private Inventory inventory;
    private double currentBalance;
    private String selectedItemId;

    public VendingMachine() {
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        dispensingState = new DispensingState(this);
        currentState = idleState;
        inventory = new Inventory();
    }

    public void insertMoney(double amount) {
        currentState.insertMoney(amount);
    }

    public void selectItem(String itemId) {
        currentState.selectItem(itemId);
    }

    public void dispense() {
        currentState.dispense();
    }

    public void cancel() {
        currentState.cancel();
    }

    // Getters and Setters
    public void setState(VendingState state) { this.currentState = state; }
    public void setCurrentBalance(double balance) { this.currentBalance = balance; }
    public void setSelectedItemId(String id) { this.selectedItemId = id; }
    public VendingState getIdleState() { return idleState; }
    public VendingState getHasMoneyState() { return hasMoneyState; }
    public VendingState getDispensingState() { return dispensingState; }
    public Inventory getInventory() { return inventory; }
    public double getCurrentBalance() { return currentBalance; }
    public String getSelectedItemId() { return selectedItemId; }
}

// Demo: Main.java
public class StatePattern {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        machine.getInventory().addItem(new Item("A1", "Coke", 1.5), 5);
        machine.getInventory().addItem(new Item("B1", "Pepsi", 1.75), 3);
        machine.getInventory().displayItems();

        machine.insertMoney(2.0);
        machine.selectItem("A1");

        machine.insertMoney(1.0);
        machine.cancel();
    }
}

