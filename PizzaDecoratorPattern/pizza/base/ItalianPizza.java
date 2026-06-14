package pizza.base;

import pizza.Pizza;

public class ItalianPizza implements Pizza {
    private static final int ITALIAN_PIZZA_COST = 120;

    @Override
    public int getCost() {
        return ITALIAN_PIZZA_COST;
    }

    @Override
    public String getDescription() {
        return "Italian interfaces.Pizza";
    }
}
