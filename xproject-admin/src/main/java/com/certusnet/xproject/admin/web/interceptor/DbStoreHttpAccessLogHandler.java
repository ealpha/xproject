package com.certusnet.xproject.admin.web.interceptor;

import org.springframework.http.MediaType;

import com.certusnet.xproject.admin.model.AdminUser;
import com.certusnet.xproject.admin.model.AdminUserAccessLog;
import com.certusnet.xproject.admin.service.AdminUserAccessLogService;
import com.certusnet.xproject.common.support.HttpAccessLogging.LoggingType;
import com.certusnet.xproject.common.util.CollectionUtils;
import com.certusnet.xproject.common.util.DateTimeUtils;
import com.certusnet.xproject.common.util.JsonUtils;
import com.certusnet.xproject.common.util.SpringUtils;
import com.certusnet.xproject.common.web.springmvc.interceptor.AbstractHttpAccessLogHandler;
import com.certusnet.xproject.common.web.springmvc.interceptor.HttpAccessLog;

public class DbStoreHttpAccessLogHandler extends AbstractHttpAccessLogHandler<AdminUser> {

	private final AdminUserAccessLogService adminUserAccessLogService;
	
	public DbStoreHttpAccessLogHandler(HttpAccessLog<AdminUser> httpAccessLog) {
		super(httpAccessLog);
		this.adminUserAccessLogService = SpringUtils.getBean(AdminUserAccessLogService.class);
	}

	public LoggingType getLoggingType() {
		return LoggingType.DB;
	}

	public void handleLogger(HttpAccessLog<AdminUser> httpAccessLog) throws Exception {
		AdminUserAccessLog accessLog = new AdminUserAccessLog();
		accessLog.setTitle(httpAccessLog.getTitle());
		accessLog.setUri(httpAccessLog.getUri());
		accessLog.setMethod(httpAccessLog.getMethod());
		accessLog.setRequestHeader(CollectionUtils.isEmpty(httpAccessLog.getRequestHeader()) ? null : JsonUtils.object2Json(httpAccessLog.getRequestHeader()));
		accessLog.setRequestContentType(httpAccessLog.getRequestContentType() == null ? null : httpAccessLog.getRequestContentType().toString());
		accessLog.setRequestParameter(JsonUtils.object2Json(httpAccessLog.getRequestParameter()));
		accessLog.setAccessUserId(httpAccessLog.getAccessUser().getUserId());
		accessLog.setAccessTime(httpAccessLog.getAccessTime());
		accessLog.setClientIpAddr(httpAccessLog.getClientIpAddr());
		accessLog.setServerIpAddr(httpAccessLog.getServerIpAddr());
		accessLog.setProcessTime1(httpAccessLog.getProcessTime1());
		accessLog.setProcessTime2(httpAccessLog.getProcessTime2());
		accessLog.setLoggingCompleted(httpAccessLog.isLoggingCompleted());
		accessLog.setAsynRequest(httpAccessLog.isAsynRequest());
		accessLog.setResponseContentType(httpAccessLog.getResponseContentType() == null ? null : httpAccessLog.getResponseContentType().toString());
		if(httpAccessLog.getResponseResult() != null){
			if(httpAccessLog.getResponseResult() instanceof String){
				accessLog.setResponseResult(httpAccessLog.getResponseResult().toString());
			}else if(httpAccessLog.getResponseContentType() != null && MediaType.APPLICATION_JSON_UTF8.getType().equals(httpAccessLog.getResponseContentType().getType())){
				accessLog.setResponseResult(JsonUtils.object2Json(httpAccessLog.getResponseResult()));
			}
		}
		accessLog.setCreateTime(DateTimeUtils.formatNow());
		adminUserAccessLogService.recordUserAccessLog(accessLog);
	}

}
