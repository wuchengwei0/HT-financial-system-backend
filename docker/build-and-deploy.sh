#!/bin/bash

# 构建和部署脚本
set -e

echo "=========================================="
echo "开始构建和部署 Financial Backend"
echo "=========================================="

# 1. 清理旧的构建
echo "步骤 1: 清理旧的构建..."
mvn clean

# 2. 打包应用（跳过测试）
echo "步骤 2: 打包应用..."
mvn package -DskipTests

# 3. 复制最新的 jar 到 docker/jar 目录
echo "步骤 3: 复制 jar 文件到 docker/jar 目录..."
cp target/financial-backend.jar docker/jar/financial-backend.jar

# 4. 停止并删除旧容器
echo "步骤 4: 停止并删除旧容器..."
cd docker
docker-compose down || true

# 5. 重新构建 Docker 镜像
echo "步骤 5: 重新构建 Docker 镜像..."
docker-compose build --no-cache

# 6. 启动服务
echo "步骤 6: 启动服务..."
docker-compose up -d

# 7. 查看日志
echo "步骤 7: 查看服务日志..."
echo "等待 5 秒后显示日志..."
sleep 5
docker-compose logs -f financial-app

