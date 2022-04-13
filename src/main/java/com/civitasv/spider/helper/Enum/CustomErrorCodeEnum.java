package com.civitasv.spider.helper.Enum;

public enum CustomErrorCodeEnum {

    // 自定义Error
    KEY_POOL_RUN_OUT_OF(90001, "key池耗尽","请更换新的key，或暂停任务，第二天再尝试爬取poi"),

    // 高德Error
    OK(10000,"请求正常", "请求正常"),
    INVALID_USER_KEY(10001,"key不正确或过期", "开发者发起请求时，传入的key不正确或者过期 "),
    SERVICE_NOT_AVAILABLE(10002,"没有权限使用相应的服务或者请求接口的路径拼写错误", "1.开发者没有权限使用相应的服务，例如：开发者申请了WEB定位功能的key，却使用该key访问逆地理编码功能时，就会返回该错误。反之亦然。2.开发者请求接口的路径拼写错误。例如：正确的https://restapi.amap.com/v3/ip在程序中被拼装写了https://restapi.amap.com/vv3/ip"),
    DAILY_QUERY_OVER_LIMIT(10003,"访问已超出日访问量", "开发者的日访问量超限，被系统自动封停，第二天0:00会自动解封。"),
    ACCESS_TOO_FREQUENT(10004,"单位时间内访问过于频繁", "开发者的单位时间内（1分钟）访问量超限，被系统自动封停，下一分钟自动解封。"),
    INVALID_USER_IP(10005,"IP白名单出错，发送请求的服务器IP不在IP白名单内", "开发者在LBS官网控制台设置的IP白名单不正确。白名单中未添加对应服务器的出口IP。可到 控制台 > 配置  中设定IP白名单。"),
    INVALID_USER_DOMAIN(10006,"绑定域名无效", "开发者绑定的域名无效，需要在官网控制台重新设置"),
    INVALID_USER_SIGNATURE(10007,"数字签名未通过验证", "开发者签名未通过开发者在key控制台中，开启了“数字签名”功能，但没有按照指定算法生成“数字签名”。"),
    INVALID_USER_SCODE(10008,"MD5安全码未通过验证", "需要开发者判定key绑定的SHA1,package是否与sdk包里的一致"),
    USERKEY_PLAT_NOMATCH(10009,"请求key与绑定平台不符", "请求中使用的key与绑定平台不符，例如：开发者申请的是js api的key，却用来调web服务接口"),
    IP_QUERY_OVER_LIMIT(10010,"IP访问超限", "未设定IP白名单的开发者使用key发起请求，从单个IP向服务器发送的请求次数超出限制，被系统自动封停。封停后无法自动恢复，需要提交工单联系我们。"),
    NOT_SUPPORT_HTTPS(10011,"服务不支持https请求", "服务不支持https请求，如果需要申请支持，请提交工单联系我们"),
    INSUFFICIENT_PRIVILEGES(10012,"权限不足，服务请求被拒绝", "由于不具备请求该服务的权限，所以服务被拒绝。"),
    USER_KEY_RECYCLED(10013,"Key被删除", "开发者删除了key，key被删除后无法正常使用"),
    QPS_HAS_EXCEEDED_THE_LIMIT(10014,"云图服务QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    GATEWAY_TIMEOUT(10015,"受单机QPS限流限制", "受单机QPS限流限制时出现该问题，建议降低请求的QPS或在控制台提工单联系我们"),
    SERVER_IS_BUSY(10016,"服务器负载过高", "服务器负载过高，请稍后再试"),
    RESOURCE_UNAVAILABLE(10017,"所请求的资源不可用", "所请求的资源不可用"),
    CQPS_HAS_EXCEEDED_THE_LIMIT(10019,"使用的某个服务总QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    CKQPS_HAS_EXCEEDED_THE_LIMIT(10020,"某个Key使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    CUQPS_HAS_EXCEEDED_THE_LIMIT (10021,"账号使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    INVALID_REQUEST(10026,"账号处于被封禁状态", "由于违规行为账号被封禁不可用，如有异议请登录控制台提交工单进行申诉"),
    ABROAD_DAILY_QUERY_OVER_LIMIT(10029,"某个Key的QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    NO_EFFECTIVE_INTERFACE(10041,"请求的接口权限过期", "开发者发起请求时，请求的接口权限过期。请提交工单联系我们"),
    USER_DAILY_QUERY_OVER_LIMIT(10044,"账号维度日调用量超出限制", "账号维度日调用量超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    USER_ABROAD_DAILY_QUERY_OVER_LIMIT(10045,"账号维度海外服务日调用量超出限制", "账号维度海外服务接口日调用量超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    INVALID_PARAMS(20000,"请求参数非法", "请求参数的值没有按照规范要求填写。例如，某参数值域范围为[1,3],开发者误填了’4’"),
    MISSING_REQUIRED_PARAMS(20001,"缺少必填参数", "缺少接口中要求的必填参数"),
    ILLEGAL_REQUEST(20002,"请求协议非法", "请求协议非法，比如某接口仅支持get请求，结果用了POST方式"),
    UNKNOWN_ERROR(20003,"其他未知错误", "其他未知错误"),
    INSUFFICIENT_ABROAD_PRIVILEGES(20011,"查询坐标或规划点（包括起点、终点、途经点）在海外，但没有海外地图权限", "使用逆地理编码接口、输入提示接口、周边搜索接口、路径规划接口时可能出现该问题，规划点（包括起点、终点、途经点）不在中国陆地范围内"),
    ILLEGAL_CONTENT(20012,"查询信息存在非法内容", "使用搜索接口时可能出现该问题，通常是由于查询内容非法导致"),
    OUT_OF_SERVICE(20800,"规划点（包括起点、终点、途经点）不在中国陆地范围内", "使用路径规划服务接口时可能出现该问题，规划点（包括起点、终点、途经点）不在中国陆地范围内"),
    NO_ROADS_NEARBY(20801,"划点（起点、终点、途经点）附近搜不到路", "使用路径规划服务接口时可能出现该问题，划点（起点、终点、途经点）附近搜不到路"),
    ROUTE_FAIL(20802,"路线计算失败，通常是由于道路连通关系导致", "使用路径规划服务接口时可能出现该问题，路线计算失败，通常是由于道路连通关系导致"),
    OVER_DIRECTION_RANGE(20803,"起点终点距离过长。", "使用路径规划服务接口时可能出现该问题，路线计算失败，通常是由于道路起点和终点距离过长导致。"),
    ENGINE_RESPONSE_DATA_ERROR(300,"服务响应失败。", "出现3开头的错误码，建议先检查传入参数是否正确，若无法解决，请详细描述错误复现信息，提工单给我们。（大数据接口请直接跟负责商务反馈）如，30001、30002、30003、32000、32001、32002、32003、32200、32201、32202、32203"),
    QUOTA_PLAN_RUN_OUT(40000,"余额耗尽", "所购买服务的余额耗尽，无法继续使用服务"),
    GEOFENCE_MAX_COUNT_REACHED(40001,"围栏个数达到上限", "Key可创建的地理围栏的数量，已达上限。"),
    SERVICE_EXPIRED(40002,"购买服务到期", "所购买的服务期限已到，无法继续使用"),
    ABROAD_QUOTA_PLAN_RUN_OUT(40003,"海外服务余额耗尽", "所购买服务的海外余额耗尽，无法继续使用服务");


    private final Integer code;
    private final String description;
    private final String helpinfo;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getHelpinfo() {
        return helpinfo;
    }

    CustomErrorCodeEnum(Integer code, String description, String helpinfo) {
        this.code = code;
        this.description = description;
        this.helpinfo = helpinfo;
    }

    public static CustomErrorCodeEnum getBoundryType(String description){
        for (CustomErrorCodeEnum value : CustomErrorCodeEnum.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static CustomErrorCodeEnum getBoundryType(Integer code){
        for (CustomErrorCodeEnum value : CustomErrorCodeEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        // 300错误统一处理
        if(code / 100 == 300){
            return ENGINE_RESPONSE_DATA_ERROR;
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
