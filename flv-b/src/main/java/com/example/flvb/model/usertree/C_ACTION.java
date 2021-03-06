package com.example.flvb.model.usertree;

import java.util.HashMap;
import java.util.Map;

public class C_ACTION {
	
	HashMap<String, S_ACTION> s_actions_map = new HashMap<String, S_ACTION>();
	private long startTime;
	private long endTime;
	private long stayTime;
	HashMap<String, Integer> pre_c_actions_map = new HashMap<String, Integer>();
	// public String last_pre_s_action = null;
	private int accessed_account;
	
	// constructor
	public C_ACTION() {
		this.startTime = 0;
		this.endTime = 0;
		this.stayTime = 0;
		this.accessed_account = 1;
	}

	// put in s_actions_map hashmap
	public void set_s_actions_map(String current_s_action, String last_c_action, String last_s_action, String start_Time, String[] s1_paras) {
		if (this.s_actions_map.containsKey(current_s_action)) {
			S_ACTION temp_s_action = this.s_actions_map.get(current_s_action);
			temp_s_action.access_account_increased();
		} else {
			this.s_actions_map.put(current_s_action, new S_ACTION());
		}
		
		S_ACTION temp_s_action = this.s_actions_map.get(current_s_action);
		temp_s_action.set_startTime(start_Time);
		if (last_c_action != null && last_s_action != null) {
			temp_s_action.add_pre_c_s_actions(last_c_action + " " + last_s_action);
		}
		/*
		for (String s1_para : s1_paras) { // Problem!!!!!!!
			temp_s_action.put_s1_paras_map(s1_para);
		}
		*/
	}
	
	// put in pre_s_actions_map hashmap
	public void add_pre_c_actions(String pre_c_action) {
		if (pre_c_action != null) {
			if (this.pre_c_actions_map.containsKey(pre_c_action)) {
				Integer temp = this.pre_c_actions_map.get(pre_c_action);
				temp++;
				this.pre_c_actions_map.put(pre_c_action, temp);
			} else {
				this.pre_c_actions_map.put(pre_c_action, 1);
			}
		}
	}
	
	// set the startTime of the s_action
	public void set_startTime(String time) {
		if (time == null) {
			this.startTime = 0;
		} else {
			String[] tokens = time.split(":");
			int hours = Integer.parseInt(tokens[0]);
			int minutes = Integer.parseInt(tokens[1]);
			int seconds = Integer.parseInt(tokens[2]);
			this.startTime = 3600 * hours + 60 * minutes + seconds;
		}
	}
	
	// set the endTime of the s_action
	public void set_endTime(String time) {
		if (time == null) {
			this.endTime = 0;
		} else {
			String[] tokens = time.split(":");
			int hours = Integer.parseInt(tokens[0]);
			int minutes = Integer.parseInt(tokens[1]);
			int seconds = Integer.parseInt(tokens[2]);
			this.endTime = 3600 * hours + 60 * minutes + seconds;
		}
	}
	
	// calculate the stayTime of the s_action
	public void calculate_stayTime() {
		if (this.endTime > this.startTime) {
			this.stayTime += this.endTime - this.startTime;
		} else {
			this.stayTime += this.endTime + 43200 - this.startTime; // 43200seconds means 12 hours
		}
	}
	
	// increase accessed account
	public void access_account_increased() {
		this.accessed_account++;
	}
	
}
