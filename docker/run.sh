# 1本地启动
# 清理并编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package -DskipTests

# 运行应用
java -jar target/test.jar


# 2 Docker运行
# 1. 编译项目
mvn clean package

# 2. 构建Docker镜像
docker build -t springboot-scaffold:1.0.0 .

# 3. 运行容器
docker run -d \
  -p 8080:8080 \
  --name scaffold-app \
  springboot-scaffold:1.0.0

# 或者使用docker-compose（创建docker-compose.yml文件）


###########################################
# Knife4j文档界面：http://localhost:8080/api/doc.html
# 原生Swagger UI：http://localhost:8080/api/swagger-ui/index.html
# OpenAPI JSON文档：http://localhost:8080/api/v3/api-docs
# 分组API文档：http://localhost:8080/api/v3/api-docs/public-api
# 健康检查接口：http://localhost:8080/api/test/health
###########################################

