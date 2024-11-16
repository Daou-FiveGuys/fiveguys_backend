## AWS 적용 방법

### 1. Git Pull로 코드 업데이트
```sh
cd ~/fiveguys_backend/
git pull origin
```

### 2. Docker Compose 중지 및 컨테이너 제거
```sh
docker-compose down
```

### 3. Docker 시스템 정리 (불필요한 데이터 제거)
```sh
docker system prune -a
```

### 4. 프로젝트 빌드
```sh
./gradlew build
```

### 5. Docker 이미지 생성
```sh
docker build -t fiveguys_backend .
```

### 6. Docker Compose 실행
```sh
docker-compose up -d
```

### 7. Docker 시스템 정리 (불필요한 데이터 제거) 
```sh
docker system prune -a
```

### 8. 빌드 파일 제거 
```sh
rm -rf ./build
```

### 9. 배포 상태 확인
```sh
#로그 확인
docker-compose logs -f

#컨테이너 목록 확인
docker container ls -al
```
