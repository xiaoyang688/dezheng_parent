package com.dezheng.controller.index;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.service.business.AdService;
import com.dezheng.utils.DateUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Reference
    private AdService adService;

    @GetMapping("/business")
    public Map business(String position){
        return adService.findByPosition(position);
    }

    @GetMapping("/getTimeMenu")
    public List<Date> getTimeMenu(){
        return DateUtil.getDateMenus();
    }

}
