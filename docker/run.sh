##########################RUN

# 重新构建&启动
cd docker
docker-compose down
docker-compose up -d


# dockerfile 和 jar 放到同级目录上
# 1. 在包含 Dockerfile 和 financial-backend.jar 的目录中执行
docker build -t financial-backend:latest .

# 2. 运行容器并设置开机自启动
# docker run -d --name financial-app--restart=always -p 8080:8080 -e JAVA_OPTS="-Xmx1g -Xms512m" financial-backend:latest
docker run -d \
  --name financial-app \
  --restart=always \
  -p 8080:8080 \
  financial-backend:latest


##########################Stop & Kill
# 停止并删除容器
docker rm -f financial-app
# 删除指定镜像
docker rmi financial-backend:latest

#################V1


###########################################
# Knife4j文档界面：http://localhost:8080/api/doc.html
# 原生Swagger UI：http://localhost:8080/api/swagger-ui/index.html
# OpenAPI JSON文档：http://localhost:8080/api/v3/api-docs
# 分组API文档：http://localhost:8080/api/v3/api-docs/public-api
# 健康检查接口：http://localhost:8080/api/test/health
###########################################

