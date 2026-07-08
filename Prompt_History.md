Lịch sử promt đã tương tác:

Bạn là Senior Java Spring Boot Developer với nhiều năm kinh nghiệm phát triển hệ thống E-Learning.

Hãy đọc toàn bộ Base Code hiện có của project và **mở rộng trực tiếp trên project**, không tạo project mới, không thay đổi package hiện có và không làm ảnh hưởng đến các chức năng đang hoạt động.

Project hiện đã có sẵn Entity **User** và **Course**.

Đã có tài liệu **SRS.md** ở thư mục gốc dự án. Hãy sử dụng tài liệu này làm nguồn yêu cầu nghiệp vụ duy nhất để triển khai.

## Yêu cầu 1: Bổ sung Entity

Tạo các Entity mới theo đúng thiết kế trong SRS.md:

* LearningPath
* LearningPathCourse
* UserCourseProgress

Sử dụng chuẩn JPA Annotation.

Yêu cầu:

* Mapping chính xác với Entity User và Course hiện có.
* Thiết kế quan hệ OneToMany, ManyToOne hoặc ManyToMany phù hợp.
* Không tạo trùng các Entity đã tồn tại.
* Nếu project đang dùng Lombok thì tiếp tục sử dụng.

---

## Yêu cầu 2: Repository

Tạo Repository Interface cho tất cả Entity mới.

Ví dụ:

* LearningPathRepository
* LearningPathCourseRepository
* UserCourseProgressRepository

Bổ sung các phương thức cần thiết để phục vụ nghiệp vụ, chẳng hạn:

* tìm Course theo Learning Path và Step
* kiểm tra Step đã tồn tại chưa
* kiểm tra Course đã thuộc Learning Path chưa
* tìm tiến độ học của User theo Course

---

## Yêu cầu 3: Service

Xây dựng Service theo đúng kiến trúc hiện có của project.

Triển khai đầy đủ nghiệp vụ cho các API.

Không viết pseudo-code.

Phải viết code Java hoàn chỉnh.

---

## Yêu cầu 4: RestController

Xây dựng các API sau.

### API 1 - Admin

Thêm Course vào Learning Path.

Cho phép chỉ định Step của Course trong Learning Path.

Ví dụ:

POST /api/admin/learning-path/{learningPathId}/courses

Request Body:

```json
{
  "courseId": 5,
  "stepOrder": 2
}
```

Yêu cầu:

* Kiểm tra Learning Path tồn tại.
* Kiểm tra Course tồn tại.
* Không cho phép trùng Step trong cùng Learning Path.
* Không cho phép cùng một Course xuất hiện nhiều lần trong cùng Learning Path.

---

### API 2 - Student

Đánh dấu hoàn thành một Course.

Ví dụ:

POST /api/student/courses/{courseId}/complete

Yêu cầu:

* Tạo hoặc cập nhật UserCourseProgress.
* completed = true.
* completedAt = thời gian hiện tại.

---

### API 3 - Student (API cốt lõi)

Truy cập một Course trong Learning Path.

Ví dụ:

GET /api/student/courses/{courseId}/access

Logic bắt buộc:

1. Kiểm tra Course có thuộc Learning Path hay không.
2. Nếu không thuộc Learning Path thì cho phép truy cập.
3. Nếu là Step 1 thì cho phép truy cập.
4. Nếu là Step N (>1):

    * Tìm Course ở Step N-1 trong cùng Learning Path.
    * Kiểm tra UserCourseProgress của User đối với Course ở Step trước.
5. Nếu chưa hoàn thành Step trước thì trả về HTTP 403 và thông báo phù hợp.
6. Nếu đã hoàn thành thì cho phép truy cập.

---

## Kết quả mong muốn

Sau khi hoàn thành:

1. Liệt kê toàn bộ file mới được tạo.
2. Liệt kê toàn bộ file được chỉnh sửa.
3. Sinh đầy đủ source code cho từng file.
4. Đảm bảo project compile thành công.
5. Không làm thay đổi các chức năng cũ của hệ thống.

## Yêu cầu bổ sung: Global Exception Handler

Dựa trên Base Code hiện có, hãy kiểm tra xem project đã có lớp **Global Exception Handler** (ví dụ sử dụng `@RestControllerAdvice` hoặc `@ControllerAdvice`) hay chưa.

### Trường hợp 1: Đã có Global Exception Handler

* Không tạo mới.
* Chỉ bổ sung các phương thức xử lý exception cần thiết.
* Không làm ảnh hưởng đến các exception đang hoạt động.

### Trường hợp 2: Chưa có Global Exception Handler

* Tạo mới một lớp Global Exception Handler theo chuẩn Spring Boot.
* Sử dụng `@RestControllerAdvice`.

### Exception mới

Tạo một custom exception phù hợp, ví dụ:

* `PrerequisiteNotCompletedException`

Exception này được sử dụng khi học viên cố truy cập một Course nhưng chưa hoàn thành Course ở Step trước trong cùng Learning Path.

### Xử lý bắt buộc

Khi `PrerequisiteNotCompletedException` được ném ra, Global Exception Handler phải trả về:

* HTTP Status: **403 Forbidden**
* Response Body dạng JSON rõ ràng.

Ví dụ:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Please complete the previous course before accessing this course.",
  "timestamp": "...",
  "path": "/api/student/courses/5/access"
}
```

### Service

Trong API kiểm tra điều kiện học:

* Không trả về HTTP 500.
* Khi chưa hoàn thành Step trước, phải ném `PrerequisiteNotCompletedException`.
* Không trả về `null`.
* Không bắt exception rồi bỏ qua.

### Yêu cầu cuối cùng

Đảm bảo:

* API truy cập Course trả về HTTP 403 khi chưa đủ điều kiện học.
* Không xuất hiện lỗi Internal Server Error (HTTP 500) đối với trường hợp này.
* Toàn bộ project vẫn compile thành công sau khi bổ sung.
