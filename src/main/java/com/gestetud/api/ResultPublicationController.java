package com.gestetud.api;
import com.gestetud.model.ResultPublication;
import com.gestetud.service.ResultPublicationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/courses/{courseId}/results")
public class ResultPublicationController {
 private final ResultPublicationService service;
 public ResultPublicationController(ResultPublicationService service){this.service=service;}
 @PostMapping("/publish") public List<ResultPublication> publish(@PathVariable Long courseId){return service.publishCourse(courseId);}
}
