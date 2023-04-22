package com.example.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.DTO.DishDto;
import com.example.DTO.SetmealDto;
import com.example.POJO.*;
import com.example.Service.CategoryService;
import com.example.Service.DishFlavorService;
import com.example.Service.DishService;
import com.example.Service.ShoppingCartService;
import com.example.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/dish")
@RestController
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("添加{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        //删除对于分类缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        log.info("分类查询");
        //原理，把所有的dish数据弄出来，分装到dishdto，然后再通过dishid查出所有分类，把所有分类添加到dishdto里面，然后返回
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(name != null, Dish::getName,name);
        //排序规则
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);
        //把pageInfo的分页数据考到dishDtoPage里面去
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取到所有的分页数据
        List<Dish> records = pageInfo.getRecords();
        //对dish数据变化
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //把item的数据考到dishDto里面
            BeanUtils.copyProperties(item,dishDto);
            //获取到id
            Long categoryId = item.getCategoryId();
            //根据id查询分类
            Category category = categoryService.getById(categoryId);
            //获取到分类名称
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            //返回新结果
            return dishDto;
        }).collect(Collectors.toList());


        //赋值
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("根据id查询{}",id);
        return R.success(dishService.getByIdWidthFlavor(id));
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改{}",dishDto);
        dishService.updateWithFlavor(dishDto);
        //删除对于分类缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        log.info("获取菜品");

        //获取菜品，菜品条件器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品分类id来查找对应菜品
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //只获取在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询
        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }
*/
   @GetMapping("/list")
   public R<List<DishDto>> list(Dish dish){
       log.info("获取菜品");
       List<DishDto> dishDtoList = null;

       //获取redis中对应的分类是否存在
       //设置key值来，从redis中查询：categoryid为分类id，status为是否停售
       String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

       //转换为对应的数据
       dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);

       //如果存在就直接把数据返回
       if(dishDtoList != null){
           return R.success(dishDtoList);
       }
       //获取菜品，菜品条件器
       LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
       //根据菜品分类id来查找对应菜品
       queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
       //只获取在售状态的菜品
       queryWrapper.eq(Dish::getStatus, 1);
       //排序条件
       queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
       //查询
       List<Dish> list = dishService.list(queryWrapper);

       dishDtoList = list.stream().map((item) -> {
           DishDto dishDto = new DishDto();
           //把item的数据考到dishDto里面
           BeanUtils.copyProperties(item,dishDto);
           //获取到id
           Long categoryId = item.getCategoryId();
           //根据id查询分类
           Category category = categoryService.getById(categoryId);
           //获取到分类名称
           if(category != null){
               String categoryName = category.getName();
               dishDto.setCategoryName(categoryName);
           }

           Long disId = item.getId();
           LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
           dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,disId);
           List<DishFlavor> dishFlavorslist = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
           dishDto.setFlavors(dishFlavorslist);
           //返回新结果
           return dishDto;
       }).collect(Collectors.toList());
       //如果不存在，把数据查出来，加入到缓存，并返回数据

       redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

       return R.success(dishDtoList);
   }






}
