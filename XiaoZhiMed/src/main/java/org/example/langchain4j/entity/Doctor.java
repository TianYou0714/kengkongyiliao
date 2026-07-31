package org.example.langchain4j.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 医生信息表
 */
@Data
@TableName("doctor")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 医生姓名
     */
    @TableField("name")
    private String name;

    /**
     * 职称（主任医师/副主任医师/主治医师/住院医师）
     */
    @TableField("title")
    private String title;

    /**
     * 所属科室ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 擅长领域
     */
    @TableField("specialty")
    private String specialty;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;
}
