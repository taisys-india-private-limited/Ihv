package com.taisys.ihvWeb.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taisys.ihvWeb.service.SessionService;
import com.taisys.ihvWeb.jwt.JwtTokenUtil;
import com.taisys.ihvWeb.dao.PortalUserDAO;
import com.taisys.ihvWeb.service.PortalUserService;
import com.taisys.ihvWeb.util.PasswordUtil;
import com.taisys.ihvWeb.jwt.JwtAuthenticationRequest;
import com.taisys.ihvWeb.jwt.JwtUser;
import com.taisys.ihvWeb.model.PortalUser;
import com.taisys.ihvWeb.model.SessionInfo;
import com.taisys.ihvWeb.model.Captcha;
import com.taisys.ihvWeb.util.GenerateKeys;
import com.taisys.ihvWeb.model.IhvCustomer;
import com.taisys.ihvWeb.model.IhvLogin;
import com.taisys.ihvWeb.model.IhvMerchant;
import com.taisys.ihvWeb.model.IhvOTP;
import com.taisys.ihvWeb.model.IhvTransaction;
import com.taisys.ihvWeb.service.IhvOTPService;
import com.taisys.ihvWeb.service.IhvTransactionService;
import com.taisys.ihvWeb.util.CaptchaUtil;
import com.taisys.ihvWeb.util.IdGenerator;
import com.taisys.ihvWeb.util.Response;
import com.taisys.ihvWeb.util.SendSMS;
import com.taisys.ihvWeb.service.CaptchaService;
import com.taisys.ihvWeb.service.IhvCustomerService;
import com.taisys.ihvWeb.service.IhvLoginService;
import com.taisys.ihvWeb.service.IhvMerchantService;
import com.taisys.ihvWeb.model.Captcha;

@Controller
@RequestMapping("/")
public class IhvWebController {

	private static final String SEND_SMS_RESPONSE_URL = "https://sms10.routesms.com:8443/bulksms/bulksms";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	IhvLoginService loginService;

	@Autowired
	IhvCustomerService customerService;

	@Autowired
	IhvMerchantService merchantService;

	@Autowired
	IhvOTPService otpService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	IhvTransactionService transactionService;

	@Autowired
	CaptchaService captchaService;

	@Autowired
	PortalUserDAO portalUserDAO;

	@Autowired
	PortalUserService portalService;

	@Autowired
	PasswordUtil passwordUtil;

	@Autowired
	SendSMS sendSMS = new SendSMS();

	@Autowired
	CaptchaUtil captchaUtil;

	@Autowired
	GenerateKeys gk;

	@Autowired
	SessionService sessionService;

	IdGenerator idGenerator = new IdGenerator();

	private List<String> invalidParameterList;

	Response response;

