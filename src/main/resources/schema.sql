-- 用户表
CREATE TABLE IF NOT EXISTS "user" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(255) COMMENT '邮箱',
    name VARCHAR(100) COMMENT '姓名',
    role VARCHAR(50) DEFAULT 'user' COMMENT '角色：admin/user',
    last_login DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
);

-- 资产表
CREATE TABLE IF NOT EXISTS asset(
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(50) NOT NULL DEFAULT '' COMMENT '资产代码',
    name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '资产名称',
    currentPrice DOUBLE COMMENT '当前价格',
    changePercent DOUBLE COMMENT '涨跌幅百分比',
    marketValue BIGINT COMMENT '市值',
    industry VARCHAR(100) COMMENT '所属行业',
    position INT COMMENT '持仓数量',
    costPrice DOUBLE COMMENT '成本价',
    dailyGain DOUBLE COMMENT '当日盈亏',
    totalGain DOUBLE COMMENT '总盈亏',
    weight DOUBLE COMMENT '持仓权重',
    pe DOUBLE COMMENT '市盈率',
    pb DOUBLE COMMENT '市净率',
    dividendYield DOUBLE COMMENT '股息率',
    volatility DOUBLE COMMENT '波动率',
    beta DOUBLE COMMENT '贝塔系数',
    sharpeRatio DOUBLE COMMENT '夏普比率',
    maxDrawdown DOUBLE COMMENT '最大回撤',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_code (code),
    INDEX idx_industry (industry),
    INDEX idx_update_time (updateTime)
);

-- 风险事件表
CREATE TABLE IF NOT EXISTS risk_event(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    event_date DATE NOT NULL COMMENT '事件日期',
    type VARCHAR(100) NOT NULL COMMENT '风险类型',
    severity VARCHAR(50) NOT NULL COMMENT '严重程度：高/中/低',
    description VARCHAR(500) COMMENT '事件描述',
    impact VARCHAR(50) COMMENT '影响程度',
    status VARCHAR(50) DEFAULT '已处理' COMMENT '状态：已处理/处理中/未处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_event_date (event_date),
    INDEX idx_type (type)
);

-- 投资组合趋势表
CREATE TABLE IF NOT EXISTS portfolio_trend(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    date VARCHAR(20) NOT NULL COMMENT '日期（YYYY-MM格式）',
    portfolio DOUBLE COMMENT '投资组合价值',
    benchmark DOUBLE COMMENT '基准价值',
    volume BIGINT COMMENT '成交量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_date1 (date)
);

-- 月度收益表
CREATE TABLE IF NOT EXISTS monthly_return(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `month` VARCHAR(20) NOT NULL COMMENT '月份（如：1月）',
    returnRate DOUBLE COMMENT '收益率',
    benchmarkRate DOUBLE COMMENT '基准收益率',
    excessReturn DOUBLE COMMENT '超额收益',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_month (`month`)
);

-- 资产配置表
CREATE TABLE IF NOT EXISTS distribution_allocation(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category VARCHAR(100) NOT NULL COMMENT '资产类别',
    `value` DOUBLE COMMENT '配置比例',
    color VARCHAR(20) COMMENT '颜色代码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_category (category)
);

-- 历史配置表
CREATE TABLE IF NOT EXISTS distribution_historical(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    date VARCHAR(20) NOT NULL COMMENT '日期（YYYY-MM格式）',
    stock_percentage DOUBLE COMMENT '股票比例',
    bond_percentage DOUBLE COMMENT '债券比例',
    cash_percentage DOUBLE COMMENT '现金比例',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_date2 (date)
);

-- 回撤数据表
CREATE TABLE IF NOT EXISTS risk_drawdown(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    date DATE NOT NULL COMMENT '日期',
    price DOUBLE COMMENT '价格',
    drawdown DOUBLE COMMENT '回撤百分比',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_date3 (date)
)