package com.example.sr.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.sr.domain.User;
import com.example.sr.models.AuthenticationRequest;
import com.example.sr.models.AuthenticationResponse;
import com.example.sr.repo.UserRepository;
import com.example.sr.service.MyUserDetailsService;
import com.example.sr.util.JwtUtil;

@RestController
public class FirstController {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;
	
	 @Autowired
	    UserRepository userRepository;
	 
	@Autowired
	private MyUserDetailsService userDetailsService;

	@RequestMapping(value = "/helloAdmin", method = RequestMethod.GET )
	public String firstPageForAdmin(HttpServletRequest request) {
		extractUserDetailsForPermissionCheck(request);
		return "Hello Admin World";
	}
	
	@RequestMapping(value =  "/helloSalesMan" , method = RequestMethod.GET)
	public String firstPageForSalesMan(HttpServletRequest request) {
		extractUserDetailsForPermissionCheck(request);
		return "Hello Salesman World";
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {		
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			
			throw new Exception("Incorrect username or password", e);
		}


		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		
		final String jwt = jwtTokenUtil.generateToken(userDetails);		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	private void extractUserDetailsForPermissionCheck(HttpServletRequest request){
		boolean hasAccess=false;
		if (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer ")) {
            String jwt = request.getHeader("Authorization").substring(7);
            String userName = jwtTokenUtil.extractUsername(jwt);            
            hasAccess=CheckPermission(userName,"Admin");   
            if(!hasAccess)
            	throw new AccessDeniedException("You are not authorized to perform this action");
        }
	}
	
	private boolean CheckPermission(String userName,String action) {
		Optional<User> user = userRepository.findByUserName(userName);
		
		if((user.get().getRoles().equalsIgnoreCase("EMP") && action.equalsIgnoreCase("EMP"))
				|| user.get().getRoles().equalsIgnoreCase("Admin"))
		{
		return true;
		}
		
		return false;
		
	}

}
