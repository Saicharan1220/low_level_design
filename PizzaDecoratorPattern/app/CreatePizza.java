package app;

import decorator.CheeseDecorator;
import decorator.ExtraChickenDecorator;
import decorator.MushroomDecorator;
import pizza.Pizza;
import pizza.base.ItalianPizza;
import pizza.base.MexicanPizza;

public class CreatePizza {
    public static void main(String[] args) {
        // Decorators can be composed in any order without creating subclasses like
        // MexicanCheeseChickenPizza, ItalianDoubleCheeseChickenPizza, etc.
        Pizza mexicanNonVegPizza = new ExtraChickenDecorator(new CheeseDecorator(new MexicanPizza()));
        printReceipt(mexicanNonVegPizza);

        Pizza italianNonVegPizzaExtraCheese = new MushroomDecorator(
                new CheeseDecorator(new ExtraChickenDecorator(new CheeseDecorator(new ItalianPizza())))
        );
        printReceipt(italianNonVegPizzaExtraCheese);
    }

    private static void printReceipt(Pizza pizza) {
        System.out.println("Order: " + pizza.getDescription());
        System.out.println("Cost : Rs. " + pizza.getCost());
        System.out.println();
    }
}
