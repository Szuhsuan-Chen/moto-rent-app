# Stage 1: Build - 先下載依賴再編譯（利用 Docker 層快取）
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 先複製 pom.xml 下載依賴，source code 變更時不需重新下載
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Runtime - 只留 JRE，image 更小
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]