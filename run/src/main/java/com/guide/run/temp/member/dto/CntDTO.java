package com.guide.run.temp.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CntDTO {
    private int trainingCnt; //참여한 훈련 수
    private int competitionCnt; //참여한 대회 수
}
