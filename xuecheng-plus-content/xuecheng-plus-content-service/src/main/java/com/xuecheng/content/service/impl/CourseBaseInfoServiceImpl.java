package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

  @Autowired
  CourseBaseMapper courseBaseMapper;

  @Autowired
  CourseMarketMapper courseMarketMapper;

  @Override
  public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams,
      QueryCourseParamsDto courseParamsDto) {

    LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(!StringUtils.isEmpty(courseParamsDto.getCourseName()), CourseBase::getName,
        courseParamsDto.getCourseName());
    queryWrapper.eq(!StringUtils.isEmpty(courseParamsDto.getAuditStatus()),
        CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
    queryWrapper.eq(!StringUtils.isEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus,
        courseParamsDto.getPublishStatus());

    Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
    Page<CourseBase> page = courseBaseMapper.selectPage(courseBasePage, queryWrapper);

    PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(page.getRecords(),
        page.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());

    return courseBasePageResult;
  }

  @Transactional
  @Override
  public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

    //向课程基本信息表course_base写入数据
    CourseBase courseBaseNew = new CourseBase();
    //将传入的页面的参数放到courseBaseNew对象
//        courseBaseNew.setName(dto.getName());
//        courseBaseNew.setDescription(dto.getDescription());
    //上边的从原始对象中get拿数据向新对象set，比较复杂
    BeanUtils.copyProperties(dto, courseBaseNew);//只要属性名称一致就可以拷贝
    courseBaseNew.setCompanyId(companyId);
    courseBaseNew.setCreateDate(LocalDateTime.now());
    //审核状态默认为未提交
    courseBaseNew.setAuditStatus("202002");
    //发布状态为未发布
    courseBaseNew.setStatus("203001");
    //插入数据库
    int insert = courseBaseMapper.insert(courseBaseNew);
    if (insert <= 0) {
      throw new RuntimeException("添加课程失败");
    }

    //向课程营销系course_market写入数据
    CourseMarket courseMarketNew = new CourseMarket();
    //将页面输入的数据拷贝到courseMarketNew
    BeanUtils.copyProperties(dto, courseMarketNew);
    //课程的id
    Long courseId = courseBaseNew.getId();
    courseMarketNew.setId(courseId);
    //保存营销信息
    saveCourseMarket(courseMarketNew);
    //从数据库查询课程的详细信息，包括两部分
    CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

    return courseBaseInfo;
  }

  //查询课程信息
  public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

    //从课程基本信息表查询
    CourseBase courseBase = courseBaseMapper.selectById(courseId);
    if (courseBase == null) {
      return null;
    }
    //从课程营销表查询
    CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

    //组装在一起
    CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
    BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
    if (courseMarket != null) {
      BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
    }

    return courseBaseInfoDto;
  }

  @Override
  public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

    Long courseId = editCourseDto.getId();
    CourseBase courseBase = courseBaseMapper.selectById(courseId);
    if (courseBase == null) {
      XueChengPlusException.cast("课程不存在");
    }

    if (!companyId.equals(courseBase.getCompanyId())) {
      XueChengPlusException.cast("本机构只能修改本机构的课程");
    }

    // 封装数据
    BeanUtils.copyProperties(editCourseDto, courseBase);
    courseBase.setChangeDate(LocalDateTime.now());

    int i = courseBaseMapper.updateById(courseBase);
    if (i <= 0) {
      XueChengPlusException.cast("修改课程失败");
    }

    // 保存营销信息
    //向课程营销系course_market写入数据
    CourseMarket courseMarketNew = new CourseMarket();
    //将页面输入的数据拷贝到courseMarketNew
    BeanUtils.copyProperties(editCourseDto, courseMarketNew);
    //保存营销信息
    saveCourseMarket(courseMarketNew);

    CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
    return courseBaseInfo;
  }

  //单独写一个方法保存营销信息，逻辑：存在则更新，不存在则添加
  private int saveCourseMarket(CourseMarket courseMarketNew) {

    //从数据库查询营销信息,存在则更新，不存在则添加
    Long id = courseMarketNew.getId();//主键
    CourseMarket courseMarket = courseMarketMapper.selectById(id);
    if (courseMarket == null) {
      //插入数据库
      int insert = courseMarketMapper.insert(courseMarketNew);
      return insert;
    } else {
      //将courseMarketNew拷贝到courseMarket
      BeanUtils.copyProperties(courseMarketNew, courseMarket);
      courseMarket.setId(courseMarketNew.getId());
      //更新
      int i = courseMarketMapper.updateById(courseMarket);
      return i;
    }
  }

}
