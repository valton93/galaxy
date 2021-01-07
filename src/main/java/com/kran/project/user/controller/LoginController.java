package com.kran.project.user.controller;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.user.dto.UserBean;
import com.kran.project.user.entities.CullingCenterUsers;
import com.kran.project.user.entities.DistrictUsers;
import com.kran.project.user.entities.Users;
import com.kran.project.user.entities.repo.CullingCenterUsersRepository;
import com.kran.project.user.entities.repo.DistrictUsersRepository;
import com.kran.project.user.entities.repo.UsersRepository;
import com.kran.project.utilities.MessageService;
import com.kran.project.utilities.MessageService.Provider;
import com.kran.utility.encryption.EncryptUsingDES;

@Controller
@RequestMapping("login")
@Scope("prototype")
public class LoginController {
	@Autowired
    UsersRepository usersLoginRepository;
	@Autowired
    DistrictUsersRepository districtUsersRepository;
	@Autowired
    CullingCenterUsersRepository cullingCenterUsersRepository;
	
	@Value("${templates.forgotpassword}")
    private String templatesForgotPassword;
	
	@GetMapping(value = "forgotPassword")
	public ModelAndView forgotPassword(HttpSession session) {
		return new ModelAndView("login/forgotpassword");
	}
	
	@PostMapping(value = "submitResetPassword")
	public ModelAndView submitResetPassword(UserBean userBean) {
		boolean resultStatus = false;
		
		String mobileNumber = null;
		Long userId = null;
		
		ModelAndView modelAndView = new ModelAndView();

		if (userBean.getUserDomain() != null
			&& !userBean.getUserDomain().isBlank()) {
			Users user = null;
			DistrictUsers districtUsers = null;
			CullingCenterUsers cullingCenterUsers = null;
			
			if (userBean.getUserDomain().equals("A")) {
				Optional<Users> optionalUser
					= usersLoginRepository.findByUserName(userBean.getName());
				if (optionalUser.isPresent()) {
					user = optionalUser.get();
					if (user.getMobile() != null) {
						mobileNumber = user.getMobile();
						userId = user.getId();
					}
				}
			} else if (userBean.getUserDomain().equals("D")) {
				Optional<DistrictUsers> optionalUser
					= districtUsersRepository.findByUserName(userBean.getName());
				if (optionalUser.isPresent()) {
					districtUsers = optionalUser.get();
					if (districtUsers.getMobile() != null) {
						mobileNumber = String.valueOf(districtUsers.getMobile());
						userId = districtUsers.getId();
					}
				}
			}
			else if (userBean.getUserDomain().equals("C")) {
				Optional<CullingCenterUsers> optionalUser
					= cullingCenterUsersRepository.findByUserName(userBean.getName());
				if (optionalUser.isPresent()) {
					cullingCenterUsers = optionalUser.get();
					if (cullingCenterUsers.getMobile() != null) {
						mobileNumber = String.valueOf(cullingCenterUsers.getMobile());
						userId = cullingCenterUsers.getId();
					}
				}
			}
			else if (userBean.getUserDomain().equals("F")) {
				Optional<Users> optionalUser
				= usersLoginRepository.findByUserName(userBean.getName());
			if (optionalUser.isPresent()) {
				user = optionalUser.get();
				if (user.getMobile() != null) {
					mobileNumber = user.getMobile();
					userId = user.getId();
				}
			}
		}
			
			if (mobileNumber != null
				&& !mobileNumber.isBlank()
				&& mobileNumber.length() == 10) {
				if (userBean.getCurrentpassword().equals(mobileNumber)) {
					StringBuilder generatedToken = new StringBuilder();
					try {
						SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
						for (int i = 0; i < 4; i++) {
							generatedToken.append(number.nextInt(9));
						}
						
						if (userBean.getUserDomain().equals("A")) {
							user.setOtp(generatedToken.toString());
							usersLoginRepository.save(user);
						} else if (userBean.getUserDomain().equals("D")) {
							districtUsers.setOtp(generatedToken.toString());
							districtUsersRepository.save(districtUsers);
						} 
						else if (userBean.getUserDomain().equals("C")) {
							cullingCenterUsers.setOtp(generatedToken.toString());
							cullingCenterUsersRepository.save(cullingCenterUsers);
						} 
						else if (userBean.getUserDomain().equals("F")) {
							user.setOtp(generatedToken.toString());
							usersLoginRepository.save(user);
						} 
						
						String messageContent = templatesForgotPassword.replaceAll("#VAL1", generatedToken.toString());
						MessageService.sendMessage(messageContent,
												   new String[] { mobileNumber },
												   Provider.SMS_SERVETEL);
						resultStatus = true;
					} catch (NoSuchAlgorithmException
							| UnknownHostException
							| UnsupportedEncodingException e) {
						resultStatus = false;
					}
				}
			}
			
		}
		
		if (resultStatus) {
			modelAndView.setViewName("redirect:/login/forgotPasswordConfirm/" + userBean.getUserDomain() + "/" + userId);
		} else {
			modelAndView.addObject("error", "Invalid User Category/ Username/ Mobile Number. And make sure that your mobile number is registered.");
			modelAndView.setViewName("/login/forgotpassword");
		}

		return modelAndView;
	}
	
