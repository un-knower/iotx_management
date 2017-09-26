package com.anosi.asset.controller;

import java.io.IOException;

import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.anosi.asset.exception.CustomRunTimeException;

@RestController
public class GlobalController<T> {
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(Exception ex) throws IOException {
		ex.printStackTrace();
		//如果抛出的是自定义的异常
		if(ex instanceof CustomRunTimeException){
			ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
			String message = ex.getMessage();
			mv.addObject("result", "error");
			mv.addObject("message", message);
			return mv;
		}else if(ex instanceof UnauthenticatedException){
			//没有进行认证
			ModelAndView mv =  new ModelAndView("redirect:/login");
			return mv;
		}else{
			ModelAndView mv =  new ModelAndView("error");
			StringBuilder sb = new StringBuilder();
			StackTraceElement[] stackTrace = ex.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append(stackTraceElement+"\n");
			}
			mv.addObject("message", sb.toString());
			return mv;
		}
	}

}
