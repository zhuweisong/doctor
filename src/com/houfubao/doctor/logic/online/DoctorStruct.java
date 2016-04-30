package com.houfubao.doctor.logic.online;


public class DoctorStruct {
	static public class BaseMessage {
		public BaseMessage(String ownerId) {
			this.ownerId = ownerId;
		}
		public String ownerId;
	}
	
	static public class MessageObj extends BaseMessage {
		public int int1;
		public int int2;
		public Object obj0;
		public Object obj1;
		public MessageObj(String ownerId, Object obj) {
			super(ownerId);
			this.obj0 = obj;
		}
		public MessageObj(String ownerId, Object obj, Object obj1) {
			super(ownerId);
			this.obj0 = obj;
			this.obj1 = obj1;
		}
		public MessageObj(String ownerId, int int1, int int2, Object obj, Object obj1) {
			super(ownerId);
			this.int1 = int1;
			this.int2 = int2;
			this.obj0 = obj;
			this.obj1 = obj1;
		}
	}
}
