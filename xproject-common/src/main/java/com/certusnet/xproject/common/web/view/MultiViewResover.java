package com.certusnet.xproject.common.web.view;

import java.util.Locale;
import java.util.Map;

import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.certusnet.xproject.common.util.StringUtils;

public class MultiViewResover implements ViewResolver, InitializingBean, Ordered  {
	
	/** 默认视图类型 */
	public static final String DEFAULT_VIEW_TYPE = "html";
	
	private Map<String,ViewResolver> viewResolvers;
	
	private String defaultViewType = DEFAULT_VIEW_TYPE;
	
	private int order = Integer.MAX_VALUE;
	
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		String viewType = this.defaultViewType;
		viewName = StringUtils.stripStart(viewName, "/");
		int index = viewName.lastIndexOf(".");
		if(index > 0){
			viewType = viewName.substring(index + 1);
			if(!this.viewResolvers.containsKey(viewType)){
				viewType = this.defaultViewType;
			}
			viewName = viewName.substring(0, index);
		}
		ViewResolver viewResolver = this.viewResolvers.get(viewType);
		return viewResolver.resolveViewName(viewName, locale);
	}
	
	public Map<String, ViewResolver> getViewResolvers() {
		return viewResolvers;
	}

	public void setViewResolvers(Map<String, ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	public String getDefaultViewType() {
		return defaultViewType;
	}

	public void setDefaultViewType(String defaultViewType) {
		if(defaultViewType != null){
			this.defaultViewType = defaultViewType.toLowerCase();
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notEmpty(viewResolvers, "Property 'viewResolvers' can not be empty!");
		Assert.isTrue(viewResolvers.containsKey(defaultViewType), String.format("Property 'viewResolvers' must be contains a ViewResolver with key '%s'!", defaultViewType));
	}
	
}
