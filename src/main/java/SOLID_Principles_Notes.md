
# 🧱 SOLID Principles in Object-Oriented Design

**SOLID** is an acronym representing five design principles intended to make software designs more understandable, flexible, and maintainable.

---

## 🔹 S – Single Responsibility Principle (SRP)

> A class should have **one and only one reason to change**.

### ❌ Bad Example

```java
class User {
    void saveToDb() { /* ... */ }
    void sendEmail() { /* ... */ }
}
```

This class handles persistence and communication — two reasons to change.

### ✅ Good Example

```java
class User {
    String name;
}

class UserRepository {
    void save(User user) { /* ... */ }
}

class EmailService {
    void sendWelcomeEmail(User user) { /* ... */ }
}
```

---

## 🔹 O – Open/Closed Principle (OCP)

> **Software entities should be open for extension but closed for modification.**

### ❌ Bad Example

```java
class InvoicePrinter {
    void print(String format) {
        if (format.equals("PDF")) { /* ... */ }
        else if (format.equals("HTML")) { /* ... */ }
    }
}
```

Every new format requires modifying `print()`.

### ✅ Good Example

```java
interface InvoiceFormatter {
    void format();
}

class PDFFormatter implements InvoiceFormatter {
    public void format() { /* ... */ }
}

class HTMLFormatter implements InvoiceFormatter {
    public void format() { /* ... */ }
}

class InvoicePrinter {
    void print(InvoiceFormatter formatter) {
        formatter.format();
    }
}
```

---

## 🔹 L – Liskov Substitution Principle (LSP)

> **Subtypes must be substitutable for their base types without altering the correctness.**

### ❌ Bad Example

```java
class Bird {
    void fly() {}
}

class Ostrich extends Bird {
    void fly() {
        throw new UnsupportedOperationException(); // violates LSP
    }
}
```

### ✅ Good Example

```java
interface Bird {}

interface FlyingBird extends Bird {
    void fly();
}

class Sparrow implements FlyingBird {
    public void fly() { /* ... */ }
}

class Ostrich implements Bird {
    // Doesn’t fly, so it’s not a FlyingBird
}
```

---

## 🔹 I – Interface Segregation Principle (ISP)

> **Clients should not be forced to depend on interfaces they do not use.**

### ❌ Bad Example

```java
interface Worker {
    void work();
    void eat();
}

class Robot implements Worker {
    public void work() {}
    public void eat() {} // irrelevant for a robot
}
```

### ✅ Good Example

```java
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

class Human implements Workable, Eatable {
    public void work() {}
    public void eat() {}
}

class Robot implements Workable {
    public void work() {}
}
```

---

## 🔹 D – Dependency Inversion Principle (DIP)

> **High-level modules should not depend on low-level modules. Both should depend on abstractions.**

### ❌ Bad Example

```java
class MySQLDatabase {
    void connect() { }
}

class UserService {
    MySQLDatabase db = new MySQLDatabase(); // tightly coupled
}
```

### ✅ Good Example

```java
interface Database {
    void connect();
}

class MySQLDatabase implements Database {
    public void connect() { }
}

class UserService {
    private final Database db;

    UserService(Database db) {
        this.db = db;
    }

    void register() {
        db.connect();
    }
}
```

---

## 💡 Bonus: Strategy Pattern Preserves OCP

Instead of writing logic like:

```java
if (paymentMode.equals("UPI")) { ... }
else if (paymentMode.equals("CARD")) { ... }
```

Use the **Strategy Pattern**:

```java
interface PaymentStrategy {
    void pay(double amount);
}

class UpiPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid using UPI: " + amount);
    }
}

class CardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid using Card: " + amount);
    }
}

class PaymentService {
    void makePayment(PaymentStrategy strategy) {
        strategy.pay(100);
    }
}
```

Now, adding new payment modes requires **no change** to `PaymentService`.
