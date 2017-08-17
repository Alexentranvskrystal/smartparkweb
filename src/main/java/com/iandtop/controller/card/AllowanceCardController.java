package com.iandtop.controller.card;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.iandtop.service.card.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iandtop.model.card.CardChargeRecordModel;
import com.iandtop.model.card.CardModel;
import com.iandtop.model.meal.MealAllowanceModel;
import com.iandtop.model.meal.MealAllowanceNumModel;
import com.iandtop.model.statuscodeconstant.StatusCodeConstants;
import com.iandtop.service.card.AllowanceCardService;
import com.iandtop.utils.APIRestResponse;
import com.iandtop.utils.ResponseUtils;
import com.iandtop.utils.RestOperateCode;
import com.iandtop.utils.excel.ExcelDataFormatter;
import com.iandtop.utils.excel.ExcelUtils;
import com.iandtop.utils.excel.MealAllowanceExcel;

@Controller
	@RequestMapping("/AllowanceCard")
	public class AllowanceCardController {

	    @Autowired
	    private AllowanceCardService service;
		@Autowired
		private CardService cardService;
	    // 根据部门查询查询状态为10的人
	    @ResponseBody
	    @RequestMapping(value="/retrieve")
	    public String retrieve(CardModel vo, HttpServletRequest request) {

	        List<CardModel> result = service.retrieveAllWithPage(vo);

	       /// List<CardModel> resultAll = service.retrieveAllWithPageCount(vo);

	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("total",9999);
	        jsonObject.put("rows",JSONArray.toJSON(result));

	        return JSONArray.toJSON(jsonObject).toString();
	    }
	  //补贴发放方法
	    @ResponseBody
	    @RequestMapping("/butieSend")
	    public APIRestResponse butieSend(@RequestParam(value="ids",required=false) String ids,
	    		@RequestParam(value="operator",required=false) String operator) throws Exception{
	        //前台获取选中的补贴批次ID,如果没有直接报错，不要往下走了
	    	//格式为为字符串，逗号拼接的形式:118,119,120
	    	if(ids==null || "".equals(ids)){
	        	 return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.SAVE_DATA);
	        }
	    	Integer num;
	        //补贴发放service
	    	num = service.allowanceSend(ids,operator);
	        if(num == StatusCodeConstants.Fail){
                return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
            }
	        return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.INSERT_DATA);

	    }
	    //补贴登记方法
	    @ResponseBody
	    @RequestMapping("/butiecharge")
	    public APIRestResponse batchcharge(@RequestBody List<CardChargeRecordModel> vos) throws Exception{
	        Integer num;
	        //插入补贴头表记录//插入补贴明细表记录
	        num = service.insertMealAllowanceNum(vos);
	        if(num == StatusCodeConstants.Fail){
                return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
            }

	        return ResponseUtils.getSuccessAPI(true,"true", RestOperateCode.INSERT_DATA);

	    }
	    //查询头部表格
	    @ResponseBody
	    @RequestMapping(value="/queryMainData")
	    public String queryMainData(MealAllowanceNumModel vo, HttpServletRequest request) {


	        List<MealAllowanceNumModel> result = service.queryMainData(vo);

	      //  List<CardModel> resultAll = service.queryMainDataCount(vo);

	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("total",9999);
	        jsonObject.put("rows",JSONArray.toJSON(result));

	        return JSONArray.toJSON(jsonObject).toString();
	    }
	    
	    //查询明细表格
	    @ResponseBody
	    @RequestMapping(value="/queryDeatilData")
	    public String queryDeatilData(MealAllowanceModel vo, HttpServletRequest request) {

	        List<MealAllowanceModel> result = service.queryDeatilData(vo);

	       // List<CardModel> resultAll = service.queryDeatilDataCount(vo);

	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("total",9999);
	        jsonObject.put("rows",JSONArray.toJSON(result));

	        return JSONArray.toJSON(jsonObject).toString();
	    }
	    @ResponseBody
	    @RequestMapping("/deleteRecord")
	    public APIRestResponse deleteRecord(@RequestParam(value="ids",required=false) String ids) throws Exception{
		    	if(ids==null || "".equals(ids)){
		        	 return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.SAVE_DATA);
		        }
		    	Integer num;
		        //补贴发放service
		    	num = service.deleteRecord(ids);
		        if(num == 0){
	               return ResponseUtils.getSuccessAPI(false,"false", RestOperateCode.INSERT_DATA);
	           }
		        return ResponseUtils.getSuccessAPI(true,num.toString(), RestOperateCode.INSERT_DATA);

	    }
	    //导入excel的方法
	    @ResponseBody
	    @RequestMapping("ULE")
	    public Object ule(@RequestParam MultipartFile file,@RequestParam(value="operator",required=false) String operator, HttpServletRequest request) {
	        int status = 0;
			int num=0;//用来记录excel表格里的总记录数
			int m=0;//用来记录成功数
	        ExcelDataFormatter edf = new ExcelDataFormatter();
	        File newFile = null;
	        try {
	            String name = file.getOriginalFilename();
	            String rootPath = request.getServletContext().getRealPath("/");

	            //获取项目路径，创建临时文件
	            //windows下
	            if ("\\".equals(File.separator)) {
	                newFile = new File(rootPath + "\\" + name);
	            }
	            //linux下
	            if ("/".equals(File.separator)) {
	                newFile = new File(rootPath + "/" + name);
	            }
	            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
	            stream.write(file.getBytes());
	            stream.close();

				//读取Excel文件并解析
				List<MealAllowanceExcel> vos = new ExcelUtils<MealAllowanceExcel>(new MealAllowanceExcel()).readFromFile(edf, newFile);
				//MealAllowanceNumModel numModel = new MealAllowanceNumModel();
				long batch_num_id = service.insertBatchNum(operator);
				for (MealAllowanceExcel tmp : vos) {
					num++;
//	                pkCard;//员工卡号ID moneyAllowance  allowanceType
					//查出卡号所对应的卡
					String str = tmp.getPkCard();
					int endIndex = str.indexOf(".");
					String pk_code = endIndex>0?str.substring(0, endIndex):str;
					CardModel cd=cardService.selectByPkStaff(pk_code);
					//如果存在的话，卡对象的pk_card,pk_staff设置到model对象中
					if(cd!=null){
						m++;
						MealAllowanceModel model = new MealAllowanceModel();
						//1:0
						model.setAllowance_type(tmp.getAllowanceType().indexOf("累加") > 0 ? MealAllowanceModel.ALLOWANCE_TYPE_MEALALLOWANCE_ADD : MealAllowanceModel.ALLOWANCE_TYPE_MEALALLOWANCE_CLEAR);
						model.setMoney_allowance(Double.parseDouble(tmp.getMoneyAllowance()));
						model.setPk_card(cd.getPk_card());
						model.setPk_staff(Integer.parseInt(cd.getPk_staff()));
						model.setOperator(operator);
						status += service.save(model,batch_num_id);
					}
				}

	        } catch (Exception e) {
	            e.printStackTrace();

	            //删除临时文件
	            if (newFile != null) {
	                newFile.delete();
	            }

	            if (newFile != null) {
	                newFile.delete();
	            }
	        }
	        return ResponseUtils.getSuccessAPI(status > 0 ? true : false, m+"", (num-m)+"");

	    }
	  
	}