package com.zcxd.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String value;
}
