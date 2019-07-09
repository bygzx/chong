package com.papa.util.controller;

/**
 * @author bygzx
 * @date 2019/5/13 10:29
 **/
public enum CommonResultCode {

    SUCCESS(10000, "OK"),
    USER_NO_EXIST(10001, "用户不存在"),
    INVALID_PARAM(10002 , "请求参数错误"),
    MISSING_PARAM(10003, "请求参数不全"),
    UPDATE_DATA(10004, "更新数据失败，具体信息详见msg字段"),
    NO_SESSION(10005, "对不起，当前Session失效,请重新登录"),
    IMAGE_UPLOAD(10006, "只支持上传jpg/gif/png格式文件"),
    CODE_MALL_EXIST(10006 , "商城已存在"),
    CODE_PRODUCT_EXIST(10013, "已新增过此课程"),
    CODE_BANNER_EXCEED(10014 , "已存在5条上架素材，无法上架新素材"),

    REQUEST_NO_AUTH(10101 , "该请求未授权无法获取用户信息"),
    CHECK_CODE(10102 , "验证码不正确，请核对后重试"),
    CHECK_EXPIRED(10103 , "验证码已过期，请重新获取"),
    CHECK_CODE_INVALID(10104 , "无效验证码，请重新获取"),
    BIND_PHONE(10105 , "绑定手机失败，稍后再试"),
    UPLOAD_FILE(10106 , "上传文件失败，稍后再试"),
    CHECK_CODE_SEND_ERROR(10107, "验证码下发失败，请稍后再试"),
    USER_EXIST(10108, "此账号已添加进入系统，请配置所需角色。"),
    USER_EXIST_KLIB(10108, "账号已存在"),
    LOGIN_ERROR(10109, "您的账号或密码有误，请重新输入"),
    ALREADY_REGISTER(10110, "该手机号已注册"),
    PHONE_ALREADY_EXISTS(10111, "该手机号已存在"),
    RESET_PASSWORD_ERROR(10112, "重置密码失败，请稍后再试"),
    CODE_PRODUCT_UPDATE_PERMISSION(10012, "无权限上架该课程"),

    ROLE_EXIST(10201, "当前角色已经存在"),
    NO_ACCOUNT(10202, "对不起，当前帐户不存在"),
    NO_PERMISSION(10203, "sorry，暂无权限~~"),
    REGISTER_ERROR(10204, "用户注册失败"),
    ACCOUNT263_ALREADY_EXISTS(10205, "该263账号已存在"),
    CODE_REGISTER_ERROR(10025, "注册服务异常，请稍后再试"),
    CODE_ALREADY_REGISTER(10023, "账号已存在，可直接登录"),
    CODE_WORRY_DATE(10050, "手机日期和服务器日期不匹配"),
    DO_NOT_RESUBIMT(10099, "请勿重复提交"),
    DO_NOT_RECEIVE_WELFARE(10210, "福利已经领取，请勿重复领取"),

