package com.zcxd.pda.dto;

import java.util.List;

import lombok.Data;

@Data
public class BoxpackEmpDTO {
	private String empName;
	
	private String photo;
	
	private List<FingerprintDTO> fingerprint;
}
