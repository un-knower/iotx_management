package com.anosi.asset.model.mongo;

import java.util.UUID;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Message extends AbstractDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6513001095854167095L;
	
	private Header header;
	
	private Body body;
	
	private Response response;
	
	private Type type;
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public static class Header{
		
		@Indexed
		private String uniqueId = UUID.randomUUID().toString();
		
		private String type;
		
		@Indexed
		private String serialNo;

		public String getUniqueId() {
			return uniqueId;
		}

		public void setUniqueId(String uniqueId) {
			this.uniqueId = uniqueId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		
	}
	
	public static class Body{
		
		private String type;
		
		private Object value;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
		
	}
	
	public static class Response{
		
		private boolean status;
		
		private String reason;

		public boolean getStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}
		
	}
	
	public static enum Type{
		SEND,RECEIVE
	}

}
