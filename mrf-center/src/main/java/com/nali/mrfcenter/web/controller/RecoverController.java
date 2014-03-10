package com.nali.mrfcenter.web.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nali.mrfcenter.service.BatchProcessResult;
import com.nali.mrfcenter.service.MRFRecoverService;
import com.nali.mrfcenter.service.ProcessResult;

@Controller
public class RecoverController {
	
	@Autowired
	private MRFRecoverService mrfRecoverService;
	
	private Logger log = LoggerFactory.getLogger(RecoverController.class);
	
	@RequestMapping("/recoverList")
	public ModelAndView recoverList() {
		ModelAndView mav = new ModelAndView("recoverList");
		return mav;
	}
	
	@RequestMapping(value="/getPageRecovers", method=RequestMethod.GET)
	public ModelAndView getPageRecovers(@RequestParam(value="pageIdx") int pageIndex, 
			@RequestParam(value="pageSize") int pageSize) {
		getLog().info("getPageRecovers: pageIndex {}, pageSize {}", pageIndex, pageSize);
		ModelAndView mav = new ModelAndView("recoverListTable");
		mav.addObject("page", mrfRecoverService.getRecoverPage(pageIndex, pageSize));
		
		return mav;
	}
	
	@RequestMapping(value="/deleteRecord", method=RequestMethod.POST)
	@ResponseBody
	public ProcessResult deleteRecord(@RequestParam(value="msgID") int msgID) {
		getLog().info("deleteRecord: msgID {}", msgID);
		if(msgID <= 0) {
			return new ProcessResult(msgID);
		}
		
		return mrfRecoverService.deleteRecoverRecord(msgID);
	}
	
	@RequestMapping(value="/batchDeleteRecords", method=RequestMethod.POST)
	@ResponseBody
	public BatchProcessResult batchDeleteRecords(@RequestParam(value="msgIDs[]") int[] msgIDs) {
		getLog().info("batchDeleteRecords: msgIDs {}", Arrays.toString(msgIDs));
		return mrfRecoverService.batchDeleteRecoverRecords(msgIDs);
	}
	
	@RequestMapping(value="/recoverRecord", method=RequestMethod.POST)
	@ResponseBody
	public ProcessResult recoverRecord(@RequestParam(value="msgID") int msgID) {
		getLog().info("recoverRecord: msgID {}", msgID);
		if(msgID <= 0) {
			return new ProcessResult(msgID);
		}
		
		return mrfRecoverService.doRecoverRecord(msgID);
	}
	
	@RequestMapping(value="/batchRecoverRecords", method=RequestMethod.POST)
	@ResponseBody
	public BatchProcessResult batchRecoverRecords(@RequestParam(value="msgIDs[]") int[] msgIDs) {
		getLog().info("batchRecoverRecords: msgIDs {}", Arrays.toString(msgIDs));
		return mrfRecoverService.doBatchRecoverRecords(msgIDs);
	}
	
 	public Logger getLog() {
		return log;
	}
	
}
