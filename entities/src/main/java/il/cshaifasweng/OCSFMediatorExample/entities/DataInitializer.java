package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.Session;


public class DataInitializer {

    public static void initializeData(Session session) {
        // יצירת פריטים עבור התפריט
        MenuItem item1 = new MenuItem();
        item1.setName("Salad");
        item1.setPrice(35.00);
        item1.setIngredients("Tomatoes, cucumbers, lettuce");
        item1.setPreference("Low calorie");

        MenuItem item2 = new MenuItem();
        item2.setName("Pizza");
        item2.setPrice(55.00);
        item2.setIngredients("Mushrooms, onions, tomatoes");
        item2.setPreference("Includes vegan option");

        MenuItem item3 = new MenuItem();
        item3.setName("Pasta");
        item3.setPrice(60.00);
        item3.setIngredients("Mushroom cream sauce");
        item3.setPreference("Available gluten-free");

        MenuItem item4 = new MenuItem();
        item4.setName("Hamburger");
        item4.setPrice(80.00);
        item4.setIngredients("Meatball, pickle, tomato, lettuce");
        item4.setPreference("Choice of meat or plant-based");

        MenuItem item5 = new MenuItem();
        item5.setName("Edamame");
        item5.setPrice(30.00);
        item5.setIngredients("Edamame");
        item5.setPreference("Served with sea salt");

        // שמירת הפריטים במסד הנתונים
        session.save(item1);
        session.save(item2);
        session.save(item3);
        session.save(item4);
        session.save(item5);

        System.out.println("Data initialization completed!");
    }
}

