# 1. 실제 실행 단계 (경량화된 JDK 사용)
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 로컬에서 복사 (이미 로컬에서 빌드된 JAR 파일 사용)
COPY build/libs/*.jar app.jar

COPY .env .env

# 4. 실행 시 환경 변수 로드 (dotenv 사용)
ENV DOTENV_CONFIG_PATH=/app/.env

# 5. JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]