package com.xlingmao.vncdemo.utils;

public class CommandResult {
	public String errorMsg;
	public int result;
	public String successMsg;

	public CommandResult(int paramInt) {
		this.result = paramInt;
	}

	public CommandResult(int paramInt, String paramString1, String paramString2) {
		this.result = paramInt;
		this.successMsg = paramString1;
		this.errorMsg = paramString2;
	}

	public String toString() {
		return "CommandResult [result=" + this.result + ", successMsg=" + this.successMsg + ", errorMsg="+ this.errorMsg + "]";
	}
}
