# X Clone Social App Backend (Spring Boot + MongoDB)

A social media backend for football fans with:
- User registration & login (JWT)
- Posts, Likes, Comments
- Follow/Unfollow
- Search users
- Recommended users to follow
- Image Uploads via MultipartFile
- MongoDB persistence

## API Base URL
http://localhost:8080/api/

## Features

### Authentication
POST /api/auth/login  
POST /api/users/register  

### Posts
POST /api/posts/create  
PATCH /api/posts/edit/{postId}  
DELETE /api/posts/{postId}  
POST /api/posts/like/{postId}  
POST /api/posts/comment/{postId}  

### Users
POST /api/users/follow  
POST /api/users/unfollow  
GET  /api/users/search?query=  
GET  /api/users/recommend/{userId}

### Media Upload
POST /api/media/upload
