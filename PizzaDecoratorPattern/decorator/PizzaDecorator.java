package decorator;

import pizza.Pizza;

import java.util.Objects;

public abstract class PizzaDecorator implements Pizza {
    // Decorator keeps a reference to another interfaces.Pizza and adds behavior around it.
    private final Pizza pizza;

    protected PizzaDecorator(Pizza pizza) {
        this.pizza = Objects.requireNonNull(pizza, "pizza cannot be null");
    }

    protected Pizza getPizza() {
        return pizza;
    }
}
