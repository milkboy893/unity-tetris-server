# 1. ビルド用の環境（Java 17）
FROM eclipse-temurin:17-jdk-jammy AS build
COPY . .
# 実行権限を与えてビルド（JARファイル作成）
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# 2. 実行用の軽量環境
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar app.jar
# サーバー起動！
ENTRYPOINT ["java", "-jar", "/app.jar"]