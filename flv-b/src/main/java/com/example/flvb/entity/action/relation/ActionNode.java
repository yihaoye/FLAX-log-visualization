package com.example.flvb.entity.action.relation;

public class ActionNode {

	public String name;
	public int account;
	
	public ActionNode(){
		
	}
	
	public ActionNode(String actionName, int actionAccount){
		name = actionName;
		account = actionAccount;
	}
}