    // H5商城
    CODE_USER_NO_EXIST(10801, "对不起，您没有访问该系统权限，如需访问请联系管理员"),
    CODE_NO_SESSION(10802, "对不起，当前Session失效,请重新登录"),
    CODE_PARAM(10002, "请求参数错误，具体信息详见msg字段"),
    CODE_263_NO_EXIST(10803, "该263账户名无效，请重新输入"),
    CODE_CHECK_PRICE(10808, "商品价格不符合系统要求"),
    CODE_USER_EXIST(10814, "此账号已经存在系统中"),
    CODE_BANNER_EXIST(10819, "该Banner素材名称已存在"),
    CODE_PRODUCT_NOT_FOUND(10824, "无法找到该商品信息"),
    CODE_LOGIN_ERROR(10826, "登录服务异常，请稍后再试"),
    CODE_INSURANCE_NOT_FOUND(10827, "无法找到该保险信息"),
    CODE_PARAM_ERROR(10828, "请求参数错误"),
    CODE_COUPON_NOT_FOUND(10829, "无法找到优惠券信息"),
    CODE_INSURANCE_AMOUNT_NOT_VALID(10830, "保险金额无法通过校验"),
    CODE_COUPON_AMOUNT_NOT_VALID(10831, "优惠券金额无法通过校验"),
    CODE_TOTAL_AMOUNT_NOT_VALID(10832, "订单总额无法通过校验"),
    CODE_TOTAL_AMOUNT_NOT_VALID_HAVE_EXIST(108321, "您购买的该课程订单已存在"),
    CODE_CREATE_ORDER_FAIL(10833, "创建订单失败，请重新再试"),
    CODE_CREATE_ORDER_FAIL_ONE(108331, "价格变动，请重新支付"),
    CODE_UPDATE_ORDER_FAIL(10834, "支付回调更新订单失败"),
    CODE_ORDER_NOT_FOUND(10835, "无法找到该订单信息"),
    CODE_PRODUCT_UNAVAILABLE(10836, "该商品已下架"),
    CODE_GET_GOODS_AGREEMENT_FAIL(10837, "获取服务协议失败"),
    CODE_MALL_IS_UNDER_CONSTRUCTION(10838, "商城正在建设中"),
    CODE_GET_USER_INFO_FAIL(10839, "获取用户信息失败"),
    CODE_GET_USER_GRADE_FAIL(10840, "获取用户等级信息失败"),
    CODE_LOAN_CODE_IS_INVALID(10041, "贷款券已过期/已失效，请选择其他贷款券"),
    NO_PAGE_SELECTED(10206,"请选择页面进行赋权"),
    CODE_QUERY_AVAILABLE_LOANS_FAIL(10042, "查询可用贷款券失败"),
    CODE_TOTAL_REAL_PAYMENT_NOT_VALID(10043, "实际支付金额不能低于产品包低价"),
    CODE_TOTAL_REAL_PAYMENT_NOT_EQUAL_PKGPRICE(10044, "实际支付金额加上优惠券不等于产品包价格"),
    CODE_TOTAL_REAL_PAYMENT_NOT_INSURANCE(10045, "实际支付金额低于产品包九折强制购买保险"),
    CODE_CANCEL_ORDER_FAIL(10846, "取消订单失败，请稍后再试"),
    CODE_QUERY_REMOTE_ORDER_FAIL(10848,"查询订单中心订单失败，请稍后再试"),
    CODE_CONFIRM_RIGHTS_PROTOCOL_FAIL(10847, "获取确认权益页失败，请稍后再试"),
    CODE_LOGIN_PASSWORD_ERROR(10849, "用户账号或密码错误，请重新再试"),
    //    // 考试相关
    EXAM_INVALID_TARGET(10301, "非法的考试目标"),
    EXAM_USER_TARGET_NOT_FOUND(10302, "用户未设置考试目标"),
    EXAM_SUBJECT_NOT_FOUND(10303, "找不到科目"),
    EXAM_SUBJECT_ALREADY_EXISTS(10304, "该科目已存在"),
    //
    //	// quiz相关
    CHAPTER_NOT_FOUND(10401, "找不到目标章节"),
    CHAPTER_NO_QUESTIONS(10402, "章节没有题目，不可练习"),
    QUIZ_NOT_FOUND(10403, "找不到目标练习"),
    QUIZ_ALREADY_COMPLETED(10404, "练习已结束"),
    QUIZ_INCOMPLETED(10405, "练习未完成"),
    QUESTION_NOT_FOUND(10406, "找不到题目"),
    PAPER_NOT_FOUND(10407, "找不到目标试卷"),
    PAPER_NO_QUESTIONS(10408, "试卷没有题目，不可练习"),
    QUESTION_COLLECT_DISABLED(10409, "此题暂不支持收藏（⊙.⊙）我们会抓紧修正问题~~"),
    CHAPTER_NO_FAVORITES(10410, "该章节没有收藏题目"),
    CHAPTER_NO_WRONG_SETS(10411, "该章节没有错题"),
    QUIZ_CREATE_TOO_FAST(10412, "练习创建太频繁"),
    //
    //    // 资讯相关
    INFORMATION_NOT_FOUND(10501, "找不到该资讯"),
    EXAM_REQUIRED_LIMITATION(10502, "考试必看类型最多可添加12个，当前已经有12个，您可以先保存为其它类型，再去列表进行调整"),
    //
    //    //tkt相关错误
    TKT_WORD_DUPLICATE(10601,"该单词已存在"),

    // 面试
    QUESTION_NOT_FAVORITE(10701, "题目未收藏"),
    FILE_MD5_NOT_MATCHED(10702, "DM5不一致"),

    //笔记本
    NOTE_NOT_VALID(10801, "笔记信息不存在"),
    //////////////////////
    //公开课小程序

