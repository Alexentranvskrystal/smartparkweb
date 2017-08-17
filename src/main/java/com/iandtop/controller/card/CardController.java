package com.iandtop.controller.card;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iandtop.model.card.*;
import com.iandtop.model.statuscodeconstant.StatusCodeConstants;
import com.iandtop.service.card.CardService;
import com.iandtop.utils.APIRestResponse;
import com.iandtop.utils.ResponseUtils;
import com.iandtop.utils.RestOperateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lz on 2017/5/4.
 */

@Controller
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService service;

    @ResponseBody
    @RequestMapping(value="/retrieve")
    public String retrieve(CardModel vo, HttpServletRequest request) {

        List<CardModel> result = service.retrieveAllWithPage(vo);

        List<CardModel> resultAll = service.retrieveAllWithPageCount(vo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",resultAll.size());
        jsonObject.put("rows",JSONArray.toJSON(result));

        return JSONArray.toJSON(jsonObject).toString();
    }

    @ResponseBody
    @RequestMapping("/issue")
    public APIRestResponse issue(@RequestBody CardModel vo) throws Exception{
        return service.insertByVO(vo);
    }

    @ResponseBody
    @RequestMapping("/charge")
    public APIRestResponse charge(@RequestBody CardChargeRecordModel vo) throws Exception{
        Integer num = service.chargeByVO(vo);
        if(StatusCodeConstants.Success == num){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.INSERT_DATA);
        }else{
            return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
        }

    }

    @ResponseBody
    @RequestMapping("/refund")
    public APIRestResponse refund(@RequestBody CardRefundRecordModel vo) throws Exception{
        Integer num = service.refundByVO(vo);
        if(StatusCodeConstants.Success == num){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.INSERT_DATA);
        }else{
            return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
        }

    }

    @ResponseBody
    @RequestMapping("/batchcharge")
    public APIRestResponse batchcharge(@RequestBody List<CardChargeRecordModel> vos) throws Exception{
        Integer num;
        for (CardChargeRecordModel vo : vos) {
            num = service.chargeByVO(vo);
            if(num == StatusCodeConstants.Fail){
                return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
            }
        }
        return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.INSERT_DATA);

    }

    @ResponseBody
    @RequestMapping("/lost")
    public APIRestResponse lost(@RequestBody CardLostRecordModel vo) throws Exception{

        Integer result = service.lostCard(vo);

        if(result == StatusCodeConstants.Success){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.UPDATE_DATA);
        }
        return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.UPDATE_DATA);
    }

    @ResponseBody
    @RequestMapping("/return")
    public APIRestResponse returncard(@RequestBody List<CardChangeRecordModel> vos) throws Exception{
        Integer result = service.returnCard(vos);

        if(StatusCodeConstants.Success == result){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.SAVE_DATA);
        }
        return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.SAVE_DATA);
    }

    @ResponseBody
    @RequestMapping("/change")
    public APIRestResponse change(@RequestBody CardChangeRecordModel vo) throws Exception{
        Integer result = service.changeCard(vo);

        if(StatusCodeConstants.Success == result){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.SAVE_DATA);
        }
        return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.SAVE_DATA);
    }
    @ResponseBody
    @RequestMapping("/updatePwd")
    public APIRestResponse updatePwd(CardModel vo,HttpServletResponse response) throws Exception{
        Integer result = service.updatePwd(vo);
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(result == StatusCodeConstants.Success){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.UPDATE_DATA);
        }
        return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.UPDATE_DATA);
    }

}
