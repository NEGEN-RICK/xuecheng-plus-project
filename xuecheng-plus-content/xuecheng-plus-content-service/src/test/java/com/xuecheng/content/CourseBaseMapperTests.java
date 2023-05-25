package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.mapper.CourseBaseMapper;
import org.junit.jupiter.api.Assertions;
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
public class CourseBaseMapperTests {

  @Autowired
  CourseBaseMapper courseBaseMapper;

  @Test
  public void testCourseBaseMapper() {
//    CourseBase courseBase = courseBaseMapper.selectById(18);
//    Assertions.assertNotNull(courseBase);

    QueryCourseParamsDto courseParamsDto = new QueryCourseParamsDto();
    courseParamsDto.setCourseName("java");

    LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(!StringUtils.isEmpty(courseParamsDto.getCourseName()), CourseBase::getName,
        courseParamsDto.getCourseName());
    queryWrapper.eq(!StringUtils.isEmpty(courseParamsDto.getAuditStatus()),
        CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
    queryWrapper.eq(!StringUtils.isEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus,
        courseParamsDto.getPublishStatus());

    Page<CourseBase> courseBasePage = new Page<>(1, 10);
    Page<CourseBase> page = courseBaseMapper.selectPage(courseBasePage, queryWrapper);

    PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(page.getRecords(),
        page.getTotal(), 1, 10);
    System.out.printf(courseBasePageResult.toString());
  }
}