    CODE_REQUEST_NO_AUTH(10003 , "该请求未授权无法获取用户信息"),
    CODE_USER_INFO(10005 , "无法获取用户信息"),
    CODE_NO_RESUME(10006 , "您还没有创建简历信息"),
    CODE_CHECK_CODE(10007 , "验证码不正确，请核对后重试"),
    CODE_CHECK_CODE_EXPIRED(10008 , "验证码已过期，请重新获取"),
    CODE_CHECK_CODE_INVALID(10009 , "无效验证码，请重新获取"),
    CODE_BIND_PHONE(10010 , "绑定手机失败，稍后再试"),
    CODE_UPLOAD_FILE(10011 , "上传文件失败，稍后再试"),
    CODE_COMPANY_NOT_EXIST(10012, "该公司不存在"),
    CODE_JOB_NOT_EXIST(10013, "职位不存在"),
    CODE_ROLE_EXIST(10015, "当前角色已经存在"),
    CODE_UPDATE_DATA(10016, "更新数据失败，具体信息详见msg字段"),
    CODE_NO_PERMISSION(10017, "对不起，您没有该请求权限"),
    CODE_NO_ACCOUNT(10018, "对不起，当前帐户不存在"),
    CODE_SCHOLARSHIP_FOUND(10019, "该助学金记录不存在"),
    CODE_COURSE_NOT_FOUND(10020, "该课程记录不存在"),
    CODE_WELFARE_NOT_FOUND(10021, "该公益记录不存在"),
    CODE_SCHOLARSHIP_ALREADY_APPLIED(10022, "已申请过该助学金"),
    CODE_ACTIVITY_NO_EXIST(10023, "该活动不存在"),
    CODE_ACTIVITY_NOT_BEGIN(10024, "该活动未开始"),
    CODE_ACTIVITY_HAS_END(10025 , "该活动已结束"),
    CODE_ACTIVITY_HAS_BARGAIN(10026 , "你已经砍过价了"),
    CODE_ACTIVITY_OVERLIMIT(10027 , "已经是最低价了，谢谢你的参与"),
    CODE_ACTIVITY_ORDER(10028 , "该优惠不是您的，无法下单"),
    CODE_ACTIVITY_ORDER_REPEAT(10029 , "重复下单"),
    CODE_ACTIVITY_ORDER_FAILUER(10030 , "重复下单失败"),
    CODE_ADD_COURSE_FAIL(10031, "新建课程失败"),
    CODE_UPDATE_COURSE_FAIL(10032, "更新课程失败"),
    CODE_UPDATE_COURSE_STATUS_FAIL(10033, "更新课程状态失败"),
    CODE_ADD_TEACHER_FAIL(10034, "新增讲师失败"),
    CODE_UPDATE_TEACHER_FAIL(10035, "更新讲师失败"),
    CODE_UPDATE_RECORD_STATUS_FAIL(10036, "更新录播课状态失败"),
    CODE_IMAGE_UPLOAD(10037, "只支持上传jpg/gif/png格式文件"),
    CODE_BIND_ORGANIZATION_FAIL(10039, "绑定军团失败"),
    CODE_COURSE_DETAIL_NOT_FOUND(10040, "无法找到该课程详情"),
    CODE_TEACHER_DETAIL_NOT_FOUND(10041, "无法找到该讲师详情"),
    CODE_NO_PROJECT(10042, "对不起，该项目不存在"),
    CODE_NO_SYLLABUS(10043, "对不起，课程存在"),
    CODE_SPREAD_HAS_BIND(10044, "用户已经进行推广绑定"),
    CODE_263ACCOUNT_ALREADY_EXISTS(10045, "该263账号已存在"),
    CODE_PHONE_ALREADY_EXISTS(10046, "该手机号已存在"),
    CODE_PHONE_NOT_EXISTS(100461, "用户openid下手机号不存在"),

    SET_COURSE_NOT_EXISTS(10047, "该套课不存或已下架"),
    CODE_CSV_UPLOAD(10048, "只支持上传csv格式文件"),
    CODE_POSTER_PARAM_NOT_FOUND(10049, "无法找到领取参数设置"),
    CODE_POSTER_NO_SYLLABUS(10051, "请选择课程后再生成海报"),
    CODE_POSTER_SYLLABUS_OVER_LIMIT(10052, "已选课程超出可选择课程上限"),
    CODE_POSTER_CREATE_ERROR(10053, "生成海报失败，请重试"),
    POSTER_QUOTA_MANAGER_ORG_EXISTS(10054, "当前军团已配置主管，无需重复配置"),
    POSTER_QUOTA_PENDING_APPROVAL_EXISTS(10055, "已有待审批申请，请联系主管处理"),
    POSTER_QUOTA_APPROVAL_MANAGER_NOT_FOUND(10056, "当前军团尚未配置主管，无法申请额度"),
    CODE_ORGANIZATION_NOT_FOUND(10057, "无法找到该军团"),
    CODE_REACH_POSTER_QUOTA_LIMIT(10058, "本周海报生成上限已满，请向主管申请额度"),
    CODE_NO_POSTER_QUOTA_APPROVAL_PERMISSION(10059, "您不是主管，无法审核额度申请记录"),
    POSTER_QUOTA_MANAGER_EXISTS(10060, "当前军团已有主管，无法配置多个主管"),
    POSTER_QUOTA_LIMIT(10061, "您申请的额度超过主管审核额度"),
    CODE_ERROR(99999, "系统异常，具体信息详见msg字段"),

