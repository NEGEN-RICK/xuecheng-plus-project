package com.xuecheng.content;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeachplanController {

  @Autowired
  private TeachplanService teachplanService;

  @GetMapping("/teachplan/{courseId}/tree-nodes")
  public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
    List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

    return teachplanTree;
  }

  @PostMapping("/teachplan")
  public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
    teachplanService.saveTeachplan(saveTeachplanDto);
  }

  @DeleteMapping("/teachplan/{teachplanId}")
  public void deleteTeachPlan(@PathVariable Long teachplanId) {
    teachplanService.deleteTeachplan(teachplanId);
  }

}
