package com.loger.controller;

import com.loger.model.Loger;
import com.loger.service.LogerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/log")
@EnableAutoConfiguration
@Api(tags = "Log", description = "日志操作")
public class LogerController {
    private final LogerService logerService;

    public LogerController(LogerService logerService){
        this.logerService = logerService;
    }

    @ApiOperation(value = "添加日志记录")
    @RequestMapping(path = "/addLog")
    @CrossOrigin
    public void addLog(@RequestParam("account") String account, @RequestParam("detail")String detail) {
        logerService.addLog(account, detail);
    }

    @ApiOperation(value = "获取所有日志记录")
    @RequestMapping(path = "/getLog", method = RequestMethod.GET)
    @CrossOrigin
    public List<Loger> getLog(){
        return logerService.get_all();
    }

    @ApiOperation(value = "获取特定日期范围日志记录")
    @RequestMapping(path = "/getLogByDate", method = RequestMethod.GET)
    @CrossOrigin
    public List<Loger> getLogByDate(@RequestParam("start")String start,@RequestParam("end")String end){
        return logerService.get_by_date(start,end);
    }

    @ApiOperation(value = "删除一条日志记录")
    @RequestMapping(path = "/deleteLog", method = RequestMethod.POST)
    @CrossOrigin
    public void deleteLog(@RequestParam("time")String time){
        logerService.delete_log(time);
    }

    @ApiOperation(value = "删除所有日志记录")
    @RequestMapping(path = "/clearLog", method = RequestMethod.POST)
    @CrossOrigin
    public void clearLog(){
        logerService.clear_log();
    }
}
