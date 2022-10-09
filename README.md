# 유어슈 백엔드 과제 (2022 2학기)

## 엔드포인트
### 회원가입
> POST /signup

회원가입 요청을 보내는 엔드포인트입니다. 요청 양식은 아래와 같습니다.
```json
{
    "email": "이메일",
    "password": "비밀번호",
    "username": "사용자 이름"
}
```
응답 양식은 아래와 같습니다.
```json
{
"email" : "email@urssu.com",
"username" : "username"
}
```

### 회원 탈퇴
> DELETE /signout

회원탈퇴 요청을 보내는 엔드포인트입니다. 요청 양식은 아래와 같습니다.
```json
{
    "email": "이메일",
    "password": "비밀번호"
}
```
별도의 응답은 없습니다.

### 게시글 작성
> POST /article

게시글을 작성하는 엔드포인트입니다. 요청 양식은 아래와 같습니다.
```json
{
	"email": "이메일",
	"password": "비밀번호",
	"title": "게시글 제목",
	"content": "게시글 본문"
}
```
이메일과 비밀번호는 앞서 `/signup` 엔드포인트에서 가입한 계정의 이메일과 비밀번호여야 합니다.
`title`과 `content`는 공백이어선 안됩니다. ("", " ", null)

### 게시글 수정
> PUT /article/{articleId}

게시글을 수정하는 엔드포인트입니다.
articleId는 수정할 게시글의 id입니다.
요청 양식은 아래와 같습니다.
```json
{
  "email" : "이메일",
  "password" : "비밀번호",
  "title" : "게시글 제목",
  "content" : "게시글 본문"
}
```
이메일과 비밀번호는 이 게시글을 작성한 계정의 이메일과 비밀번호여야 합니다.
`title`과 `content`는 공백이어선 안됩니다. ("", " ", null)

### 게시글 삭제
> DELETE /article/{articleId}

게시글을 수정하는 엔드포인트입니다.
articleId는 삭제할 게시글의 id입니다.
요청 양식은 아래와 같습니다.
```json
{
  "email" : "이메일",
  "password" : "비밀번호"
}
```
이메일과 비밀번호는 이 게시글을 작성한 계정의 이메일과 비밀번호여야 합니다.

### 댓글 작성
> POST /comment/{articleId}

게시글에 댓글을 작성하는 엔드포인트입니다.
articleId는 댓글을 작성할 게시글의 id입니다.
요청 양식은 아래와 같습니다.
```json
{
  "email" : "이메일",
  "password" : "비밀번호",
  "content": "댓글 본문"
}
```
이메일과 비밀번호는 앞서 `/signup` 엔드포인트에서 가입한 계정의 이메일과 비밀번호여야 합니다.
`content`는 공백이어선 안됩니다. ("", " ", null)

### 댓글 수정
> PUT /comment/{articleId}/{commentId}

댓글을 수정하는 엔드포인트입니다.
articleId는 댓글을 작성할 게시글의 id입니다.
commentId는 수정할 댓글의 id입니다.
요청 양식은 아래와 같습니다.
```json
{
  "email" : "이메일",
  "password" : "비밀번호",
  "content": "댓글 본문"
}
```
이메일과 비밀번호는 이 댓글을 작성한 계정의 이메일과 비밀번호여야 합니다.
`content`는 공백이어선 안됩니다. ("", " ", null)

### 댓글 삭제
> DELETE /comment/{articleId}/{commentId}

댓글을 삭제하는 엔드포인트입니다.
articleId는 댓글을 작성할 게시글의 id입니다.
commentId는 삭제할 댓글의 id입니다.
요청 양식은 아래와 같습니다.
```json
{
  "email" : "이메일",
  "password" : "비밀번호"
}
```
이메일과 비밀번호는 이 댓글을 작성한 계정의 이메일과 비밀번호여야 합니다.
별도의 응답은 없습니다.
