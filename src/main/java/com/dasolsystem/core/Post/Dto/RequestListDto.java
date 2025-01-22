package com.dasolsystem.core.Post.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestListDto {

    private Integer page =0;
    private Integer pageSize=0;

    public Integer getPage(){
        page = page-1;
        if(page<0){
            page = 0;
        }
        return page;
    }
}
