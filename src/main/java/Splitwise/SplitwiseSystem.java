package Splitwise;

import java.util.*;

// USER CLASS
class User {
    String id, name, email, phone;

    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}

// SPLIT CLASSES
abstract class Split {
    User user;
    double amount;

    public Split(User user) {
        this.user = user;
    }
}

class EqualSplit extends Split {
    public EqualSplit(User user) {
        super(user);
    }
}

class ExactSplit extends Split {
    public ExactSplit(User user, double amount) {
        super(user);
        this.amount = amount;
    }
}

class PercentSplit extends Split {
    double percent;

    public PercentSplit(User user, double percent) {
        super(user);
        this.percent = percent;
    }
}

// EXPENSE CLASSES
abstract class Expense {
    double totalAmount;
    User paidBy;
    List<Split> splits;
    String description;

    public Expense(double totalAmount, User paidBy, List<Split> splits, String description) {
        this.totalAmount = totalAmount;
        this.paidBy = paidBy;
        this.splits = splits;
        this.description = description;
    }

    public abstract boolean isValid();
}

class EqualExpense extends Expense {
    public EqualExpense(double amount, User paidBy, List<Split> splits, String description) {
        super(amount, paidBy, splits, description);
        double share = Math.round((amount / splits.size()) * 100.0) / 100.0;
        for (Split s : splits) {
            s.amount = share;
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

class ExactExpense extends Expense {
    public ExactExpense(double amount, User paidBy, List<Split> splits, String description) {
        super(amount, paidBy, splits, description);
    }

    @Override
    public boolean isValid() {
        double total = 0;
        for (Split s : splits) total += s.amount;
        return Math.abs(total - totalAmount) < 0.01;
    }
}

class PercentExpense extends Expense {
    public PercentExpense(double amount, User paidBy, List<Split> splits, String description) {
        super(amount, paidBy, splits, description);
        for (Split s : splits) {
            PercentSplit ps = (PercentSplit) s;
            s.amount = Math.round((amount * ps.percent / 100.0) * 100.0) / 100.0;
        }
    }

    @Override
    public boolean isValid() {
        double totalPercent = 0;
        for (Split s : splits) {
            totalPercent += ((PercentSplit) s).percent;
        }
        return Math.abs(totalPercent - 100.0) < 0.01;
    }
}

// EXPENSE MANAGER
class ExpenseManager {
    Map<String, User> users = new HashMap<>();
    Map<String, Map<String, Double>> balances = new HashMap<>();

    public void addUser(User user) {
        users.put(user.id, user);
        balances.put(user.id, new HashMap<>());
    }

    public void addExpense(Expense expense) {
        if (!expense.isValid()) {
            System.out.println("❌ Invalid Splitwise.Expense");
            return;
        }

        String payer = expense.paidBy.id;

        for (Split split : expense.splits) {
            String payee = split.user.id;
            if (!payer.equals(payee)) {
                double amount = split.amount;

                // Step 1: Get existing balance for payee -> payer
                Map<String, Double> payeeBalances = balances.get(payee);
                double payeeToPayerAmount = payeeBalances.getOrDefault(payer, 0.0);

                // Step 2: Update balance (payee owes payer more now)
                payeeToPayerAmount += amount;
                payeeBalances.put(payer, payeeToPayerAmount);

                // Step 3: Get existing balance for payer -> payee
                Map<String, Double> payerBalances = balances.get(payer);
                double payerToPayeeAmount = payerBalances.getOrDefault(payee, 0.0);

                // Step 4: Update balance (payer is owed more by payee)
                payerToPayeeAmount -= amount;
                payerBalances.put(payee, payerToPayeeAmount);
            }
        }
    }

    public void showBalances() {
        for (String user1 : balances.keySet()) {
            for (String user2 : balances.get(user1).keySet()) {
                double amount = balances.get(user1).get(user2);
                if (amount > 0) {
                    System.out.println(users.get(user1).name + " owes " +
                            users.get(user2).name + ": Rs " + amount);
                }
            }
        }
    }
}

// MAIN METHOD
public class SplitwiseSystem {
    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();

        // Create users
        User u1 = new User("u1", "Alice", "alice@mail.com", "123");
        User u2 = new User("u2", "Bob", "bob@mail.com", "456");
        User u3 = new User("u3", "Charlie", "charlie@mail.com", "789");

        manager.addUser(u1);
        manager.addUser(u2);
        manager.addUser(u3);

        // Equal Splitwise.Split
        List<Split> dinnerSplits = Arrays.asList(new EqualSplit(u1), new EqualSplit(u2), new EqualSplit(u3));
        manager.addExpense(new EqualExpense(300, u1, dinnerSplits, "Dinner"));

        // Exact Splitwise.Split
        List<Split> snacksSplits = Arrays.asList(new ExactSplit(u2, 70), new ExactSplit(u3, 30));
        manager.addExpense(new ExactExpense(100, u1, snacksSplits, "Snacks"));

        // Percent Splitwise.Split
        List<Split> taxiSplits = Arrays.asList(new PercentSplit(u1, 40), new PercentSplit(u2, 60));
        manager.addExpense(new PercentExpense(200, u3, taxiSplits, "Taxi"));

        // Display balances
        manager.showBalances();
    }
}
