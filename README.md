## E-Commerce(옷 쇼핑몰) 프로젝트
- 본 프로젝트는 3계층 아키텍처를 기반으로 설계 및 구현되었습니다.

## 개발 스택
- 언어 : Java(17)
- 프레임워크 : Spring Boot(3.3.4)
- ORM : Spring Data Jpa
- 데이터베이스 : MySQL(8.0.32)
- 테스트 : JUnit5, Mockito
- 스토리지 : AWS S3
- 컨테이너 : 도커

## ERD
<img width="640" alt="Screen Shot 2025-01-27 at 6 24 49 PM" src="https://github.com/user-attachments/assets/65f32b42-fc03-43ff-a758-fa7a530928af" />


## 작업 리스트
1. 상품 장바구니 담기 및 제거
2. 결제를 위한 PortOne 연결
3. 주문 
4. 상품 생성 및 제거
5. 상품 검색 및 필터링
6. AWS S3 버킷 생성 및 연결
7. 상품 생성 시 이미지 파일을 S3에 저장 후 fileKey를 데이터베이스 컬럼에 저장
8. 리뷰 작성
9. 사용자 회원가입 및 로그인
