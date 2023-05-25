package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 9:24
 */
@SpringBootTest
public class CourseBaseServiceTests {

  @Autowired
  CourseBaseInfoService courseBaseInfoService;

  @Test
  public void testCourseBaseMapper() {
    QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
    queryCourseParamsDto.setCourseName("java");

    PageParams pageParams = new PageParams();
    pageParams.setPageNo(1L);
    pageParams.setPageSize(5L);

    PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(
        pageParams, queryCourseParamsDto);
    System.out.printf(courseBasePageResult.toString());
  }
}
