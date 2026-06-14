package pizza;

public interface Pizza {
    // Component: common contract for both base pizzas and decorated pizzas.
    int getCost();

    String getDescription();
}