	public static final Logger logger = Logger.getLogger(IhvWebController.class);

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	//// --------Ihv Customer Send OTP
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/os", method = RequestMethod.POST)
	public ResponseEntity<?> sendOTP(@RequestParam(value = "mobileNo") String mobileNo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			response = new Response();
			logger.info("**** iNside sendOTP ****");
			logger.info("Param : mobile : " + mobileNo);
			if (mobileNo.equals("9891747697")) {
				// fixed otp as 123123 for mobile number 9891747698 to bypass
				// iOS app validation
				int number = 123123;
				IhvOTP otp = new IhvOTP();
				IhvOTP existingOTP = otpService.getOTP(mobileNo);
				if (existingOTP != null) {
					existingOTP.setStatus("DISCARDED");
					otpService.updateOTP(existingOTP);
				}
				otp.setId(idGenerator.generateOtpId("OTP"));
				otp.setMobileNo(mobileNo);
				otp.setOtp(String.valueOf(number));
				otp.setStatus("UNUSED");
				otpService.createOTP(otp);
				String sendSMSContent = String.valueOf(number)
						+ " is the IHV registration one time password(OTP) for mobile: +91" + mobileNo;
				sendSMS.sendSMSResponse(sendSMSContent, mobileNo, "false", "#", "test", SEND_SMS_RESPONSE_URL);
				HashMap<String, String> otpMap = new HashMap<String, String>();
				otpMap.put("isOtpSent", "true");
				response.setIsSuccess(true);
				response.setStatus("100");
				response.setMessage("An One time password has been sent successfully on +91" + mobileNo);
				response.setResultObj(otpMap);
				logger.info(sendSMSContent);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				int number = (int) (Math.random() * ((999999 - 100000) + 1)) + 100000;
				IhvOTP otp = new IhvOTP();
				IhvOTP existingOTP = otpService.getOTP(mobileNo);
				if (existingOTP != null) {
					if (new Date().getTime() - existingOTP.getCreateTime().getTime() >= 2 * 60 * 1000) {
						// **** expire otp ****
					}
					existingOTP.setStatus("DISCARDED");
					otpService.updateOTP(existingOTP);
				}
				otp.setId(idGenerator.generateOtpId("OTP"));
				otp.setMobileNo(mobileNo);
				otp.setOtp(String.valueOf(number));
				otp.setCreateTime(new Date());
				otp.setStatus("UNUSED");
				otpService.createOTP(otp);
				String sendSMSContent = String.valueOf(number) 
						+ " is the one time password(OTP) for your Wakeepay registration.";
				sendSMS.sendSMSResponse(sendSMSContent, mobileNo, "false", "#", "test", SEND_SMS_RESPONSE_URL);
				HashMap<String, String> otpMap = new HashMap<String, String>();
				otpMap.put("isOtpSent", "true");
				response.setIsSuccess(true);
				response.setStatus("100");
				response.setMessage("An One time password has been sent successfully on +91" + mobileNo);
				response.setResultObj(otpMap);
				logger.info(sendSMSContent);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			response.setStatus("500");
			response.setMessage("Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> Login(@RequestParam(value = "email") String email,
			@RequestParam(value = "mobileNo") String mobileNo, @RequestParam(value = "password") String password) {
		HttpHeaders headers = new HttpHeaders();
		try {
			response = new Response();
			logger.info("**** iNside login ****");
			logger.info("Param : email : " + email);
			logger.info("Param : mobile : " + mobileNo);
			logger.info("Param : password : " + password);

			// IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			IhvMerchant ihvMerchant = merchantService.getMerchantEmail(email);
			IhvMerchant ihvMerchant1 = merchantService.getMerchant(mobileNo);

			// logger.info("Merchant Email" +ihvMerchant.getEmail());
			// logger.info("Merchant Pass" +ihvMerchant.getPassword());
			// logger.info("Merchant Mob" +ihvMerchant.getMobileNo());

			IhvLogin User = new IhvLogin();

			if (ihvMerchant != null) {
				if (ihvMerchant.getMobileNo().equals(mobileNo)
						|| ihvMerchant.getEmail().equals(email) && ihvMerchant.getPassword().equals(password)) {
					logger.info("Merchant LoggedIn successfully.");
					HashMap resultMap = new HashMap<String, String>();
					logger.info("Customer with respect to Mobile Number: " + mobileNo + " exists");
					// ihvCustomersMap.put("ihvCustomer", ihvCustomer);
					if (ihvMerchant.getFirstName() != null) {
						resultMap.put("firstName", ihvMerchant.getFirstName());
					} else {
						resultMap.put("firstName", "");
					}
					if (ihvMerchant.getEmail() != null) {
						resultMap.put("email", ihvMerchant.getEmail());
					} else {
						resultMap.put("email", "");
					}
					if (ihvMerchant.getMobileNo() != null) {
						resultMap.put("mobileNo", ihvMerchant.getMobileNo());
					} else {
						resultMap.put("mobileNo", "");
					}
					if (ihvMerchant.getType() != null) {
						resultMap.put("type", ihvMerchant.getType());
					} else {
						resultMap.put("type", "");
					}
					if (ihvMerchant.getStatus() != null) {
						resultMap.put("status", ihvMerchant.getStatus());
					} else {
						resultMap.put("status", "");
					}
					if (ihvMerchant.getMerchID() != null) {
						resultMap.put("MerchID", ihvMerchant.getMerchID());
					} else {
						resultMap.put("MerchID", "");
					}
					// resultMap.put("LoggedIn", "true");
					response.setResultObj(resultMap);
					response.setStatus("100");
					response.setIsSuccess(true);
					response.setMessage("Success: Merchant LoggedIn successfully.");
					User.setId(idGenerator.generateOtpId("IN"));
					User.setEmailId(email);
					User.setMobileNo(mobileNo);
					User.setPassword(password);
					User.setStatus("Logged_IN");
					User.setloginTime(new Date());
					loginService.createUser(User);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					response.setStatus("200");
					response.setMessage("Failure: Please enter the valid credentials.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else if (ihvMerchant1 != null) {
				if (ihvMerchant1.getMobileNo().equals(mobileNo) && ihvMerchant1.getPassword().equals(password)) {
					logger.info("Merchant LoggedIn successfully.");
					HashMap resultMap = new HashMap<String, String>();
					logger.info("Customer with respect to Mobile Number: " + mobileNo + " exists");
					// ihvCustomersMap.put("ihvCustomer", ihvCustomer);
					if (ihvMerchant1.getFirstName() != null) {
						resultMap.put("firstName", ihvMerchant1.getFirstName());
					} else {
						resultMap.put("firstName", "");
					}
					if (ihvMerchant1.getEmail() != null) {
						resultMap.put("email", ihvMerchant1.getEmail());
					} else {
						resultMap.put("email", "");
					}
					if (ihvMerchant1.getMobileNo() != null) {
						resultMap.put("mobileNo", ihvMerchant1.getMobileNo());
					} else {
						resultMap.put("mobileNo", "");
					}
					if (ihvMerchant1.getType() != null) {
						resultMap.put("type", ihvMerchant1.getType());
					} else {
						resultMap.put("type", "");
					}
					if (ihvMerchant1.getStatus() != null) {
						resultMap.put("status", ihvMerchant1.getStatus());
					} else {
						resultMap.put("status", "");
					}
					if (ihvMerchant1.getMerchID() != null) {
						resultMap.put("MerchID", ihvMerchant1.getMerchID());
					} else {
						resultMap.put("MerchID", "");
					}
					// resultMap.put("LoggedIn", "true");
					response.setResultObj(resultMap);
					response.setStatus("100");
					response.setIsSuccess(true);
					response.setMessage("Success: Merchant LoggedIn successfully.");
					User.setId(idGenerator.generateOtpId("IN"));
					User.setEmailId(email);
					User.setMobileNo(mobileNo);
					User.setPassword(password);
					User.setStatus("Logged_IN");
					User.setloginTime(new Date());
					loginService.createUser(User);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					response.setStatus("200");
					response.setMessage("Failure: Please enter the valid credentials.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				response.setStatus("200");
				response.setMessage("Failure: Merchant with the registered Mobile no. is not found");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			HashMap resultMap = new HashMap<String, String>();
			resultMap.put("isOtpVerified", "false");
			response.setResultObj(resultMap);
			response.setStatus("200");
			response.setMessage("Sorry, we're unable to process your request. Please try after sometime!");
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/ov", method = RequestMethod.POST)
	public ResponseEntity<?> verifyOTP(@RequestParam(value = "mobileNo") String mobile,
			@RequestParam(value = "otp") String otp) {
		HttpHeaders headers = new HttpHeaders();
		try {
			response = new Response();
			logger.info("**** iNside verifyOTP ****");
			logger.info("Param : mobile : " + mobile);
			logger.info("Param : otp : " + otp);
			HashMap resultMap;
			IhvOTP validOtp = otpService.getOTP(mobile);
			if (validOtp != null) {
				// long diff = Math.abs(validOtp.getCreateTime().getTime() - new
				// Date().getTime());
				// if (true) {
				if (validOtp.getOtp().equals(otp) /* && diff < (60 * 1000) */) {
					validOtp.setStatus("USED");
					otpService.updateOTP(validOtp);
					// otpService.deleteOTP(mobile);
					resultMap = new HashMap<String, String>();
					resultMap.put("isOtpVerified", "true");
					response.setResultObj(resultMap);
					response.setIsSuccess(true);
					response.setStatus("100");
					response.setMessage("Valid OTP found");
					logger.info("Valid OTP found");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					resultMap = new HashMap<String, String>();
					resultMap.put("isOtpVerified", "false");
					response.setResultObj(resultMap);
					response.setStatus("200");
					response.setMessage("OTP mismatch!");
					logger.info("OTP mismzatch!");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				resultMap = new HashMap<String, String>();
				resultMap.put("isOtpVerified", "false");
				response.setResultObj(resultMap);
				response.setStatus("200");
				response.setMessage("Please send OTP again");
				logger.info("Please send OTP again");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			HashMap resultMap = new HashMap<String, String>();
			resultMap.put("isOtpVerified", "false");
			response.setResultObj(resultMap);
			response.setStatus("200");
			response.setMessage("Sorry, we're unable to process your request. Please try after sometime!");
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/cic", method = RequestMethod.POST)
	public ResponseEntity<?> createIhvCustomer(@RequestParam(value = "mobileNo") String mobileNo) {
		HttpHeaders headers = new HttpHeaders();
		// headers.add("NAT", token);
		try {
			// org.json.JSONObject parameters = new
			// org.json.JSONObject(requestObj);
			logger.info("**** iNside createIhvCustomer ****");
			logger.info("Param : mobileNo : " + mobileNo);
			response = new Response();
			if (customerService.getCustomer(mobileNo) != null) {
				logger.error("Exiting Create Customer Request as Mobile Number: " + mobileNo + " already exists");
				response.setStatus("200");
				response.setMessage("Alert: Customer with same mobile number already exists.");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Customer not registered. Proceeding for customer registration...");
				String customerID = idGenerator.generateCustomerID("IHVC");
				IhvCustomer ihvCustomer = new IhvCustomer();
				ihvCustomer.setCustID(customerID);
				ihvCustomer.setMobileNo(mobileNo);
				ihvCustomer.setStatus("ACTIVE");
				ihvCustomer.setRegistrationDate(new Date());
				boolean createCustomerResult = customerService.createCustomer(ihvCustomer);
				if (createCustomerResult == true) {
					logger.info("Customer with customerID : " + customerID + " created successfully.");
					response.setStatus("100");
					response.setIsSuccess(true);
					response.setMessage("Success: Successfully Registered");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Customer not created successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Customer can not be created. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/ciu", method = RequestMethod.POST)
	public ResponseEntity<?> updateIhvCustomer(@RequestParam(value = "mobileNo") String mobileNo,
			@RequestParam(value = "fullName") String fullName, @RequestParam(value = "email") String email,
			@RequestParam(value = "dob") String dob, @RequestParam(value = "accountNo") String accountNo,
			@RequestParam(value = "ifsc") String ifsc, @RequestParam(value = "accountType") String accountType,
			@RequestParam(value = "intitutionName") String intitutionName,
			@RequestParam(value = "gstNo") String gstNo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside updateIhvCustomer ****");
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : fullName : " + fullName);
			logger.info("Param : email : " + email);
			logger.info("Param : dob : " + dob);
			logger.info("Param : accountNo : " + accountNo);
			logger.info("Param : ifsc : " + ifsc);
			logger.info("Param : accountType : " + accountType);
			logger.info("Param : intitutionName : " + intitutionName);
			logger.info("Param : gstNo : " + gstNo);

			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " already exists");
				ihvMerchant.setFirstName(fullName);
				ihvMerchant.setEmail(email);
				ihvMerchant.setDob(simpleDateFormat.parse(dob));
				ihvMerchant.setAccountType(accountType);
				ihvMerchant.setAccountNo(accountNo);
				ihvMerchant.setIfsc(ifsc);
				ihvMerchant.setIntitutionName(intitutionName);
				ihvMerchant.setGstNo(gstNo);
				// ihvMerchant.setStatus("DEACTIVE");
				ihvMerchant.setUpdatedOn(new Date());
				boolean updateCustomerResult = merchantService.updateMerchant(ihvMerchant);
				if (updateCustomerResult == true) {
					logger.info("Customer with customerID : " + ihvMerchant.getMerchID() + " updated successfully.");
					response.setStatus("100");
					response.setMessage("Success: Customer updated successfully.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Customer not updated successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Customer can not be updated. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/uiu", method = RequestMethod.POST)
	public ResponseEntity<?> updateIhvUser(@RequestParam(value = "mobileNo") String mobileNo,
			@RequestParam(value = "fullName") String fullName, @RequestParam(value = "email") String email,
			@RequestParam(value = "dob") String dob) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside updateIhvCustomer ****");
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : fullName : " + fullName);
			logger.info("Param : email : " + email);
			logger.info("Param : dob : " + dob);
			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " already exists");
				ihvMerchant.setFirstName(fullName);
				ihvMerchant.setEmail(email);
				ihvMerchant.setDob(simpleDateFormat.parse(dob));
				// ihvMerchant.setStatus("DEACTIVE");
				ihvMerchant.setUpdatedOn(new Date());
				boolean updateCustomerResult = merchantService.updateMerchant(ihvMerchant);
				if (updateCustomerResult == true) {
					logger.info("Customer with customerID : " + ihvMerchant.getMerchID() + " updated successfully.");
					response.setStatus("100");
					response.setMessage("Success: Customer updated successfully.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Customer not updated successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Customer can not be updated. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/cig", method = RequestMethod.POST)
	public ResponseEntity<?> getIhvCustomer(@RequestParam(value = "mobileNo") String mobileNo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside tomer ****");
			logger.info("Param : mobileNo : " + mobileNo);
			response = new Response();
			// HashMap ihvCustomersMap = new HashMap<String, IhvCustomer>();
			HashMap resultMap = new HashMap<String, String>();
			IhvCustomer ihvCustomer = customerService.getCustomer(mobileNo);
			if (ihvCustomer != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " exists");
				// ihvCustomersMap.put("ihvCustomer", ihvCustomer);
				if (ihvCustomer.getFullName() != null) {
					resultMap.put("fullName", ihvCustomer.getFullName());
				} else {
					resultMap.put("fullName", "");
				}
				if (ihvCustomer.getEmail() != null) {
					resultMap.put("email", ihvCustomer.getEmail());
				} else {
					resultMap.put("email", "");
				}
				if (ihvCustomer.getDob() != null) {
					resultMap.put("dob", simpleDateFormat.format(ihvCustomer.getDob()));
				} else {
					resultMap.put("dob", "");
				}
				response.setResultObj(resultMap);
				response.setStatus("100");
				response.setMessage("Success: Customer with respect to Mobile Number: " + mobileNo + " exists");
				response.setIsSuccess(true);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				response.setIsSuccess(false);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/tic", method = RequestMethod.POST)
	public ResponseEntity<?> createIhvTransaction(@RequestParam(value = "transId") String transId,
			@RequestParam(value = "mobileNo") String mobileNo, @RequestParam(value = "amount") String amount,
			@RequestParam(value = "convFee") String convFee, @RequestParam(value = "gstFee") String gstFee,
			@RequestParam(value = "totalAmount") String totalAmount, @RequestParam(value = "remark") String remark) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside createIhvTransaction ****");
			logger.info("Param : transId : " + transId);
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : amount : " + amount);
			logger.info("Param : convFee : " + convFee);
			logger.info("Param : gstFee : " + gstFee);
			logger.info("Param : totalAmount : " + totalAmount);
			logger.info("Param : remark : " + remark);

			response = new Response();
			if (transactionService.getTransactionByTransactionId(transId) != null) {
				logger.error("Exiting Create Transaction Request as Transaction ID: " + transId + " already exists");
				response.setStatus("200");
				response.setMessage("Failure: Transaction with same transaction id already exists.");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Transaction not present. Proceeding for transaction creation...");
				IhvTransaction ihvTransaction = new IhvTransaction();
				ihvTransaction.setTransID(transId);
				ihvTransaction.setMobileNo(mobileNo);
				ihvTransaction.setAmount(amount);
				ihvTransaction.setConvFee(convFee);
				ihvTransaction.setGstFee(gstFee);
				ihvTransaction.setTotalAmount(totalAmount);
				ihvTransaction.setRemark(remark);
				ihvTransaction.setTransactionStatus("IN-PROGRESS");
				ihvTransaction.setInitiatedOn(new Date());
				boolean createCustomerResult = transactionService.createTransaction(ihvTransaction);
				if (createCustomerResult == true) {
					logger.info("Transaction with TransactionID : " + transId + " created successfully.");
					response.setStatus("100");
					response.setMessage("Success: Transaction created successfully.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Transaction not created successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Transaction cannot be created. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/tiu", method = RequestMethod.POST)
	public ResponseEntity<?> updateIhvTransaction(@RequestParam(value = "mobileNo") String mobileNo,
			@RequestParam(value = "transId") String transId,
			@RequestParam(value = "transaction_status") String transaction_status,
			@RequestParam(value = "paymentRefId") String paymentRefId,
			@RequestParam(value = "MerchID") String MerchID) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside updateIhvTransaction ****");
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : transId : " + transId);
			logger.info("Param : transaction_status : " + transaction_status);
			logger.info("Param : paymentRefId : " + paymentRefId);
			logger.info("Param : MerchID : " + MerchID);

			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);

			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " already exists");
				IhvTransaction ihvTransaction = transactionService.getTransactionByTransactionId(transId);
				if (ihvTransaction != null) {
					logger.error("Updating transaction ID: " + transId);
					if (transaction_status.trim().equals("SUCCESS")) {
						ihvTransaction.setPaymentRefId(paymentRefId);
						IhvMerchant ihvMerchant_new = merchantService.getMerchantByMerchantId(MerchID);
						ihvTransaction.setMerchantId(MerchID);
						String sendSMSContent = "Transaction of INR " + ihvTransaction.getAmount()
								+ " is made Successfully to Merchant " + ihvMerchant_new.getFirstName() + "  from User "
								+ merchantService.getMerchant(mobileNo).getFirstName() + " ,Transaction id is "
								+ transId;
						sendSMS.sendSMSResponse(sendSMSContent, ihvMerchant_new.getMobileNo(), "false", "#", "test",
								SEND_SMS_RESPONSE_URL);

						logger.info("Message Sent to:" + ihvMerchant_new.getMobileNo());
					} else if (transaction_status.trim().equals("FAILED")) {
						ihvTransaction.setPaymentRefId("N/A");
						//IhvMerchant ihvMerchant_new = merchantService.getMerchantByMerchantId(MerchID);
						ihvTransaction.setMerchantId(MerchID);
						logger.info("Param : MerchID HELLOO : " + MerchID);
						
					} else {
						ihvTransaction.setPaymentRefId("N/A");
					}
					ihvTransaction.setTransactionStatus(transaction_status);
					ihvTransaction.setCompletedOn(new Date());
					boolean updateTransactionResult = transactionService.updateTransaction(ihvTransaction);
					if (updateTransactionResult == true) {
						logger.info("Transaction with Transaction ID : " + ihvTransaction.getTransID()
								+ " updated successfully.");
						response.setStatus("100");
						response.setMessage("Success: Transaction with Transaction ID : " + ihvTransaction.getTransID()
								+ " updated successfully.");
						return new ResponseEntity(response, headers, HttpStatus.OK);
					} else {
						logger.info("Transaction not updated successfully.");
						response.setStatus("200");
						response.setMessage("Failure: Transaction can not be updated. Please try again.");
						return new ResponseEntity(response, headers, HttpStatus.OK);
					}
				} else {
					logger.error(
							"Exiting Update Transaction Request as Transaction ID: " + transId + " doesn't exist!");
					response.setStatus("200");
					response.setMessage("Failure: Transaction with Transaction ID: " + transId + " doesn't exist!");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/tig", method = RequestMethod.POST)
	public ResponseEntity<?> getIhvTransactions(@RequestParam(value = "mobileNo") String mobileNo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside getIhvTransactions ****");
			logger.info("Param : mobileNo : " + mobileNo);
			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " already exists");
				HashMap resultMap = new HashMap<String, List<IhvTransaction>>();
				List<IhvTransaction> ihvTransactionsList = transactionService.getTransactions(mobileNo);
				if (ihvTransactionsList != null) {
					logger.info("Transactions with respect to Mobile Number: " + mobileNo + " exists");

					resultMap.put("ihvTransactionsList", ihvTransactionsList);

					response.setResultObj(resultMap);
					response.setStatus("100");
					response.setMessage("Success: Transactions with respect to Mobile Number: " + mobileNo
							+ " found successfully.");
					response.setIsSuccess(true);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Transactions with respect to Mobile Number: " + mobileNo + " not found successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Transactions with respect to Mobile Number: " + mobileNo
							+ " not found successfully.");
					response.setIsSuccess(false);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/mtmg", method = RequestMethod.POST)
	public ResponseEntity<?> getMerchantTransactionsForMobileNumber(@RequestParam(value = "merchId") String merchId,
			@RequestParam(value = "mobileNumber") String mobileNumber) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside getMerchantTransactionsForMobileNumber ****");
			logger.info("Param : merchId : " + merchId);
			response = new Response();
			// IhvMerchant ihvMerchant = merchantService.getMerchant(merchId);
			// if (ihvMerchant != null) {
			logger.info("Customer with respect to Merchant ID: " + merchId + " already exists");
			HashMap resultMap = new HashMap<String, List<IhvTransaction>>();
			List<IhvTransaction> ihvTransactionsList = transactionService
					.getMerchantTransactionsForMobileNumber(mobileNumber, merchId);
			if (ihvTransactionsList != null) {
				logger.info("Transactions with respect to Merchant ID: " + merchId + " exists");

				resultMap.put("ihvTransactionsList", ihvTransactionsList);

				response.setResultObj(resultMap);
				response.setStatus("100");
				response.setMessage(
						"Success: Transactions with respect to Merchant ID: " + merchId + " found successfully.");
				response.setIsSuccess(true);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Transactions with respect to Merchant ID: " + merchId + " not found successfully.");
				response.setStatus("200");
				response.setMessage(
						"Failure: Transactions with respect to Merchant ID: " + merchId + " not found successfully.");
				response.setIsSuccess(false);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
			// } else {
			// logger.info("Mobile number is not registered. Please follow
			// mobile verification first!");
			// response.setStatus("200");
			// response.setMessage(
			// "Failure: Mobile number is not registered. Please follow mobile
			// verification first!");
			// return new ResponseEntity(response, headers, HttpStatus.OK);
			// }
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/tmg", method = RequestMethod.POST)
	public ResponseEntity<?> getMerchantTransactions(@RequestParam(value = "merchId") String merchId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside getIhvTransactions ****");
			logger.info("Param : merchId : " + merchId);
			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchantByMerchantId(merchId);
			if (ihvMerchant == null) {
				logger.info("Merchant not found.");
				response.setStatus("200");
				response.setMessage("Merchant not found.");
				// response.setResultObj(newPartnerBank);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
			logger.info("Customer with respect to Merchant ID: " + merchId + " already exists");
			HashMap resultMap = new HashMap<String, List<IhvTransaction>>();
			List<IhvTransaction> ihvReceivedTransactionsList = transactionService.getMerchantTransactions(merchId);
			List<IhvTransaction> ihvTransferTransactionsList = transactionService
					.getTransactions(ihvMerchant.getMobileNo());

			if (ihvReceivedTransactionsList != null) {
				logger.info("Transactions with respect to Merchant ID: " + merchId + " exists");

				resultMap.put("ihvReceivedTransactionsList", ihvReceivedTransactionsList);
				response.setStatus("100");
				response.setMessage(
						"Success: Transactions with respect to Merchant ID: " + merchId + " found successfully.");
				response.setIsSuccess(true);
				if (ihvTransferTransactionsList != null) {
					logger.info("Transactions with respect to MobileNum: " + ihvMerchant.getMobileNo() + " exists");

					resultMap.put("ihvTransferTransactionsList", ihvTransferTransactionsList);

					response.setResultObj(resultMap);
					response.setStatus("100");
					response.setMessage(
							"Success: Transactions with respect to Merchant ID: " + merchId + " found successfully.");
					response.setIsSuccess(true);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					resultMap.put("ihvTransferTransactionsList", new ArrayList<>());
					response.setResultObj(resultMap);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			}

			if (ihvTransferTransactionsList != null) {
				logger.info("Transactions with respect to MobileNum: " + ihvMerchant.getMobileNo() + " exists");

				resultMap.put("ihvTransferTransactionsList", ihvTransferTransactionsList);

				if(ihvReceivedTransactionsList == null){
					resultMap.put("ihvReceivedTransactionsList", new ArrayList<>());
				}
				
				response.setResultObj(resultMap);
				response.setStatus("100");
				response.setMessage(
						"Success: Transactions with respect to Merchant ID: " + merchId + " found successfully.");
				response.setIsSuccess(true);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Transactions with respect to Merchant ID: " + merchId + " not found successfully.");
				response.setStatus("200");
				response.setMessage(
						"Failure: Transactions with respect to Merchant ID: " + merchId + " not found successfully.");
				response.setIsSuccess(false);
				resultMap.put("ihvReceivedTransactionsList", new ArrayList<>());
				resultMap.put("ihvTransferTransactionsList", new ArrayList<>());
				response.setResultObj(resultMap);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
			// } else {
			// logger.info("Mobile number is not registered. Please follow
			// mobile verification first!");
			// response.setStatus("200");
			// response.setMessage(
			// "Failure: Mobile number is not registered. Please follow mobile
			// verification first!");
			// return new ResponseEntity(response, headers, HttpStatus.OK);
			// }
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public ResponseEntity<?> getAllITransactions(@RequestBody String requestObj) {

		JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		HttpHeaders headers = new HttpHeaders();
		headers.add("NAT", token);

		try {

			org.json.JSONObject parameters = new org.json.JSONObject(requestObj);
			String startDate = (String) parameters.get("y1");
			String endDate = (String) parameters.get("y2");
			response = new Response();
			logger.info("**** iNside getAllIhvMerchant ****");
			List transactions = transactionService.getAllTransactions(new Date(Long.valueOf(startDate)),
					new Date(Long.valueOf(endDate) + 24 * 60 * 60000 - 1));
			if (transactions == null) {
				logger.info("Transactions not found.");
				response.setStatus("200");
				response.setMessage("Merchants not found.");
				// response.setResultObj(newPartnerBank);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Transactions found successfully. Total Transactions found : " + transactions.size());
				response.setStatus("100");
				response.setMessage("Transactions exists");
				response.setResultObjList(transactions);
				return new ResponseEntity(response, headers, HttpStatus.OK);
				// return new
				// ResponseEntity<List<NewPartnerBank>>(newPartnerBank, headers,
				// HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	///////////////// ----------------Merchant Details

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/mic", method = RequestMethod.POST)
	public ResponseEntity<?> createIhvMerchant(@RequestParam(value = "mobileNo") String mobileNo,
			@RequestParam(value = "firstName") String firstName, @RequestParam(value = "email") String email,
			@RequestParam(value = "password") String password, @RequestParam(value = "type") String type) {
		HttpHeaders headers = new HttpHeaders();
		// headers.add("NAT", token);
		try {
			// org.json.JSONObject parameters = new
			// org.json.JSONObject(requestObj);
			logger.info("**** iNside createIhvMerchant ****");
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : firstName : " + firstName);
			logger.info("Param : emailId : " + email);
			logger.info("Param : password : " + password);
			logger.info("Param : type : " + type);

			response = new Response();
			if (merchantService.getMerchant(mobileNo) != null) {
				logger.error("Exiting Create Merchant Request as Mobile Number: " + mobileNo + " already exists");
				response.setStatus("200");
				response.setMessage("Alert: Merchant with same mobile number already exists.");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Merchant not registered. Proceeding for Merchant registration...");
				String merchantID = "MW" + mobileNo;
				IhvMerchant ihvMerchant = new IhvMerchant();
				ihvMerchant.setMerchID(merchantID);
				ihvMerchant.setMobileNo(mobileNo);
				ihvMerchant.setFirstName(firstName);
				ihvMerchant.setPassword(password);
				ihvMerchant.setEmail(email);
				ihvMerchant.setType(type);
				ihvMerchant.setStatus("DEACTIVE");
				ihvMerchant.setRegistrationDate(new Date());
				boolean createCustomerResult = merchantService.createMerchant(ihvMerchant);
				if (createCustomerResult == true) {
					logger.info("Merchant with customerID : " + merchantID + " created successfully.");
					response.setStatus("100");
					response.setIsSuccess(true);
					response.setMessage("Success: Merchant created successfully.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Merchant not created successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Merchant can not be created. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/itl", method = RequestMethod.POST)
	public ResponseEntity<?> loginMerchant(@RequestParam(value = "email") String email,
			@RequestParam(value = "password") String password) {
		HttpHeaders headers = new HttpHeaders();
		// headers.add("NAT", token);
		try {
			// org.json.JSONObject parameters = new
			// org.json.JSONObject(requestObj);
			response = new Response();
			logger.info("**** iNside loginIhvMerchant ****");
			logger.info("Param : emailId : " + email);
			logger.info("Param : password : " + password);

			HashMap resultMap;

			IhvLogin User = new IhvLogin();
			IhvMerchant validMerchant = merchantService.getMerchantEmail(email);

			if (validMerchant != null) {
				if (validMerchant.getPassword().equals(password)) {
					resultMap = new HashMap<String, String>();
					resultMap.put("logged_in", "true");
					response.setResultObj(resultMap);
					response.setIsSuccess(true);
					response.setStatus("100");
					response.setMessage("Successfully logged in!");
					logger.info("Successfully logged in!");
					User.setId(idGenerator.generateOtpId("IN"));
					User.setEmailId(email);
					User.setPassword(password);
					User.setStatus("Logged_IN");
					User.setloginTime(new Date());
					loginService.createUser(User);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					resultMap = new HashMap<String, String>();
					resultMap.put("logged_in", "false");
					response.setResultObj(resultMap);
					// response.setResult(resultMap.entrySet());
					response.setStatus("200");
					response.setMessage("Please enter a valid Password.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				resultMap = new HashMap<String, String>();
				resultMap.put("logged_in", "false");
				response.setResultObj(resultMap);
				response.setStatus("200");
				response.setMessage("E-mail Id is not registered");
				logger.info("E-mail Id is not registered");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			HashMap resultMap = new HashMap<String, String>();
			response.setResultObj(resultMap);
			response.setStatus("200");
			response.setMessage("Sorry, we're unable to process your request. Please try after sometime!");
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/mig", method = RequestMethod.POST)
	public ResponseEntity<?> getIhvMerchant(@RequestParam(value = "mobileNo") String mobileNo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside to merchant ****");
			logger.info("Param : mobileNo : " + mobileNo);
			response = new Response();
			// HashMap ihvCustomersMap = new HashMap<String, IhvCustomer>();
			HashMap resultMap = new HashMap<String, String>();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " exists");
				// ihvCustomersMap.put("ihvCustomer", ihvCustomer);
				if (ihvMerchant.getFirstName() != null) {
					resultMap.put("firstName", ihvMerchant.getFirstName());
				} else {
					resultMap.put("firstName", "");
				}
				if (ihvMerchant.getEmail() != null) {
					resultMap.put("email", ihvMerchant.getEmail());
				} else {
					resultMap.put("email", "");
				}
				if (ihvMerchant.getMobileNo() != null) {
					resultMap.put("mobileNo", ihvMerchant.getMobileNo());
				} else {
					resultMap.put("mobileNo", "");
				}
				if (ihvMerchant.getDob() != null) {
					resultMap.put("dob", simpleDateFormat.format(ihvMerchant.getDob()));
				} else {
					resultMap.put("dob", "");
				}
				if (ihvMerchant.getAccountNo() != null) {
					resultMap.put("accountNo", ihvMerchant.getAccountNo());
				} else {
					resultMap.put("accountNo", "");
				}
				if (ihvMerchant.getAccountType() != null) {
					resultMap.put("accountType", ihvMerchant.getAccountType());
				} else {
					resultMap.put("accountType", "");
				}
				if (ihvMerchant.getIfsc() != null) {
					resultMap.put("ifsc", ihvMerchant.getIfsc());
				} else {
					resultMap.put("ifsc", "");
				}
				if (ihvMerchant.getIntitutionName() != null) {
					resultMap.put("institutionName", ihvMerchant.getIntitutionName());
				} else {
					resultMap.put("institutionName", "");
				}
				if (ihvMerchant.getGstNo() != null) {
					resultMap.put("gstNo", ihvMerchant.getGstNo());
				} else {
					resultMap.put("gstNo", "");
				}
				response.setResultObj(resultMap);
				response.setStatus("100");
				response.setMessage("Success: Merchant with respect to Mobile Number: " + mobileNo + " exists");
				response.setIsSuccess(true);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				response.setIsSuccess(false);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('MANAGEBANKSMAKER', 'MANAGEBANKSCHECKER')")
	@RequestMapping(value = "/miag", method = RequestMethod.GET)
	public ResponseEntity<?> getAllIhvMerchant() {
		JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);

		HttpHeaders headers = new HttpHeaders();
		headers.add("NAT", token);
		try {
			response = new Response();
			logger.info("**** iNside getAllIhvMerchant ****");
			List ihvMerchants = merchantService.getMerchantByType();
			if (ihvMerchants == null) {
				logger.info("Merchants not found.");
				response.setStatus("200");
				response.setMessage("Merchants not found.");
				// response.setResultObj(newPartnerBank);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Merchants details found successfully. Total Merchants found : " + ihvMerchants.size());
				response.setStatus("100");
				response.setMessage("Merchants exists");
				response.setResultObjList(ihvMerchants);
				return new ResponseEntity(response, headers, HttpStatus.OK);
				// return new
				// ResponseEntity<List<NewPartnerBank>>(newPartnerBank, headers,
				// HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('MANAGEBANKSMAKER', 'MANAGEBANKSCHECKER')")
	@RequestMapping(value = "/misu", method = RequestMethod.POST)
	public ResponseEntity<?> UpdateStatusIhvMerchant(@RequestBody String requestObj, HttpServletRequest request) {
		JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		org.json.JSONObject parameters = new org.json.JSONObject(requestObj);
		String merchantID = (String) parameters.get("c1");
		HttpHeaders headers = new HttpHeaders();
		headers.add("NAT", token);
		try {
			response = new Response();
			logger.info("**** iNside UpdateStatusIhvMerchant  ****");
			IhvMerchant ihvMerchant = merchantService.getMerchantByMerchantId(merchantID);
			if (ihvMerchant == null) {
				logger.info("Merchant not found.");
				response.setStatus("200");
				response.setMessage("Merchants not found.");
				// response.setResultObj(newPartnerBank);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				logger.info("Merchant details found successfully.");
				if (ihvMerchant.getStatus().equals("ACTIVE")) {
					ihvMerchant.setStatus("DEACTIVE");
				} else {
					ihvMerchant.setStatus("ACTIVE");
				}
				merchantService.updateMerchant(ihvMerchant);
				response.setStatus("100");
				response.setMessage("Merchant " + ihvMerchant.getFirstName() + " status changed successfully to : "
						+ ihvMerchant.getStatus());
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/kwcg", method = RequestMethod.POST)
	public ResponseEntity<?> generateCaptchaWithKey(@RequestBody String requestObj, HttpServletRequest request) {
		try {
			org.json.JSONObject parameters = new org.json.JSONObject(requestObj);
			String userId = (String) parameters.get("c1");
			response = new Response();
			logger.info("Inside generateCaptcha...");
			logger.info("Param : userIdParam : " + userId);
			logger.info("generate captcha Req from: " + request.getRemoteAddr());
			String userIdParam = userId.toLowerCase();
			List<String> resultObj = new ArrayList<String>();
			if (userIdParam != null && !userIdParam.isEmpty()) {
				Captcha captcha = captchaService.getCaptchaByUserId(userIdParam);
				if (captcha != null) {
					logger.info("Existing captcha found for user " + userIdParam + "in database. Deleting....");
					captchaService.deleteCaptcha(captcha);
				} else {
					logger.info("No existing captcha found for user " + userIdParam + "in database.");
					captcha = new Captcha();
				}
				String captchaStr = captchaUtil.generateCaptchaText(6);
				BufferedImage captchaImg = captchaUtil.generateCaptchaImage(captchaStr);
				// change bufferedImage to byte[] - START
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(captchaImg, "jpg", baos);
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				String encodedImageInString = Base64.getEncoder().encodeToString(imageInByte);
				baos.close();
				// change bufferedImage to byte[] - END
				captcha.setUserId(userIdParam);
				captcha.setCaptchaText(captchaStr);
				captcha.setCaptchaImg(imageInByte);
				captcha.setCreatedOn(new Date());
				gk.createKeys();
				captcha.setSalt(gk.createStringFromPrivateKey(gk.getPrivateKey()));
				boolean generateCaptcha = captchaService.createCaptcha(captcha);
				if (generateCaptcha) {
					resultObj.add(encodedImageInString);
					resultObj.add(gk.createStringFromPublicKey(gk.getPublicKey()));
					response.setStatus("100");
					response.setMessage("Captcha generated successfully.");
					response.setResultObjList(resultObj);
					return new ResponseEntity(response, HttpStatus.OK);
				} else {
					logger.info("Error while creating captcha!");
					response.setStatus("200");
					response.setMessage("Error while creating captcha!");
					return new ResponseEntity(response, HttpStatus.OK);
				}
			} else {
				logger.info("User id param not provided.");
				response.setStatus("200");
				response.setMessage("User id not provided.");
				return new ResponseEntity(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/ay", method = RequestMethod.POST)
	public ResponseEntity<?> createYesBankAuthenticationToken(@RequestBody String object, HttpServletRequest request)
			throws AuthenticationException {
		try {
			response = new Response();
			logger.info("YBL Login Req from: " + request.getRemoteAddr());
			logger.info("Inside createYesBankAuthenticationToken...");
			org.json.JSONObject parameters = new org.json.JSONObject(object);
			String username = parameters.getString("a1");
			String password = parameters.getString("a2");
			// String passphrase = "Secret Passphrase";
			// String ivParam = parameters.getString("a4");
			// String saltParam = parameters.getString("a5");
			// Inserted captcha validation in login request -- START
			String captchaParam = parameters.getString("a3");
			logger.info("validate captcha Req from: " + request.getRemoteAddr());
			String userIdParam = username.toLowerCase();
			if (userIdParam != null && !userIdParam.isEmpty()) {
				logger.info("Parameter user-id provided.");
				Captcha captcha = captchaService.getCaptchaByUserId(userIdParam);
				if (captcha != null) {
					logger.info("Captcha exists in database.");
					if (captchaParam != null && !captchaParam.isEmpty()) {
						logger.info("Parameter captcha provided.");
						if (captcha.getCaptchaText().equals(captchaParam)) {
							logger.info("Captcha matched successfully, Trying to login now...");
							captchaService.deleteCaptcha(captcha);
							response.setStatus("100");
							response.setMessage("Captcha matched successfully.");
							String decryptedPassword = gk.decryptRsa(gk.createPrivateKeyFromString(captcha.getSalt()),
									password);

							portalUserDAO.setPasswordForLDAPUser(passwordUtil.generateHash(decryptedPassword));

							// String dn = "uid=" + username + "," + ldapBase;

							/*
							 * // Setup environment for authenticating
							 * Hashtable<String, String> environment = new
							 * Hashtable<String, String>();
							 * environment.put(Context.INITIAL_CONTEXT_FACTORY,
							 * "com.sun.jndi.ldap.LdapCtxFactory");
							 * 
							 * environment.put(Context.PROVIDER_URL, ldapUrl);
							 * environment.put(Context.SECURITY_AUTHENTICATION,
							 * "simple");
							 * environment.put(Context.SECURITY_PRINCIPAL,
							 * "yesbank\\" + username);
							 * environment.put(Context.SECURITY_CREDENTIALS,
							 * decryptedPassword);
							 * 
							 * DirContext authContext = new
							 * InitialDirContext(environment); // user is
							 * authenticated
							 */
							List resultObj = new ArrayList();
							PortalUser portalUser = portalService.getUserByUserName(username);
							if (portalUser == null) {
								logger.info("User details not found.");
								response.setStatus("200");
								response.setMessage("User not found");
								return new ResponseEntity(response, HttpStatus.OK);
							} else {
								logger.info("User details found.");
								if (portalUser.getUserStatus().equals("ACTIVE")) {
									if (portalUser.getRoles().contains("YESBANKADMIN")
											|| portalUser.getRoles().contains("KITMANAGER")
											|| portalUser.getRoles().contains("RECONSYSTEMMANAGER")
											|| portalUser.getRoles().contains("MISREPORTMANAGER")
											|| portalUser.getRoles().contains("MANAGEBANKSMAKER")
											|| portalUser.getRoles().contains("MANAGEBANKSCHECKER")
											|| portalUser.getRoles().contains("MANAGEUSERMAKER")
											|| portalUser.getRoles().contains("MANAGEUSERCHECKER")
											|| portalUser.getRoles().contains("KYCMANAGER")
											|| portalUser.getRoles().contains("QUERYMANAGER")) {
										logger.info(portalUser.getRoles());
										SessionInfo prevSession = sessionService
												.getSessionByUser(portalUser.getUserName());
										if (prevSession == null || (prevSession.getIssuedAt() == null
												&& prevSession.getIp().equals(request.getRemoteAddr()))) {
											// Perform the security
											JwtAuthenticationRequest authenticationRequest = new JwtAuthenticationRequest();
											authenticationRequest.setUsername(username.toLowerCase());
											authenticationRequest
													.setPassword(passwordUtil.generateHash(decryptedPassword));
											final Authentication authentication = authenticationManager
													.authenticate(new UsernamePasswordAuthenticationToken(
															authenticationRequest.getUsername(),
															authenticationRequest.getPassword()));
											SecurityContextHolder.getContext().setAuthentication(authentication);
											// Reload password post-security so
											// we
											// can generate token
											final UserDetails userDetails = userDetailsService
													.loadUserByUsername(authenticationRequest.getUsername());
											if (prevSession != null) {
												sessionService.deleteSession(userDetails.getUsername());
											}
											SessionInfo sessionInfo = new SessionInfo();
											sessionInfo.setId(idGenerator.generateId("SES"));
											sessionInfo.setUserId(userDetails.getUsername());
											sessionInfo.setIp(request.getHeader("X-FORWARDED-FOR") != null
													? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr());
											sessionInfo.setRequestsThisMinute(0);
											sessionService.createSession(sessionInfo);
											final String token = jwtTokenUtil.generateToken(userDetails);
											// Return the token
											logger.info("Returning token...");
											HttpHeaders headers = new HttpHeaders();
											headers.add("NAT", token);
											// return ResponseEntity.ok(new
											// JwtAuthenticationResponse(token));
											Date loginTime = new Date();
											portalUser.setLastLogin(loginTime);
											portalService.updateUser(portalUser);
											// return the successful login
											// object
											// with message
											logger.info("User is eligible to login into the system.");
											response.setStatus("100");
											response.setMessage("User is eligible to login into the system");
											response.setResultObjList(resultObj);
											logger.info("Creating audit log for Portal.");
											JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext()
													.getAuthentication().getPrincipal();
											/*
											 * portalAuditUtil.createPortalAudit
											 * (idGenerator.
											 * generatePortalAuditID("AUDIT"),
											 * currentUser.getUsername(),
											 * request.getHeader(
											 * "X-FORWARDED-FOR") != null ?
											 * request.getHeader(
											 * "X-FORWARDED-FOR") :
											 * request.getRemoteAddr(), new
											 * Date(), "Login - YesBank", "",
											 * "", "", "", "", "", "");
											 */
											return new ResponseEntity(response, headers, HttpStatus.OK);
										} else {
											logger.info(
													"Your previous session has incorrectly expired or is currently active. Please try logging in after some time.");
											response.setStatus("200");
											response.setMessage(
													"Your previous session has incorrectly expired or is currently active. Please try logging in after some time.");
											return new ResponseEntity(response, HttpStatus.OK);
										}
									} else {
										logger.info("User Does Not Have Permission To Log In To This Portal!");
										response.setStatus("200");
										response.setMessage(
												"Alert: You Do Not Have Permission To Log In To This Portal!");
										return new ResponseEntity(response, HttpStatus.OK);
									}
								} else {
									logger.info("User is currently not ACTIVE");
									response.setStatus("200");
									response.setMessage("User is currently not ACTIVE");
									return new ResponseEntity(response, HttpStatus.OK);
								}
							}
							// Inserted existing login code -- END
						} else {
							logger.info("Captcha not matched.");
							response.setStatus("200");
							response.setMessage("Captcha not matched.");
							return new ResponseEntity(response, HttpStatus.OK);
						}
					} else {
						logger.info("Parameter captcha not provided.");
						response.setStatus("200");
						response.setMessage("Parameter captcha not provided.");
						return new ResponseEntity(response, HttpStatus.OK);
					}
				} else {
					logger.info("Invalid Captcha");
					response.setStatus("200");
					response.setMessage("Invalid Captcha");
					return new ResponseEntity(response, HttpStatus.OK);
				}
			} else {
				logger.info("Parameter user-id not provided.");
				response.setStatus("200");
				response.setMessage("Parameter user-id not provided.");
				return new ResponseEntity(response, HttpStatus.OK);
			}
			// Inserted captcha validation in login request -- END
		} catch (BadCredentialsException e) {
			response.setStatus("200");
			response.setMessage("Bad Credentials");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (AuthenticationException e) {
			response.setStatus("200");
			response.setMessage("Bad Credentials");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (NamingException e) {
			response.setStatus("200");
			response.setMessage("Bad Credentials");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/la", method = RequestMethod.POST)
	public ResponseEntity<?> logoutUser(@RequestBody String requestObj, HttpServletRequest request)
			throws AuthenticationException {
		try {
			org.json.JSONObject parameters = new org.json.JSONObject(requestObj);
			String userName = (String) parameters.get("l1");
			response = new Response();
			logger.info("Logout Req from: " + request.getRemoteAddr());
			logger.info("Inside logoutUser...");
			// JwtUser currentUser = (JwtUser)
			// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			// List resultObj = new ArrayList();
			PortalUser portalUser = portalService.getUserByUserName(userName.toLowerCase());
			if (portalUser == null) {
				logger.info("User details not found.");
				response.setStatus("200");
				response.setMessage("ERROR");
				return new ResponseEntity(response, HttpStatus.OK);
			} else {
				logger.info("User details found.");
				SessionInfo session = sessionService.getSessionByUser(portalUser.getUserName());
				if (session != null && session.getIp().equals(request.getHeader("X-FORWARDED-FOR") != null
						? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr())) {
					logger.info("Updating ActiveLogins for the user -- 0");
					sessionService.deleteSession(userName.toLowerCase());
					portalService.updateUser(portalUser);
					logger.info("User is successfully logged-out from the system.");
					response.setStatus("100");
					response.setMessage("User is successfully logged-out from the system.");
					// response.setResultObj(resultObj);
					logger.info("Creating audit log for Portal.");
					/*
					 * portalAuditUtil.createPortalAudit(idGenerator.
					 * generatePortalAuditID("AUDIT"), userName.toLowerCase(),
					 * request.getHeader("X-FORWARDED-FOR") != null ?
					 * request.getHeader("X-FORWARDED-FOR") :
					 * request.getRemoteAddr(), new Date(), "Logout", "", "",
					 * "", "", "", "", "");
					 */
					return new ResponseEntity(response, HttpStatus.OK);
				} else {
					logger.info("User is already logged out.");
					response.setStatus("200");
					response.setMessage("ERROR.");
					return new ResponseEntity(response, HttpStatus.OK);
				}
			}
		} catch (ClassCastException e) {
			response.setStatus("500");
			response.setMessage("No user to log out");
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage("Internal Server Error");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@RequestMapping(value = "/ayc", method = RequestMethod.POST)
	public ResponseEntity<?> createIHVUser(@RequestBody String json, HttpServletRequest request) {
		JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		HttpHeaders headers = new HttpHeaders();
		headers.add("NAT", token);
		try {
			if (portalService.getUserByUserName(currentUser.getUsername()) == null) {
				response = new Response();
				PortalUser user = new PortalUser();
				user.setUserID(idGenerator.generateId("USR"));
				user.setUserName(currentUser.getUsername());
				List<String> roles = new ArrayList<>();
				roles.add("ADMIN");
				user.setRoles(roles);
				user.setEmployeeID(String.valueOf(new Random().nextInt(5654213)));
				user.setUserStatus("ACTIVE");
				user.setCreateDate(new Date());
				boolean created = portalService.createUser(user);
				response.setStatus("100");
				response.setMessage("Success: YES Bank user has been created with the specified roles.");
				logger.info("Creating audit log for Portal....");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				response.setStatus("200");
				response.setMessage(
						"Failure: Entered User Name has already been registered. Please enter another User Name");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setStatus("500");
			response.setMessage(
					"Failure: We are unable to process the request right now. Please try again after sometime");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/pm", method = RequestMethod.POST)
	public ResponseEntity<?> makePayment(@RequestParam(value = "merchID") String merchantID) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside to make Payment ****");
			response = new Response();
			// HashMap ihvCustomersMap = new HashMap<String, IhvCustomer>();
			HashMap resultMap = new HashMap<String, String>();
			IhvMerchant ihvMerchant = merchantService.getMerchantByMerchantId(merchantID);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + merchantID + " exists");
				// ihvCustomersMap.put("ihvCustomer", ihvCustomer);
				if (ihvMerchant.getStatus().equals("ACTIVE")) {
					resultMap.put("merchantExist", "True");
					resultMap.put("merchantName", ihvMerchant.getFirstName());
					response.setResultObj(resultMap);
					response.setStatus("100");
					response.setMessage("Success: Merchant is Active");
					response.setIsSuccess(true);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					resultMap.put("merchantExist", "False");
					response.setResultObj(resultMap);
					response.setStatus("200");
					response.setMessage(
							"Success: Merchant with respect to Merchant Id: " + merchantID + " is not Active");
					response.setIsSuccess(true);
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
				// return new ResponseEntity(response, headers, HttpStatus.OK);
			} else {
				resultMap.put("merchantExist", "False");
				logger.info("Merchant is not registered. Please follow mobile verification first!");
				response.setResultObj(resultMap);
				response.setStatus("100");
				response.setMessage("Merchant with Merch Id " + merchantID + "  is not registered ");
				response.setIsSuccess(true);
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			response.setIsSuccess(false);
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/piu", method = RequestMethod.POST)
	public ResponseEntity<?> updateIhvPassword(@RequestParam(value = "mobileNo") String mobileNo,
			@RequestParam(value = "password") String password) {
		HttpHeaders headers = new HttpHeaders();
		try {
			logger.info("**** iNside updateIhvCustomer ****");
			logger.info("Param : mobileNo : " + mobileNo);
			logger.info("Param : password : " + password);
			response = new Response();
			IhvMerchant ihvMerchant = merchantService.getMerchant(mobileNo);
			if (ihvMerchant != null) {
				logger.info("Customer with respect to Mobile Number: " + mobileNo + " already exists");
				ihvMerchant.setPassword(password);
				// ihvMerchant.setStatus("DEACTIVE");
				ihvMerchant.setUpdatedOn(new Date());
				boolean updateCustomerResult = merchantService.updateMerchant(ihvMerchant);
				if (updateCustomerResult == true) {
					logger.info("Customer with customerID : " + ihvMerchant.getMerchID() + " updated successfully.");
					response.setStatus("100");
					response.setIsSuccess(true);
					response.setMessage("Success: Customer updated successfully.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				} else {
					logger.info("Customer not updated successfully.");
					response.setStatus("200");
					response.setMessage("Failure: Customer can not be updated. Please try again.");
					return new ResponseEntity(response, headers, HttpStatus.OK);
				}
			} else {
				logger.info("Mobile number is not registered. Please follow mobile verification first!");
				response.setStatus("200");
				response.setMessage(
						"Failure: Mobile number is not registered. Please follow mobile verification first!");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Sorry, we're unable to process your request. Please try after sometime!");
			response.setStatus("500");
			response.setMessage("Failure: Sorry, we're unable to process your request. Please try after sometime!");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
	}
	
}
