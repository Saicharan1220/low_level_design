package decorator;

import pizza.Pizza;

public class CheeseDecorator extends PizzaDecorator {
    private static final int CHEESE_COST = 10;

    public CheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int getCost() {
        return getPizza().getCost() + CHEESE_COST;
    }

    @Override
    public String getDescription() {
        return getPizza().getDescription() + " + Extra Cheese";
    }
}