	@GetMapping(value = "forgotPasswordConfirm/{userDomain}/{userId}")
	public ModelAndView forgotPasswordConfirm(@PathVariable("userDomain") String userDomain, @PathVariable("userId") Long userId) {
		ModelAndView modelAndView = new ModelAndView();
		
		if (userDomain != null && !userDomain.isBlank()) {
			UserBean userBean = new UserBean();
			userBean.setUserDomain(userDomain);
			
			if (userDomain.equals("A")||userDomain.equals("F")) {
				Optional<Users> optionalUser = usersLoginRepository.findById(userId);
				if (optionalUser.isPresent()) {
					Users user = optionalUser.get();
					userBean.setUserId(user.getId());
					userBean.setOtp(user.getOtp());
				}
			} else if (userDomain.equals("D")) {
				Optional<DistrictUsers> optionalUser = districtUsersRepository.findById(userId);
				if (optionalUser.isPresent()) {
					DistrictUsers districtUsers = optionalUser.get();
					userBean.setUserId(districtUsers.getId());
					userBean.setOtp(districtUsers.getOtp());
				}
			} 
			else if (userDomain.equals("C")) {
				Optional<CullingCenterUsers> optionalUser = cullingCenterUsersRepository.findById(userId);
				if (optionalUser.isPresent()) {
					CullingCenterUsers cullingCenterUsers = optionalUser.get();
					userBean.setUserId(cullingCenterUsers.getId());
					userBean.setOtp(cullingCenterUsers.getOtp());
				}
			}
			
			modelAndView.addObject("userBean", userBean);
			modelAndView.setViewName("/login/forgotpasswordconfirm");
			return modelAndView;
		}
		
		modelAndView.addObject("error", "Invalid User Category/ Username/ Mobile Number. And make sure that your mobile number is registered.");
		modelAndView.setViewName("/login/forgotpassword");
		return modelAndView;
	}
	
	@PostMapping(value = "resetUserCredentials")
	public ModelAndView resetUserCredentials(UserBean userBean) {
		ModelAndView modelAndView = new ModelAndView();
		
		if (userBean.getUserDomain() != null
			&& !userBean.getUserDomain().isBlank()
			&& userBean.getUserId() > 0) {
			if (userBean.getUserDomain().equals("A")||userBean.getUserDomain().equals("F")) {
				Optional<Users> optionalUser = usersLoginRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					Users user = optionalUser.get();
					user.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
					user.setPasswordReset("Y");
					user.setPasswordResetOn(new Date());
					user.setOtp(null);
					usersLoginRepository.save(user);
				}
			} else if (userBean.getUserDomain().equals("D")) {
				Optional<DistrictUsers> optionalUser = districtUsersRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					DistrictUsers districtUsers = optionalUser.get();
					districtUsers.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
					districtUsers.setPasswordReset("Y");
					districtUsers.setPasswordResetOn(new Date());
					districtUsers.setOtp(null);
					districtUsersRepository.save(districtUsers);
				}
			}
			 else if (userBean.getUserDomain().equals("C")) {
					Optional<CullingCenterUsers> optionalUser = cullingCenterUsersRepository.findById(userBean.getUserId());
					if (optionalUser.isPresent()) {
						CullingCenterUsers cullingCenterUsers = optionalUser.get();
						cullingCenterUsers.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
						cullingCenterUsers.setPasswordReset("Y");
						cullingCenterUsers.setPasswordResetOn(new Date());
						cullingCenterUsers.setOtp(null);
						cullingCenterUsersRepository.save(cullingCenterUsers);
					}
				}
			
