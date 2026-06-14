package pizza.base;

import pizza.Pizza;

public class MexicanPizza implements Pizza {
    private static final int MEXICAN_PIZZA_COST = 100;

    @Override
    public int getCost() {
        return MEXICAN_PIZZA_COST;
    }

    @Override
    public String getDescription() {
        return "Mexican interfaces.Pizza";
    }
}
