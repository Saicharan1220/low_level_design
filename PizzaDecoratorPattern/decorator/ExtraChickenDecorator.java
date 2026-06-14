package decorator;

import pizza.Pizza;

public class ExtraChickenDecorator extends PizzaDecorator {
    private static final int EXTRA_CHICKEN_COST = 50;

    public ExtraChickenDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int getCost() {
        return getPizza().getCost() + EXTRA_CHICKEN_COST;
    }

    @Override
    public String getDescription() {
        return getPizza().getDescription() + " + Extra Chicken";
    }
}
