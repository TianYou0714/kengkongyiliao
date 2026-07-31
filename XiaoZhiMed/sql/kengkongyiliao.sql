-- ============================================================
-- 可控医疗（原小智医疗）业务数据库初始化脚本
-- 数据库沿用 xiaozhi，与 application.properties 中配置一致
-- ============================================================

CREATE DATABASE IF NOT EXISTS xiaozhi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE xiaozhi;

-- ---------------- 预约信息表（原有） ----------------
CREATE TABLE IF NOT EXISTS appointment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username     VARCHAR(50)  NOT NULL COMMENT '预约人姓名',
    id_card      VARCHAR(18)  NOT NULL COMMENT '身份证号',
    department   VARCHAR(50)  NOT NULL COMMENT '预约科室',
    date         VARCHAR(20)  NOT NULL COMMENT '预约日期（yyyy-MM-dd）',
    time         VARCHAR(20)  NOT NULL COMMENT '预约时间（HH:mm）',
    doctor_name  VARCHAR(50)  DEFAULT NULL COMMENT '医生姓名'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='预约信息表';

-- ---------------- 科室信息表 ----------------
CREATE TABLE IF NOT EXISTS department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL COMMENT '科室名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '科室简介'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='科室信息表';

-- ---------------- 医生信息表 ----------------
CREATE TABLE IF NOT EXISTS doctor (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name          VARCHAR(50) NOT NULL COMMENT '医生姓名',
    title         VARCHAR(50) DEFAULT NULL COMMENT '职称',
    department_id BIGINT      DEFAULT NULL COMMENT '所属科室ID',
    specialty     VARCHAR(200) DEFAULT NULL COMMENT '擅长领域',
    phone         VARCHAR(20) DEFAULT NULL COMMENT '联系电话'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='医生信息表';

-- ---------------- 患者信息表 ----------------
CREATE TABLE IF NOT EXISTS patient (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name     VARCHAR(50) NOT NULL COMMENT '患者姓名',
    gender   VARCHAR(10) DEFAULT NULL COMMENT '性别',
    age      INT         DEFAULT NULL COMMENT '年龄',
    phone    VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    id_card  VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    address  VARCHAR(200) DEFAULT NULL COMMENT '住址'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='患者信息表';

-- ---------------- 示例数据 ----------------
INSERT INTO department (name, description) VALUES
('内科', '常见内科疾病诊治，如呼吸系统、消化系统疾病等'),
('外科', '普外科、骨科等手术治疗科室'),
('儿科', '儿童常见病、多发病的诊治'),
('妇产科', '妇科疾病诊治及孕产妇保健'),
('眼科', '眼部疾病诊治与视力矫正'),
('口腔科', '口腔疾病诊治、牙齿修复与正畸');

INSERT INTO doctor (name, title, department_id, specialty, phone) VALUES
('张伟', '主任医师', 1, '慢性支气管炎、哮喘', '13800000001'),
('李芳', '副主任医师', 1, '胃炎、消化性溃疡', '13800000002'),
('王强', '主任医师', 2, '骨折复位、关节置换', '13800000003'),
('刘洋', '主治医师', 3, '小儿呼吸道感染', '13800000004'),
('陈静', '副主任医师', 4, '妇科炎症、产前检查', '13800000005'),
('杨帆', '主治医师', 5, '白内障、近视矫正', '13800000006');

INSERT INTO patient (name, gender, age, phone, id_card, address) VALUES
('赵磊', '男', 32, '13911111111', '110101199201010011', '北京市海淀区中关村大街1号'),
('孙丽', '女', 28, '13922222222', '110101199605050022', '北京市朝阳区建国路88号'),
('周杰', '男', 45, '13933333333', '110101197903030033', '北京市西城区西直门南大街2号');
