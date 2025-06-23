package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 保存员工信息
     *
     * @param employeeDTO 员工数据传输对象，包含员工相关信息
     * @return 返回保存结果，成功则返回成功结果
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        // 记录员工保存日志
        log.info("员工保存：{}", employeeDTO);

        // 调用员工服务的保存方法
        employeeService.save(employeeDTO);

        // 返回成功结果
        return Result.success();
    }


    /**
     * 处理员工信息的分页查询请求
     *
     * @param employeePageQueryDTO 包含分页查询参数的DTO对象，用于传递分页查询的条件
     * @return 返回一个Result对象，其中包含分页查询的结果
     */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 记录员工分页查询的日志，以便于调试和审计
        log.info("员工分页查询：{}", employeePageQueryDTO);

        // 调用员工服务的分页查询方法，获取查询结果
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        // 返回一个表示成功的Result对象，其中包含分页查询结果
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result<?> startOrStop(@PathVariable Integer status,Long id){
        log.info("员工状态：{},员工id：{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

}
