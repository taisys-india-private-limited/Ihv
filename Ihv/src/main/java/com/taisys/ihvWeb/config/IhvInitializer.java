package com.taisys.ihvWeb.config;

import javax.servlet.Filter;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class IhvInitializer extends AbstractAnnotationConfigDispatcherServletInitializer implements WebApplicationInitializer {

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { IhvConfig.class };
	}
	
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[]{new CORSFilter()};
	}

}
