---
layout: default
title: "Welcome to POIKit!"
date: 2022-07-09
---

[![build](https://img.shields.io/badge/build-success-brightgreen)](https://github.com/Civitasv/AMapPoi)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/Civitasv/AMapPoi/graphs/commit-activity)
[![version](https://img.shields.io/badge/dynamic/json?color=brightgreen&label=version&query=tag_name&url=https%3A%2F%2Fapi.github.com%2Frepos%2FCivitasv%2FAMapPoi%2Freleases%2Flatest)](https://github.com/Civitasv/AMapPoi/releases/latest)

POIKit 用于提供一套**简单**、**易用**、**稳定**的 POI 获取与处理工具套件，方便相关从业者。

功能：

- POI 搜索 (支持多线程并发)
- 地理编码 (支持多线程并发)
- 格式转换 (目前可转换 geojson -> shp 、shp -> geojson/csv)
- 坐标转换 (支持 wgs84/gcj02/bd09)

目前软件处于**2.0 版本**，在 1.0 版本的基础上，增加了**断点续爬**功能，大幅优化了 POI 数据的获取速度。

希望各位多多尝试，多多提问题（The More Questions，The Better）。

若帮助到了您，点击 [Github Star](https://github.com/Civitasv/AMapPoi) 就是对我们最大的肯定。

## 2.0 版本：支持 POI 断点续爬

相较于 1.0 版本，2.0 版本支持继续完成未执行完的任务，防止因为 key 的配额耗尽导致程序中断。

1. 支持切换 key，继续上一未完成任务
2. 若无 key 可切换，支持第二天 key 重置后，继续上一未完成任务
3. 切分阶段支持多线程 :smile:

## 目录

- [快速开始](#快速开始)
- [功能演示](#功能演示)
- [技术选型](#技术选型)
- [维护人员](#维护人员)
- [支持该项目](#支持该项目)
- [开发路线](#开发路线)
- [License](#License)

## Preview

![preview](image/preview.png)

## 快速开始

### 1. 配置环境

软件基于 Java 环境运行，需要首先安装 jre/jdk（1.8 版本），安装步骤如下：

- 下载[JDK8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)，选择适合本系统的版本；
- 配置环境变量`JAVA_HOME`为安装目录，然后在`Path`中添加`%JAVA_HOME%\bin`；
- 打开终端，输入`java -version`，若出现 Java 版本号为 1.8，则配置成功。

**软件启动不了，99%是 Java 版本错误（必须使用 java1.8）或环境变量未正确设置的问题。**

安装教程：[【晓时代】windows 安装 Java8 及环境变量配置](https://www.jianshu.com/p/1d834fcf5c44)

常见错误：

java 版本错误：高版本不会自带 javafx 库，**版本必须是 java1.8**。

![First Example](image/error1.jpg)

### 2. 启动 POIKit

[下载](https://github.com/Civitasv/AMapPoi/releases)最新发布的软件压缩包，以`POIKit.zip`为例，解压缩后，双击`start.bat`即可运行。linux 和 mac 用户可以使用 start.sh 启动。

### 3. 申请 Key

目前只支持高德 key，请前往[高德控制台](https://console.amap.com/dev/index)获申请**Web 服务**类型的 key。

### 4. POI 搜索功能

以行政区为例，POI 搜索功能如下所示：

#### 4.1. 小批量数据

类型：050000；行政区：371723；阈值：850；线程数目：20；输出格式：geojson。

![First Example](image/first.gif)

#### 4.2. 大批量数据

类型：010000；行政区：110000；阈值：850；线程数目：20；输出格式：csv。

![Second Example](image/second.gif)

#### 4.3. 断点续爬

类型：010000；行政区：110000；阈值：850；线程数目：20；输出格式：csv。

![Third Example](image/third.gif)

软件重启之后，会弹出弹窗提示是否继续执行上次任务。

**继续爬取之前，用户可以更改高德 key，用户类型以及线程数目**。

![retry](image/retry.png)

### 5. 地理编码

允许 CSV 或 TXT 格式文件，必须至少指定 address 值。

![geocoding](image/geocoding.gif)

### 6. 格式转换

![transform_1](image/pattern_transform_1.gif)

### 7. 坐标转换

![transform_2](image/coordinate_transform.gif)

### 8. 版本更新

当无版本更新时：

![update](image/autoupdate.gif)

当有版本更新时：

![update2](image/autoupdate2.gif)

## 详细说明

### POI 搜索

**功能配置参数如下表：**

| 参数       | 说明                                                                                                                                                  | 注意                                                                                                                |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| 高德 key   | 软件支持多个 key，不同 key 之间需要用逗号分割                                                                                                         | 使用英文逗号分割                                                                                                    |
| 开发者类型 | 个人开发者/个人认证开发者/企业开发者                                                                                                                  | 用于设置 QPS 值，当用户设置线程数大于最大线程数，将强制设为最大线程，防止过高并发                                   |
| POI 关键字 | 搜索关键字，如 KFC，不同关键字之间使用逗号分割                                                                                                        | 使用英文逗号分割                                                                                                    |
| POI 类型   | 搜索类型，可为分类代码或汉字，如 010000，不同类型之间使用逗号分割                                                                                     | 英文逗号分割，若使用汉字，必须严格按照[高德 POI 分类编码](https://lbs.amap.com/api/webservice/download)中的汉字编写 |
| 行政区     | [行政区六位代码](http://www.mca.gov.cn//article/sj/xzqh/2020/202006/202008310601.shtml)                                                               |                                                                                                                     |
| 矩形       | 格式严格遵循左上角经纬度#右下角经纬度，如 133,34#135,30                                                                                               | 经纬度坐标可以使用 wgs84/gcj02/bd09 坐标，请使用下拉框选择合适的经纬度坐标                                          |
| 自定义     | 支持用户上传 geojson 边界文件                                                                                                                         | 经纬度坐标可以使用 wgs84/gcj02/bd09 坐标，类型可以为 Polygon 或 MultiPolygon                                        |
| 初始网格数 | 初始网格剖分数目                                                                                                                                      | 一般情况按默认值为 4 即可                                                                                           |
| 阈值       | 当该网格 POI 数量超出阈值，会对网格进一步四分                                                                                                         | 一般情况下按 850 即可                                                                                               |
| 线程数目   | 线程数量一般不大于 QPS \* keys_num。对于单个 key，个人开发者最多设为 20，个人认证开发者最多设为 50。如果爬取过程中发生 QPS 超限错误，建议降低线程数。 | QPS 可以在[流量限制说明](https://lbs.amap.com/api/webservice/guide/tools/flowlevel)查看                             |
| 输出格式   | 目前支持 geojson、shp、csv、txt                                                                                                                       | 结果包含 gcj02 和 wgs84 两种坐标，若输出格式为 geojson 或 shp，默认使用 wgs84 坐标                                  |

**输出参数说明：**

|   参数    |       说明       |
| :-------: | :--------------: |
|   name    |       名称       |
|   type    |    兴趣点类型    |
| typecode  |  兴趣点类型编码  |
|  address  |       地址       |
|   pname   | POI 所在省份名称 |
| cityname  |      城市名      |
|  adname   |     区域名称     |
| gcj02_lon |    gcj02 经度    |
| gcj02_lat |    gcj02 纬度    |
| wgs84_lon |    wgs84 经度    |
| wgs84_lat |    wgs84 纬度    |

### 地理编码

**功能配置参数如下表所示：**

|    参数    |                                           说明                                            |                                          注意                                           |
| :--------: | :---------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------: |
|  高德 key  |                       软件支持多个 key，不同 key 之间需要用逗号分割                       |                                    使用英文逗号分隔                                     |
| 开发者类型 |                          个人开发者或个人认证开发者或企业开发者                           |    用于设置 QPS 值，当用户设置线程数大于最大线程数，将强制设为最大线程，防止过高并发    |
|  线程数目  | 线程数量一般不大于 QPS \* keys_num（例如单个认证开发者 key，线程数小于等于 50 = 1 \* 50） | [QPS 可以在流量限制说明查看](https://lbs.amap.com/api/webservice/guide/tools/flowlevel) |
|  输入文件  |                                 支持 csv 或 txt 格式文件                                  |                                至少需要包含 address 字段                                |
|  输出目录  |                结果输出路径，目前地理编码结果包括 gcj02 和 wgs84 两种坐标                 |                                            -                                            |

**注意：**

1. **若地理编码过程中 key 池额度用尽，软件会停止地理编码，但不会删除之前得到的数据，仍会导出。**
2. **若地理编码过程中，用户点击取消，软件会停止地理编码，不会导出。**

**输出参数说明：**

|       参数        |       说明       |
| :---------------: | :--------------: |
| formatted_address |  结构化地址信息  |
|      country      |       国家       |
|     province      | 地址所在的省份名 |
|       city        | 地址所在的城市名 |
|     citycode      |     城市编码     |
|     district      |   地址所在的区   |
|      adcode       |     区域编码     |
|      street       |       街道       |
|      number       |       门牌       |
|       level       |     匹配级别     |
|     gcj02_lon     |    gcj02 经度    |
|     gcj02_lat     |    gcj02 纬度    |
|     wgs84_lon     |    wgs84 经度    |
|     wgs84_lat     |    wgs84 纬度    |

### 格式转换

**功能配置参数如下表所示：**

|   参数   |                                 说明                                  |
| :------: | :-------------------------------------------------------------------: |
| 输入文件 |                     支持 geojson 或 shp 格式文件                      |
| 输出格式 | 若选择 geojson，则可以输出 shp，若选择 shp，则可以输出 geojson 或 csv |
| 输出目录 |                             结果输出路径                              |

### 坐标转换

**功能配置参数如下表所示：**

|     参数     |                  说明                  |
| :----------: | :------------------------------------: |
|   输入文件   |      支持 geojson 或 shp 格式文件      |
| 输入坐标格式 | 即输入文件的坐标格式，wgs84/gcj02/bd09 |
|   输出目录   |              结果输出路径              |
| 输入坐标格式 | 即输出文件的坐标格式，wgs84/gcj02/bd09 |

## 技术选型

目前软件采用 MVC 软件架构模式，技术选型如下：

- GUI：[JavaFX 8](https://openjfx.io/)；
- HTTP 请求：[Retrofit](https://square.github.io/retrofit/)
- Json 转换：[Gson](https://github.com/google/gson)
- 空间数据处理：[GeoTools](https://geotools.org/)
- 数据库访问：[MybatisPlus](https://baomidou.com/)
- 数据库：[Sqlite](https://www.sqlite.org/index.html)

## 维护人员

[@Civitasv](https://github.com/Civitasv)

[@SkyTreeDelivery](https://github.com/SkyTreeDelivery)

## 支持该项目

若遇到任何问题，你可以通过以下方式联系我们：

1. 邮箱：hscivitasv@gmail.com，我们会定时查看邮箱，但不保证实时性；
2. 用户 QQ 群：1097532420
3. 提 [issue](https://github.com/Civitasv/AMapPoi/issues)：这是我们推荐的方式，有问题时，也应该首先查看 issue 列表是否已有该问题的解答；

若帮助到了您，[Github Star](https://github.com/Civitasv/AMapPoi) 是对我们最大的肯定。

## 开发路线

- **v2.0.0** 2022-04-23
  - **【重要更新】poi 爬取支持断点续爬，key 的配额耗尽后，可以隔天继续任务，或替换其他 key**
  - **【重要更新】poi 爬取的全过程支持多线程执行，大幅度优化爬取速度**
  - **【重要更新】增加新版本更新提示功能**
  - UI 界面优化：增加若干提示；优化参数的输入体验；优化 Alert 界面的按钮样式；
  - 优化结果输出格式，提供更多信息，包括任务整体执行状态、与错误代码对应的中文帮助等
  - 若干 bug 修复
- **v1.0.1** 2022-03-19
  - UI 美化
  - 添加失败文件功能，一定程度上确保能够爬取更多的数据，且防止卡死
- **v1.0.0** 2022-03-17
  - 增加超时
- **v0.0.5** 2021-04-27
  - 行政区、用户自定义文件爬取得到的 POI 不再是其外接矩形的 POI，即用户不需要再做裁切处理；
  - 完成坐标转换工具开发，进行 wgs84、gcj02 和 bd09 坐标之间的转换，文件格式支持 geojson/shp，其中 geojson 支持
    Point、MultiPoint、LineString、MultiLineString、Polygon 和 MultiPolygon，不支持 GeometryCollection；
  - **使用不同的线程控制机制，能够增加线程数目，更加快速的获取 POI**；
  - UI bug 修复、cpg 文件生成修复等。
- **v0.0.4** 2021-04-26
  - 添加选取坐标类型（gcj02/wgs84）功能
  - 初步完成 geojson 转 shp，shp 转 geojson，shp 转 csv
  - 添加 cpg 格式文件，防止乱码
  - 若干 bug 修复
- **v0.0.4-alpha** 2021-04-23
  - 添加 POI 检索[错误码](https://lbs.amap.com/api/webservice/guide/tools/info)输出；
  - 修改地理编码返回字段;
  - 添加开发者类型选择下拉框，防止过高并发；
  - 解决点击“执行按钮”卡顿 bug;
  - **【重要更新】POI 搜索添加导出 shp 功能**
- **v0.0.3** 2021-04-23
  - 线程池运行优化；
  - 添加运行状态提示
- **v0.0.2** 2021-04-22
  - 修复重复 bug；
- **v0.0.1** 2021-04-20
  - 初步实现软件及安装文档；

## License

[GPL-3.0 License](https://www.gnu.org/licenses/gpl-3.0.html) © Civitasv
