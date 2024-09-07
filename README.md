# Back-end

빌드 방법

로컬에서 jar 파일 생성

    ./gradlew bootJar  
도커 이미지 빌드 arm 배포 할꺼라서 arm 환경에서 빌드함 또는 buildx로 arm 빌드함

    docker build -t kotlin-spring-app .
