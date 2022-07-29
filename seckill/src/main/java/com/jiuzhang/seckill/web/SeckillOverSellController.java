package com.jiuzhang.seckill.web;

import com.jiuzhang.seckill.services.SeckillActivityService;
import com.jiuzhang.seckill.services.SeckillOverSellService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class SeckillOverSellController {
    @Resource
    private SeckillOverSellService seckillOverSellService;

    @Resource
    private SeckillActivityService seckillActivityService;

//    /** * 简单 处理抢购请求 * @param seckillActivityId * @return */
//    @ResponseBody
//    @RequestMapping("/seckill/{seckillActivityId}")
//    public String seckill(
//            @PathVariable long seckillActivityId
//    ){
//        return seckillOverSellService.processSeckill(seckillActivityId);
//    }

    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckillCommodity(@PathVariable long seckillActivityId) {
        boolean stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
        return stockValidateResult ? "Congratulations! Seckill successully" : "The item has sold out.";

    }

}
