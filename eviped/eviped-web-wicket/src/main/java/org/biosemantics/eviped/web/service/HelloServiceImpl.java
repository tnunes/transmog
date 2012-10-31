package org.biosemantics.eviped.web.service;

import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

	public String sayHello() {
		return "Eviped Web App (Beta)";
	}

}