			modelAndView.setViewName("redirect:/login/forgotPasswordSuccess");
			return modelAndView;
		}
		
		modelAndView.addObject("error", "Something went wrong. Please try again.");
		modelAndView.setViewName("/login/forgotpassword");
		return modelAndView;
	}
	
	@GetMapping(value = "forgotPasswordSuccess")
	public ModelAndView forgotpasswordsuccess() {
		return new ModelAndView("login/forgotpasswordsuccess");
	}
	
	@GetMapping(value = "updatePassword")
	public ModelAndView updatePassword(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		
		boolean resultStatus = false;
		
		String mobileNumber = null;
		
		ModelAndView modelAndView = new ModelAndView();

		if (userBean.getUserDomain() != null
			&& !userBean.getUserDomain().isBlank()) {
			Users user = null;
			DistrictUsers districtUsers = null;
			CullingCenterUsers cullingCenterUsers = null;
			
			if (userBean.getUserDomain().equals("A")) {
				Optional<Users> optionalUser = usersLoginRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					user = optionalUser.get();
					if (user.getMobile() != null) {
						mobileNumber = user.getMobile();
					}
				}
			} else if (userBean.getUserDomain().equals("D")) {
				Optional<DistrictUsers> optionalUser
					= districtUsersRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					districtUsers = optionalUser.get();
					if (districtUsers.getMobile() != null) {
						mobileNumber = String.valueOf(districtUsers.getMobile());
					}
				}
			}
			else if (userBean.getUserDomain().equals("C")) {
				Optional<CullingCenterUsers> optionalUser
					= cullingCenterUsersRepository.findByUserName(userBean.getName());
				if (optionalUser.isPresent()) {
					cullingCenterUsers = optionalUser.get();
					if (cullingCenterUsers.getMobile() != null) {
						mobileNumber = String.valueOf(cullingCenterUsers.getMobile());
					}
				}
			}
			else if (userBean.getUserDomain().equals("F")) {
				Optional<Users> optionalUser
				= usersLoginRepository.findByUserName(userBean.getName());
			if (optionalUser.isPresent()) {
				user = optionalUser.get();
				if (user.getMobile() != null) {
					mobileNumber = user.getMobile();
				}
			}
		}
			
			if (mobileNumber != null
				&& !mobileNumber.isBlank() && mobileNumber.length() == 10) {
				StringBuilder generatedToken = new StringBuilder();
				try {
					SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
					for (int i = 0; i < 4; i++) {
						generatedToken.append(number.nextInt(9));
					}
					
					if (userBean.getUserDomain().equals("A")) {
						user.setOtp(generatedToken.toString());
						usersLoginRepository.save(user);
						
						userBean.setOtp(user.getOtp());
					} else if (userBean.getUserDomain().equals("D")) {
						districtUsers.setOtp(generatedToken.toString());
						districtUsersRepository.save(districtUsers);
						
						userBean.setOtp(districtUsers.getOtp());
					}
					else if (userBean.getUserDomain().equals("C")) {
						cullingCenterUsers.setOtp(generatedToken.toString());
						cullingCenterUsersRepository.save(cullingCenterUsers);
					} 
					else if (userBean.getUserDomain().equals("F")) {
						user.setOtp(generatedToken.toString());
						usersLoginRepository.save(user);
					} 
					String messageContent = templatesForgotPassword.replaceAll("#VAL1", generatedToken.toString());
					MessageService.sendMessage(messageContent,
											   new String[] { mobileNumber },
											   Provider.SMS_SERVETEL);
					resultStatus = true;
				} catch (NoSuchAlgorithmException
						| UnknownHostException
						| UnsupportedEncodingException e) {
					resultStatus = false;
				}
			}
			
		}
		
		if (resultStatus) {
			modelAndView.addObject("userBean", userBean);
			modelAndView.setViewName("/login/updatepasswordconfirm");
		} else {
			modelAndView.addObject("error", "INVALID MOBILE NUMBER OR YOUR MOBILE NUMBER IS NOT REGISTERED WITH US.");
			modelAndView.setViewName("/login/updatepassword");
		}

		return modelAndView;
	}
	
	@PostMapping(value = "updateUserCredentials")
	public ModelAndView updateUserCredentials(UserBean userBean) {
		ModelAndView modelAndView = new ModelAndView();
		
		if (userBean.getUserDomain() != null
			&& !userBean.getUserDomain().isBlank()
			&& userBean.getUserId() > 0) {
			if (userBean.getUserDomain().equals("A")||userBean.getUserDomain().equals("F")) {
				Optional<Users> optionalUser = usersLoginRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					Users user = optionalUser.get();
					user.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
					user.setPasswordReset("Y");
					user.setPasswordResetOn(new Date());
					user.setOtp(null);
					usersLoginRepository.save(user);
				}
			} else if (userBean.getUserDomain().equals("D")) {
				Optional<DistrictUsers> optionalUser = districtUsersRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					DistrictUsers districtUsers = optionalUser.get();
					districtUsers.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
					districtUsers.setPasswordReset("Y");
					districtUsers.setPasswordResetOn(new Date());
					districtUsers.setOtp(null);
					districtUsersRepository.save(districtUsers);
				}
			} 
			else if (userBean.getUserDomain().equals("C")) {
				Optional<CullingCenterUsers> optionalUser = cullingCenterUsersRepository.findById(userBean.getUserId());
				if (optionalUser.isPresent()) {
					CullingCenterUsers cullingCenterUsers = optionalUser.get();
					cullingCenterUsers.setPassword(new EncryptUsingDES().encrypt(userBean.getNewpassword()));
					cullingCenterUsers.setPasswordReset("Y");
					cullingCenterUsers.setPasswordResetOn(new Date());
					cullingCenterUsers.setOtp(null);
					cullingCenterUsersRepository.save(cullingCenterUsers);
				}
			}
			
			modelAndView.setViewName("redirect:/login/updatePasswordSuccess");
			return modelAndView;
		}
		
		modelAndView.addObject("error", "Something went wrong. Please try again.");
		modelAndView.setViewName("/login/updatepassword");
		return modelAndView;
	}
	
	@GetMapping(value = "updatePasswordSuccess")
	public ModelAndView updatePasswordSuccess() {
		return new ModelAndView("login/updatepasswordsuccess");
	}

}
