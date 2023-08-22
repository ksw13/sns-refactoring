# 이미지 이름 : 태그
FROM openjdk:11
# 이미지 빌드 시점에서 사용할 변수 지정
ARG JAR_FILE=build/libs/app.jar
# 호스트에 있는 파일을 Docker 이미지의 파일 시스템으로 복사
COPY ${JAR_FILE} ./app.jar
# 환경 변수 지정
ENV TZ=Asia/Seoul
# 컨테이너가 실행될 때 항상 실행되어야 하는 커멘드 지정
ENTRYPOINT ["java","-jar","./app.jar"]