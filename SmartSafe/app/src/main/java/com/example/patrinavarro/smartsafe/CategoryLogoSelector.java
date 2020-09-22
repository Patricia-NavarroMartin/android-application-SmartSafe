package com.example.patrinavarro.smartsafe;

import android.content.Context;

/**
 * Created by Patri Navarro on 05/05/2018.
 */

public class CategoryLogoSelector {
    Context context;

    public CategoryLogoSelector(Context context) {
        this.context = context;
    }

    public int getImageId(String category_txt)
    {
        String category_logo = "abstract_logo_azul";
        switch (category_txt) {
            case "Leisure":
                category_logo = "dice_v2";
                break;
            case "Other":
                category_logo = "stack_v2";
                break;
            case "Shopping":
                category_logo = "cart_v2";
                break;
            case "Travel":
                category_logo = "travel_v2";
                break;
            case "Personal":
                category_logo = "heart_v2";
                break;
        }
        return context.getResources().getIdentifier(category_logo, "drawable", context.getPackageName());

    }
}
