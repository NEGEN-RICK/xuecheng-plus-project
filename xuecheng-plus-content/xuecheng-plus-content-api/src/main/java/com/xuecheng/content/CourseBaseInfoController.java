package com.xuecheng.content;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseBaseInfoController {

  @Autowired
  CourseBaseInfoService courseBaseInfoService;

  @PostMapping("/course/list")
  public PageResult<CourseBase> list(PageParams pageParams,
      @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
    PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(
        pageParams, queryCourseParamsDto);

    return courseBasePageResult;
  }

  @PostMapping("/course")
  public CourseBaseInfoDto createCourseBase(
      @RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto) {
    //获取到用户所属机构的id
    Long companyId = 1232141425L;
    CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

    return courseBase;
  }

  @GetMapping("/course/{courseId}")
  public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
    CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

    return courseBaseInfo;
  }

  @PutMapping("/course")
  public CourseBaseInfoDto updateCourseBase(
      @RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto) {
    Long companyId = 1232141425L;
    CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(companyId, editCourseDto);

    return courseBaseInfoDto;
  }
}
