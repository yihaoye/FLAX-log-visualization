package com.example.flvb.model.usertree;

import java.util.HashMap;
import java.util.Map;

public class USER {
	
	public String userID = null;
	HashMap<String, C_ACTION> c_actions_map = new HashMap<String, C_ACTION>();
	private long startTime;
	private long endTime;
	private long stayTime;
	public String last_pre_c_action = null;
	public String last_pre_s_action = null;
	private int actions_account;
	
	// constructor
	public USER() {
		this.startTime = 0;
		this.endTime = 0;
		this.stayTime = 0;
		this.actions_account = 1;
	}
	
	
	
	/**************              Refered functions 1 (carry out by order for dealing with c_s_actions)                ******************/
	// set stayTime for last c_s_action
	public void calculate_last_action_stayTime(String time) {
		if (this.last_pre_c_action != null && this.last_pre_s_action != null) {
			// calculate last c_action stayTime
			this.c_actions_map.get(this.last_pre_c_action).set_endTime(time);
			this.c_actions_map.get(this.last_pre_c_action).calculate_stayTime();
			// clear the c_action startTime and endTime
			this.c_actions_map.get(this.last_pre_c_action).set_startTime(null);
			this.c_actions_map.get(this.last_pre_c_action).set_endTime(null);
			
			// calculate last s_action stayTime
			this.c_actions_map.get(this.last_pre_c_action).s_actions_map.get(this.last_pre_s_action).set_endTime(time);
			this.c_actions_map.get(this.last_pre_c_action).s_actions_map.get(this.last_pre_s_action).calculate_stayTime();
			// clear the s_action startTime and endTime
			this.c_actions_map.get(this.last_pre_c_action).s_actions_map.get(this.last_pre_s_action).set_startTime(null);
			this.c_actions_map.get(this.last_pre_c_action).s_actions_map.get(this.last_pre_s_action).set_endTime(null);
		}
		
		// calculate user stayTime
		if (this.last_pre_c_action != null && this.last_pre_s_action != null) {
			if (endTime > startTime) {
				stayTime = endTime - startTime;
			} else {
				stayTime = endTime + 43200 - startTime; // 43200seconds means 12 hours
			}
		}
	}
	
	// set c_s_actions hashmap, add/increase C_ACTION/SACTION, set pre_action/startTime for both action, set s1.para
	public void set_c_s_actions_map(String current_c_action, String current_s_action, String startTime, String[] s1_paras) {
		
		// set c_actions_map (add/increase C_ACTION to c_actions_map, set pre_action/startTime for the map)
		set_c_actions_map(current_c_action, this.last_pre_c_action, startTime);
		
		// set s_actions_map (add/increase S_ACTION to s_actions_map, set pre_action/startTime for the map)
		C_ACTION temp_c_action = this.c_actions_map.get(current_c_action);
		temp_c_action.set_s_actions_map(current_s_action, this.last_pre_c_action, this.last_pre_s_action, startTime, s1_paras);
		
	}
	
	// set last pre_c_s_action
	public void update_last_action(String current_c_action, String current_s_action) {
		this.last_pre_c_action = current_c_action;
		this.last_pre_s_action = current_s_action;
	}
	
	// set userID
	public void setUserID(String input_user_id) {
		this.userID = input_user_id;
	}
	
	
	
	/**************              Refered functions 2 (for dealing USER inner variables)                ******************/
	// increase user's actions account
	public void actions_account_increased() {
		this.actions_account++;
	}
	
	// set the time for the user first behavior
	public void set_startTime(String time) {
		String[] tokens = time.split(":");
		int hours = Integer.parseInt(tokens[0]);
		int minutes = Integer.parseInt(tokens[1]);
		int seconds = Integer.parseInt(tokens[2]);
		this.startTime = 3600 * hours + 60 * minutes + seconds;
	}
	
	// set the time for the user last behavior
	public void set_endTime(String time) {
		String[] tokens = time.split(":");
		int hours = Integer.parseInt(tokens[0]);
		int minutes = Integer.parseInt(tokens[1]);
		int seconds = Integer.parseInt(tokens[2]);
		this.endTime = 3600 * hours + 60 * minutes + seconds;
	}
	
	
	
	
	/**************              Inner functions                 ******************/
	// put in c_actions_map hashmap, set pre_action/startTime for the map
	public void set_c_actions_map(String current_c_action, String last_c_action, String start_Time) {
		if (this.c_actions_map.containsKey(current_c_action)) {
			C_ACTION temp_c_action = this.c_actions_map.get(current_c_action);
			temp_c_action.access_account_increased();
		} else {
			this.c_actions_map.put(current_c_action, new C_ACTION());
		}
		
		C_ACTION temp_c_action = this.c_actions_map.get(current_c_action);
		temp_c_action.set_startTime(start_Time);
		temp_c_action.add_pre_c_actions(last_c_action);
	}

}
