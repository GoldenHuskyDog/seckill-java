# seckill-java
# 商品秒杀系统


## 项目简介

本项目前后端分离，前端基于`Vue`+`Vue-router`+`Vuex`+`Element-ui`+`Axios`。后端基于`SpringBoot` +`Redis`+ `RabbitMQ` + `Shiro` + `JWT`+ `MySQL`实现。

前台实现了**用户登录**，**秒杀商品展示**，**秒杀商品**，**秒杀商品**等功能。

后台实现了**用户管理**，**商品管理**，**订单管理**等功能。

**后端接口全部采用Resultful风格，前端使用axios与后端进行通信。**

前端项目地址：https://github.com/GoldenHuskyDog/seckill-vue

后端项目地址：https://github.com/GoldenHuskyDog/seckill-java

## 项目已部署

商城秒杀系统： http://47.93.139.186/

`Mysql`、`Redis`、`RabbitMQ`均已部署在47.93.139.186上

## 技术栈

- **前端：**`Vue`+`Vue-router`+`Vuex`+`Element-ui`+`Axios`
- **后端：**`SpringBoot` +`Redis`+ `RabbitMQ` + `Shiro` + `JWT`
- **数据库：**`Mysql`

## 运行项目

**前端运行**

```
1. 克隆项目

git clone https://github.com/GoldenHuskyDog/seckill-vue.git

2. 安装

cd seckill-vue
npm install

3. 运行

npm run serve
前端项目默认运行在 localhost:8080.

4. 打包和部署

npm run build
将打包好的项目复制到ngnix默认目录的下的html目录下
```

**后端运行**

```
1. 克隆项目

git clone https://github.com/GoldenHuskyDog/seckill-java.git

2. 安装

cd seckill-java
mvn install

3. 运行

mvn spring-boot:run
后端项目默认运行在 localhost:8081.

4. 打包和部署

mvn clean package
java -jar seckill-java-0.0.1-SNAPSHOT.jar

```

