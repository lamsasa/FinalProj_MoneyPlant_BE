package com.MoneyPlant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDto {

//    private Long scId;
    private Long userId; // join 으로 사용 예정
    private String calId;
    private String scName;
    private int scColor;
    private String scDate;
    private int scBudget;

}
