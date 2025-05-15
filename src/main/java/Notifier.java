import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A demonstration of Observer Pattern
 */


interface Observer {
    void notify(String msg);
}

@AllArgsConstructor
class SmsSubscriber implements Observer {

    private String name;

    @Override
    public void notify(String msg) {
        System.out.println("User: " + name + " received sms: " + msg);
    }
}

@AllArgsConstructor
class EmailSubscriber implements Observer {

    private String name;

    @Override
    public void notify(String msg) {
        System.out.println("User: " + name + " received email: " + msg);
    }
}

class NewsAgency {
    private final List<Observer> observers;

    NewsAgency() {
        this.observers = new ArrayList<>();
    }

    public void subscribe(Observer o) {
        observers.add(o);
    }

    public void unsubscribe(Observer o) {
        observers.remove(o);
    }

    public void publish(String msg) {
        observers.forEach(observer -> observer.notify(msg));
    }

}

public class Notifier {

    public static void main(String[] args) {
        NewsAgency agency = new NewsAgency();

        Observer emailSubscriber = new EmailSubscriber("Alice");
        Observer smsSubscriber = new SmsSubscriber("Bob");

        agency.subscribe(emailSubscriber);
        agency.subscribe(smsSubscriber);

        agency.publish("Observer Pattern Implemented!");
    }

}