    //游戏兑换码
    CODE_NO_PHOME_PRIZE(12001, "该手机号没有待领奖品"),
    CODE_ALREADY_GOT_PRIZE(12001, "该手机号已兑换了奖品"),

    // 企业大学
    NO_ENTERPRISE_COURSE_AUTH(10901, "您不是企业用户，无法查看企业课程"),
    ENTERPRISE_SATUS_INVALID(10902, "该企业运营管理系统已停用，请联系尚德机构相关人员"),

    //////////////////////
    SYS_ERROR(99999, "服务异常，请稍后再试"),

    // 以下错误代码用以在监控平台对异常分类
    SYS_RUNTIME_ERROR(20001,"[服务器]运行时异常"),
    SYS_NULLPOINT_ERROR(20002,"[服务器]空值异常"),
    SYS_CLASSCAST_ERROR(20003,"[服务器]类型转换异常"),
    SYS_IO_ERROR(20004,"[服务器]IO异常"),
    SYS_NOSUCHMETHOD_ERROR(20005,"[服务器]未知方法异常"),
    SYS_INDEXOUTOFBOUNDS_ERROR(20006,"[服务器]数组越界异常"),
    SYS_SQL_ERROR(20007,"[服务器]SQL异常"),
    SYS_HTTP_MESSAGE_NOT_READABLE_ERROR(20008,"[服务器]requestNotReadable异常"),
    SYS_REQUEST_TYPE_MISMATCH_ERROR(20009,"[服务器]TypeMismatch异常"),
    SYS_MISSING_SERVELET_REQUEST_ERROR(200010,"[服务器]MissingServletRequest异常"),
    SYS_METHOD_NOT_SUPPORTED_ERROR(200011,"[服务器]MethodNotSupported异常"),
    SYS_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_ERROR(200012,"[服务器]HttpMediaTypeNotAcceptable异常"),
    SYS_SERVER_500_ERROR(200013,"[服务器]HttpMessageNotWritable/ConversionNotSupported异常"),
    SYS_BUSINESS_ERROR(200014,"[服务器]业务异常"),
    SYS_REDIS_ERROR(200015,"[服务器]redis异常"),
    SYS_INTERFACE_ERROR(200016,"[服务器]接口异常"),
    SYS_EXCEPTION_ERROR(200017,"[服务器]异常"),

    // 以下错误代码用以在监控平台对异常分类
    GW_RUNTIME_ERROR(4000,"[网关]运行时异常"),
    GW_TOKEN_NOT_FOUND_ERROR(4002,"[网关]缺少token头信息"),
    GW_TOKEN_INVALID_ERROR(4003,"[网关]token 无效"),
    GW_PRE_CHOOSE_NO_USABLE_TARGET(4004,"[路由]从上下文无法找到匹配的可用的路由转发信息"),
    GW_COUNT_LIMIT(4005,"[网关]一分钟接口调用次数超过最大限制"),
    //PreChooseTargetFilter not found usable target
    GW_IP_BLACK_REFUSE_ERROR(4006,"[网关]IP黑名单访问被禁止～"),
    GW_DEGRADE_SUCCESS(10000, "Degrade OK"),


    NTCE_MP_SCORE_ERROR(222222, "爬虫错误异常"),

    //
    param_INVALID(600000, "参数invalid"),
    OPENID_INVALID(600000, "无法找到OPENID信息"),
    PARAM_PRIVILEGE_INVALID(100100, "您选择的日期，覆盖军团不能使用8折以下的优惠券。"),
    PARAM_PRIVILEGE_KAQI(100101, "您选择的日期，覆盖军团设置需要跨期检查。"),
    FETCH_COUPON_FAILUER(100102, "领取优惠券失败。");


    public final int code;
    public final String msg;

    CommonResultCode(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

}
