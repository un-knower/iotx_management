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
	
	private String topic;
	
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
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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
		
		private Object val;
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Object getVal() {
			return val;
		}

		public void setVal(Object val) {
			this.val = val;
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
	
	public static class SensorMeta{
		
		private Integer dust_id;
		
		private String sensor_no;
		
		private Integer slave;
		
		private Integer plc_address;
		
		private String sensor_name;
		
		private String type;
		
		private String unit;
		
		private Integer job_time;
		
		private String max_limit;
		
		private String min_limit;
		
		private String description;
		
		private boolean is_used;

		public Integer getDust_id() {
			return dust_id;
		}

		public void setDust_id(Integer dust_id) {
			this.dust_id = dust_id;
		}

		public String getSensor_no() {
			return sensor_no;
		}

		public void setSensor_no(String sensor_no) {
			this.sensor_no = sensor_no;
		}

		public Integer getSlave() {
			return slave;
		}

		public void setSlave(Integer slave) {
			this.slave = slave;
		}

		public Integer getPlc_address() {
			return plc_address;
		}

		public void setPlc_address(Integer plc_address) {
			this.plc_address = plc_address;
		}

		public String getSensor_name() {
			return sensor_name;
		}

		public void setSensor_name(String sensor_name) {
			this.sensor_name = sensor_name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public Integer getJob_time() {
			return job_time;
		}

		public void setJob_time(Integer job_time) {
			this.job_time = job_time;
		}

		public String getMax_limit() {
			return max_limit;
		}

		public void setMax_limit(String max_limit) {
			this.max_limit = max_limit;
		}

		public String getMin_limit() {
			return min_limit;
		}

		public void setMin_limit(String min_limit) {
			this.min_limit = min_limit;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public boolean isIs_used() {
			return is_used;
		}

		public void setIs_used(boolean is_used) {
			this.is_used = is_used;
		}
		
	}
	
	public static enum Type{
		SEND,RECEIVE
	}
	
}
