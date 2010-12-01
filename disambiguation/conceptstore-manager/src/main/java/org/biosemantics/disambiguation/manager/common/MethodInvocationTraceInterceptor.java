package org.biosemantics.disambiguation.manager.common;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;

public class MethodInvocationTraceInterceptor extends CustomizableTraceInterceptor {

	private static final long serialVersionUID = 7858126041823551068L;
	@Override
	protected void writeToLog(Log logger, String message, Throwable ex) {
	    if (ex != null) {
	        logger.debug(message, ex);
	    } else {
	        logger.debug(message);
	    }
	  }


	  protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
	    return true;
	  }

}
