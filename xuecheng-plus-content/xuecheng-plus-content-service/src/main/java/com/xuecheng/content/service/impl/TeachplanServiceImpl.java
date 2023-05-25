package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeachplanServiceImpl implements TeachplanService {

  @Autowired
  private TeachplanMapper teachplanMapper;

  @Autowired
  private TeachplanMediaMapper teachplanMediaMapper;

  @Override
  public List<TeachplanDto> findTeachplanTree(Long courseId) {
    List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);

    return teachplanDtos;
  }

  @Override
  public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
    //通过课程计划id判断是新增和修改
    Long teachplanId = saveTeachplanDto.getId();
    if (teachplanId == null) {
      //新增
      Teachplan teachplan = new Teachplan();
      BeanUtils.copyProperties(saveTeachplanDto, teachplan);
      //确定排序字段，找到它的同级节点个数，排序字段就是个数加1  select count(1) from teachplan where course_id=117 and parentid=268
      Long parentid = saveTeachplanDto.getParentid();
      Long courseId = saveTeachplanDto.getCourseId();
      int teachplanCount = getTeachplanCount(courseId, parentid);
      teachplan.setOrderby(teachplanCount);
      teachplanMapper.insert(teachplan);
    } else {
      //修改
      Teachplan teachplan = teachplanMapper.selectById(teachplanId);
      //将参数复制到teachplan
      BeanUtils.copyProperties(saveTeachplanDto, teachplan);
      teachplanMapper.updateById(teachplan);
    }
  }

  @Override
  @Transactional
  public void deleteTeachplan(Long teachplanId) {
    Teachplan teachplan = teachplanMapper.selectById(teachplanId);
    if (teachplan.getParentid() == 0) {
      LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper = queryWrapper.eq(Teachplan::getParentid, teachplanId);
      List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
      for (Teachplan item : teachplans) {
        deleteTeachplanChild(item);
      }
      teachplanMapper.deleteById(teachplanId);
    } else {
      deleteTeachplanChild(teachplan);
    }
  }

  // 删除Teachplan子节点以及所有关联
  private void deleteTeachplanChild(Teachplan teachplan) {
    LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper = queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
    teachplanMediaMapper.delete(queryWrapper);
    teachplanMapper.deleteById(teachplan.getId());
  }

  private int getTeachplanCount(Long courseId, Long parentId) {
    LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId)
        .eq(Teachplan::getParentid, parentId);
    Integer count = teachplanMapper.selectCount(queryWrapper);
    return count + 1;
  }
}
