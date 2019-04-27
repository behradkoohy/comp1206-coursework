package comp1206.sushi.comms;

import comp1206.sushi.common.Dish;

public class RemoveDish {


    public Dish dish;

    public RemoveDish(){}

    public RemoveDish(Dish dish){
        this.dish = dish;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }


}
