package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

  @Autowired
  CourseCategoryMapper courseCategoryMapper;

  @Override
  public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
    List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

    // 找到每个节点的子节点,最后封装成List<CourseCategoryTreeDto>
    // 先将list转map,key就是id,value就是CourseCategoryTreeDto对象,目的就是为了方便从map中取结点
    Map<String, CourseCategoryTreeDto> collect = courseCategoryTreeDtos.stream()
        .filter(item -> !id.equals(item.getId()))
        .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
    // 定义最终返回的List
    List<CourseCategoryTreeDto> list = new ArrayList<>();
    // 遍历List<CourseCategoryTreeDto>, 一边遍历一边找子节点放在父节点的childTreeNodes
    courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item -> {
      if (item.getParentid().equals(id)) {
        list.add(item);
      }
      CourseCategoryTreeDto courseCategoryTreeDto = collect.get(item.getParentid());
      if (courseCategoryTreeDto != null) {
        if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
          courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
        }
        courseCategoryTreeDto.getChildrenTreeNodes().add(item);
      }
    });

    return list;
  }
}
