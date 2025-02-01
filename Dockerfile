##############################
# 1단계: 빌드 단계 (Gradle)
##############################
FROM gradle:7.6-jdk17 AS builder
WORKDIR /home/gradle/project

COPY --chown=gradle:gradle . .

# Gradle을 이용해 애플리케이션 빌드
RUN gradle clean build

##############################
# 2단계: 런타임 단계 (Amazon Corretto 17)
##############################
FROM amazoncorretto:17-alpine
WORKDIR /app

# 빌드 단계에서 생성된 실행 가능한 JAR 파일을 복사
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
