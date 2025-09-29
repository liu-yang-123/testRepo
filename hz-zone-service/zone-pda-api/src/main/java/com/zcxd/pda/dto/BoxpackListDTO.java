package com.zcxd.pda.dto;

import java.util.List;

import lombok.Data;

@Data
public class BoxpackListDTO {
	
	private List<BoxpackDTO> list;
	
	private List<BoxpackDTO> shareList;
}
