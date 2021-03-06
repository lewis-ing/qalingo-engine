/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.web.mvc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hoteia.qalingo.core.Constants;
import org.hoteia.qalingo.core.ModelConstants;
import org.hoteia.qalingo.core.domain.EngineSetting;
import org.hoteia.qalingo.core.domain.EngineSettingValue;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeCommonMessage;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeReferenceDataMessage;
import org.hoteia.qalingo.core.i18n.message.CoreMessageSource;
import org.hoteia.qalingo.core.pojo.RequestData;
import org.hoteia.qalingo.core.service.EngineSettingService;
import org.hoteia.qalingo.core.service.ReferentialDataService;
import org.hoteia.qalingo.core.service.UrlService;
import org.hoteia.qalingo.core.web.mvc.viewbean.MonitoringViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.TrackingViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.ValueBean;
import org.hoteia.qalingo.core.web.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 
 * <p>
 * <a href="AbstractQalingoController.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author Denis Gosset <a href="http://www.hoteia.com"><i>Hoteia.com</i></a>
 * 
 */
public abstract class AbstractQalingoController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CoreMessageSource coreMessageSource;

	@Autowired
	protected EngineSettingService engineSettingService;
	
	@Autowired
    protected UrlService urlService;
	
	@Autowired
    protected ReferentialDataService referentialDataService;
	
	@Autowired
    protected RequestUtil requestUtil;
	
	/**
	 * 
	 */
	@ModelAttribute
	protected void initVelocityLayout(final HttpServletRequest request, final Model model) throws Exception {
		// Velocity layout mandatory attributes
        model.addAttribute(Constants.VELOCITY_LAYOUT_ATTRIBUTE_HEAD_META, "../_include/head-common-empty-content.vm");
		model.addAttribute(Constants.VELOCITY_LAYOUT_ATTRIBUTE_HEAD_CSS_META, "../_include/head-common-empty-content.vm");
		model.addAttribute(Constants.VELOCITY_LAYOUT_ATTRIBUTE_HEAD_CONTENT, "../_include/head-common-empty-content.vm");
		model.addAttribute(Constants.VELOCITY_LAYOUT_ATTRIBUTE_FOOTER_SCRIPT_CONTENT, "../_include/body-footer-empty-script-content.vm");
	}
	
	/**
	 * 
	 */
	@ModelAttribute
	protected void handleMessages(final HttpServletRequest request, final Model model) throws Exception {
		// WE USE SESSION FOR MESSAGES BECAUSE REDIRECT CLEAN REQUEST
		// ERROR MESSAGE
		String errorMessage = (String) request.getSession().getAttribute(Constants.ERROR_MESSAGE);
		if(StringUtils.isNotEmpty(errorMessage)){
			model.addAttribute(Constants.ERROR_MESSAGE, errorMessage);
			request.getSession().removeAttribute(Constants.ERROR_MESSAGE);
		}
        // WARNING MESSAGE
        String warningMessage = (String) request.getSession().getAttribute(Constants.WARNING_MESSAGE);
        if(StringUtils.isNotEmpty(warningMessage)){
            model.addAttribute(Constants.WARNING_MESSAGE, warningMessage);
            request.getSession().removeAttribute(Constants.WARNING_MESSAGE);
        }
		// INFO MESSAGE
		String infoMessage = (String) request.getSession().getAttribute(Constants.INFO_MESSAGE);
		if(StringUtils.isNotEmpty(infoMessage)){
			model.addAttribute(Constants.INFO_MESSAGE, infoMessage);
			request.getSession().removeAttribute(Constants.INFO_MESSAGE);
		}
		// SUCCESS MESSAGE
		String successMessage = (String) request.getSession().getAttribute(Constants.SUCCESS_MESSAGE);
		if(StringUtils.isNotEmpty(successMessage)){
			model.addAttribute(Constants.SUCCESS_MESSAGE, successMessage);
			request.getSession().removeAttribute(Constants.SUCCESS_MESSAGE);
		}
	}

	/**
	 * 
	 */
	@ModelAttribute(ModelConstants.TRACKING_VIEW_BEAN)
	protected TrackingViewBean initTracking(final HttpServletRequest request, final Model model) throws Exception {
		TrackingViewBean trackingViewBean = null;
    	final String contextValue = requestUtil.getCurrentContextNameValue();

	    EngineSetting webTrackingNumberEngineSetting = engineSettingService.getSettingWebTrackingNumber();
	    if(webTrackingNumberEngineSetting != null){
	        EngineSettingValue webTrackingNumberEngineSettingValue = webTrackingNumberEngineSetting.getEngineSettingValue(contextValue);
	        if(webTrackingNumberEngineSettingValue != null
	                && StringUtils.isNotEmpty(webTrackingNumberEngineSettingValue.getValue())){
	            trackingViewBean = new TrackingViewBean();
	            trackingViewBean.setTrackingNumber(webTrackingNumberEngineSettingValue.getValue());
	            
	            EngineSetting webTrackingNameEngineSetting = engineSettingService.getSettingWebTrackingName();
	            if(webTrackingNameEngineSetting != null){
	                EngineSettingValue webTrackingNameEngineSettingValue = webTrackingNameEngineSetting.getEngineSettingValue(contextValue);
	                if(webTrackingNameEngineSettingValue != null){
	                    trackingViewBean.setTrackingName(webTrackingNameEngineSettingValue.getValue());
	                }
	            }
	        }
	    }
		return trackingViewBean;
	}
	
    /**
     * 
     */
    @ModelAttribute(ModelConstants.URL_BACK)
    protected String initBackUrl(final HttpServletRequest request, final Model model) throws Exception {
        String url = requestUtil.getCurrentRequestUrl(request);
        List<String> excludedPatterns = requestUtil.getCommonUrlExcludedPatterns();
        excludedPatterns.add(url);
        return requestUtil.getLastRequestUrl(request, excludedPatterns);
    }

	/**
	 * 
	 */
	@ModelAttribute(ModelConstants.MONITORING_VIEW_BEAN)
	protected MonitoringViewBean initMonitoring(final HttpServletRequest request, final Model model) throws Exception {
		MonitoringViewBean monitoringViewBean = new MonitoringViewBean();
    	final String contextValue = requestUtil.getCurrentContextNameValue();
	    EngineSetting webMonitoringNumberEngineSetting = engineSettingService.getSettingWebMonitoringNumber();
	    if(webMonitoringNumberEngineSetting != null){
	        EngineSettingValue webMonitoringNumberEngineSettingValue = webMonitoringNumberEngineSetting.getEngineSettingValue(contextValue);
	        if(webMonitoringNumberEngineSettingValue != null
	                && StringUtils.isNotEmpty(webMonitoringNumberEngineSettingValue.getValue())){
	            monitoringViewBean = new MonitoringViewBean();
	            monitoringViewBean.setMonitoringNumber(webMonitoringNumberEngineSettingValue.getValue());
	            
	            EngineSetting webMonitoringNameEngineSetting = engineSettingService.getSettingWebMonitoringName();
	            EngineSettingValue webMonitoringNameEngineSettingValue = webMonitoringNameEngineSetting.getEngineSettingValue(contextValue);
	            if(webMonitoringNameEngineSettingValue != null){
	                monitoringViewBean.setMonitoringName(webMonitoringNameEngineSettingValue.getValue());
	            }
	        }
	    }
		return monitoringViewBean;
	}
	
	protected List<ValueBean> getCountries(final RequestData requestData) throws Exception {
        List<ValueBean> countriesValues = new ArrayList<ValueBean>();
        try {
            final Locale locale = requestData.getLocale();
            
            final Map<String, String> countries = referentialDataService.getCountriesByLocale(locale);
            Set<String> countriesKey = countries.keySet();
            for (Iterator<String> iterator = countriesKey.iterator(); iterator.hasNext();) {
                final String countryKey = (String) iterator.next();
                countriesValues.add(new ValueBean(countryKey.replace(Constants.COUNTRY_MESSAGE_PREFIX, ""), countries.get(countryKey)));
            }
            Collections.sort(countriesValues, new Comparator<ValueBean>() {
                @Override
                public int compare(ValueBean o1, ValueBean o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
        } catch (Exception e) {
            logger.error("", e);
        }
        return countriesValues;
    }
    
    /**
     * @throws Exception 
     * 
     */
    protected String getCurrentVelocityPath(HttpServletRequest request) throws Exception {
        final RequestData requestData = requestUtil.getRequestData(request);
        return requestUtil.getCurrentVelocityWebPrefix(requestData);
    }
    
	protected void addMessageError(BindingResult result, Exception e, String formKey, String fieldKey, String errorMessage){
        if(StringUtils.isEmpty(errorMessage)){
        	errorMessage = ""; // EMPTY VALUE TO EVENT VELOCITY MethodInvocationException
        }
        FieldError error = new FieldError(formKey, fieldKey, errorMessage);
        result.addError(error);
        result.rejectValue(error.getField(), "");
        if(e != null){
            logger.error(errorMessage, e);
        } else {
            logger.warn(errorMessage);
        }
    }
	
	/**
	 * @throws Exception 
	 * 
	 */
	protected void addSessionErrorMessage(HttpServletRequest request, String message) throws Exception {
		request.getSession().setAttribute(Constants.ERROR_MESSAGE, message);
	}
	
    /**
     * @throws Exception 
     * 
     */
    protected void addSessionWarningMessage(HttpServletRequest request, String message) throws Exception {
        request.getSession().setAttribute(Constants.WARNING_MESSAGE, message);
    }
    
	/**
	 * @throws Exception 
	 * 
	 */
	protected void addSessionInfoMessage(HttpServletRequest request, String message) throws Exception {
		request.getSession().setAttribute(Constants.INFO_MESSAGE, message);
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	protected void addSessionSuccessMessage(HttpServletRequest request, String message) throws Exception {
		request.getSession().setAttribute(Constants.SUCCESS_MESSAGE, message);
	}

	   
    /**
     * @throws Exception 
     * 
     */
    protected void addRequestErrorMessage(HttpServletRequest request, String message) throws Exception {
        request.setAttribute(Constants.ERROR_MESSAGE, message);
    }
    
    /**
     * @throws Exception 
     * 
     */
    protected void addRequestWarningMessage(HttpServletRequest request, String message) throws Exception {
        request.setAttribute(Constants.WARNING_MESSAGE, message);
    }
    
    /**
     * @throws Exception 
     * 
     */
    protected void addRequestInfoMessage(HttpServletRequest request, String message) throws Exception {
        request.setAttribute(Constants.INFO_MESSAGE, message);
    }
    
    /**
     * @throws Exception 
     * 
     */
    protected void addRequestSuccessMessage(HttpServletRequest request, String message) throws Exception {
        request.setAttribute(Constants.SUCCESS_MESSAGE, message);
    }
    
    protected String getCommonMessage(ScopeCommonMessage scope, String key, Locale locale) {
        return getCommonMessage(scope.getPropertyKey(), key, locale);
    }

    protected String getCommonMessage(ScopeCommonMessage scope, String key, Object[] params, Locale locale) {
        return getCommonMessage(scope.getPropertyKey(), key, params, locale);
    }

    protected String getCommonMessage(String scope, String key, Locale locale) {
        return coreMessageSource.getCommonMessage(scope, key, locale);
    }

    protected String getCommonMessage(String scope, String key, Object[] params, Locale locale) {
        return coreMessageSource.getCommonMessage(scope, key, params, locale);
    }

    protected String getReferenceData(ScopeReferenceDataMessage scope, String key, Locale locale) {
        return getReferenceData(scope.getPropertyKey(), key, locale);
    }

    protected String getReferenceData(ScopeReferenceDataMessage scope, String key, Object[] params, Locale locale) {
        return getReferenceData(scope.getPropertyKey(), key, params, locale);
    }

    protected String getReferenceData(String scope, String key, Locale locale) {
        return coreMessageSource.getReferenceData(scope, key, locale);
    }

    protected String getReferenceData(String scope, String key, Object[] params, Locale locale) {
        return coreMessageSource.getReferenceData(scope, key, params, locale);
    }

}