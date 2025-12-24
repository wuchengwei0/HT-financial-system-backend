package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.User;
import com.example.scaffold.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }

    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody User user) {
        boolean saved = userService.save(user);
        if (saved) {
            return Result.success(true);
        } else {
            return Result.fail("保存失败");
        }
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody User user) {
        boolean updated = userService.updateById(user);
        if (updated) {
            return Result.success(true);
        } else {
            return Result.fail("更新失败");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean deleted = userService.removeById(id);
        if (deleted) {
            return Result.success(true);
        } else {
            return Result.fail("删除失败");
        }
    }
}
