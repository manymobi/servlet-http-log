package com.manymobi.servlethttplogdemo;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 梁建军
 * 创建日期： 2022/5/14
 * 创建时间： 下午9:57
 * @version 1.0
 * @since 1.0
 */
@RestController
public class TestController{

    @RequestMapping("/api/test")
    public Map<String,String> d(@RequestBody Map<String,String> map0){
        HashMap<String, String> map = new HashMap<>();
        map.put("key","value");
        map.putAll(map0);
        return map;
    }
}
