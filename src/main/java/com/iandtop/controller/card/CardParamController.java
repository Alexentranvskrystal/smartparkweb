package com.iandtop.controller.card;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iandtop.model.card.CardParamModel;
import com.iandtop.service.card.CardParamService;
import com.iandtop.utils.APIRestResponse;
import com.iandtop.utils.ResponseUtils;
import com.iandtop.utils.RestOperateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by lz on 2017/5/4.
 */

@Controller
@RequestMapping("/cardparam")
public class CardParamController {

    @Autowired
    private CardParamService service;

    @ResponseBody
    @RequestMapping(value="/retrieve")
    public String retrieve(CardParamModel vo, HttpServletRequest request) {

        List<CardParamModel> result = service.retrieveAllWithPage(vo);

        List<CardParamModel> resultAll = service.retrieveAll();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",resultAll.size());
        jsonObject.put("rows",JSONArray.toJSON(result));

        return JSONArray.toJSON(jsonObject).toString();
    }

    @ResponseBody
    @RequestMapping(value="/retrieveall")
    public APIRestResponse retrieveall(CardParamModel vo, HttpServletRequest request) {

        List<CardParamModel> resultAll = service.retrieveAll();

        return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.GET_DATA,resultAll.get(0));
    }

    @ResponseBody
    @RequestMapping("/save")
    public APIRestResponse save(@RequestBody CardParamModel vo) throws Exception{

        Integer num = 0;
        if(vo !=null){

            if(vo.getPk_card_param() != null && vo.getPk_card_param().trim() != ""){//如果前端传递过来pk,则判断为更新操作
                num = service.updateByPk(vo);
            }else{
                num = service.insertByMo(vo);
            }

            if(num>=1){
                return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.UPDATE_DATA);
            }else{
                return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.UPDATE_DATA);
            }
        }else{
            return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.UPDATE_DATA,"参数不可为null");
        }
    }

    @ResponseBody
    @RequestMapping("/delete")
    public APIRestResponse delete(@RequestBody List<String> pks) throws Exception{
        Integer num = service.deleteByPks(pks);
        if(num>=1){
            return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.DELETE_DATA);
        }else{
            return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.DELETE_DATA);
        }

    }

}
