package com.corroborator.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CustomerController extends BaseController {
//	public static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
	public CustomerController() {}
	
	
	@RequestMapping(method = RequestMethod.GET, value="/customer/customers")
	@ResponseBody
	  public String getAllCustomers() {
	  return "You will get the list of customers shortly...!!";
	 }
	
}
