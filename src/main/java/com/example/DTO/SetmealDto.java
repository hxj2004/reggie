package com.example.DTO;

import com.example.POJO.Setmeal;
import com.example.POJO.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
