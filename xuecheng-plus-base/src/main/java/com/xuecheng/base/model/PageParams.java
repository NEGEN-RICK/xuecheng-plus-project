package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 分页查询参数
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

  private Long pageNo = 1L;
  private Long pageSize = 10L;
}
